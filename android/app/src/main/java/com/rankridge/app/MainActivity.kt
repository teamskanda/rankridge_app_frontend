package com.rankridge.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rankridge.app.ui.navigation.RankridgeNavHost
import com.rankridge.app.ui.theme.RankridgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Both bars are drawn over the deep Aqua Peacock chrome in light AND dark mode, so the
        // system icons must always be light. The default auto() style keys off the system theme
        // and would render dark status icons on the teal bar in light mode.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            RankridgeTheme {
                RankridgeNavHost()
            }
        }
    }
}
