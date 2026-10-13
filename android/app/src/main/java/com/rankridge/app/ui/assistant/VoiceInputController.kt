package com.rankridge.app.ui.assistant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.DisposableEffect

/**
 * Thin wrapper around Android's on-device [SpeechRecognizer]. This is a real OS feature, not a
 * backend: speech is transcribed on the device (we request [RecognizerIntent.EXTRA_PREFER_OFFLINE]
 * where supported) and streamed back as partial text the user can edit before sending.
 *
 * The recognition engine is the phone's default and varies by manufacturer, so callers must handle
 * [onError] gracefully and always leave a typed fallback available.
 */
class VoiceInputController(
    private val context: Context,
    private val onPartial: (String) -> Unit,
    private val onFinal: (String) -> Unit,
    private val onStateChange: (listening: Boolean) -> Unit,
    private val onError: (message: String) -> Unit,
) {
    private var recognizer: SpeechRecognizer? = null

    /** Whether this device has any recognition service at all. */
    fun isAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    fun start() {
        if (!isAvailable()) {
            onError("Voice input isn't available on this device.")
            return
        }
        // Recreate per session: some OEM engines misbehave when a single instance is reused.
        recognizer?.destroy()
        val sr = SpeechRecognizer.createSpeechRecognizer(context)
        sr.setRecognitionListener(listener)
        recognizer = sr

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
            )
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        }
        onStateChange(true)
        sr.startListening(intent)
    }

    fun stop() {
        recognizer?.stopListening()
        onStateChange(false)
    }

    fun destroy() {
        recognizer?.destroy()
        recognizer = null
        onStateChange(false)
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() = onStateChange(false)

        override fun onPartialResults(partialResults: Bundle?) {
            firstResult(partialResults)?.let(onPartial)
        }

        override fun onResults(results: Bundle?) {
            onStateChange(false)
            firstResult(results)?.let(onFinal)
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onError(error: Int) {
            onStateChange(false)
            // Swallow the two benign cases (user said nothing / released early); surface the rest.
            if (error == SpeechRecognizer.ERROR_NO_MATCH ||
                error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT
            ) {
                return
            }
            onError(errorText(error))
        }
    }

    private fun firstResult(bundle: Bundle?): String? =
        bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.firstOrNull()
            ?.takeIf { it.isNotBlank() }

    private fun errorText(error: Int): String = when (error) {
        SpeechRecognizer.ERROR_AUDIO -> "Couldn't access the microphone."
        SpeechRecognizer.ERROR_NETWORK,
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Voice input needs a connection on this device."
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is needed."
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Still finishing the last phrase — try again."
        else -> "Couldn't catch that. Please try again or type instead."
    }
}

/** Remember a [VoiceInputController] bound to the current context, destroyed when it leaves. */
@Composable
fun rememberVoiceInputController(
    onPartial: (String) -> Unit,
    onFinal: (String) -> Unit,
    onStateChange: (Boolean) -> Unit,
    onError: (String) -> Unit,
): VoiceInputController {
    val context = LocalContext.current
    val controller = remember {
        VoiceInputController(context.applicationContext, onPartial, onFinal, onStateChange, onError)
    }
    DisposableEffect(controller) {
        onDispose { controller.destroy() }
    }
    return controller
}
