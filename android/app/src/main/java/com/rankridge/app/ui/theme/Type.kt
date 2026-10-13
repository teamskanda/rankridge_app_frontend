package com.rankridge.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.rankridge.app.R

/**
 * Inter, bundled as static weights so the app renders identically offline and on every device
 * (minSdk 24 rules out variable fonts, which need API 26+ for [androidx.compose.ui.text.font.FontVariation]).
 *
 * Inter is chosen over the platform default for one measurable reason: this product is mostly
 * numbers — scores, percentages, ranks, attendance, fees. Inter's tall x-height and unambiguous
 * figures keep those legible at the 11–14sp sizes the dashboards actually use.
 */
val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
)

/**
 * The type scale, specified rather than inherited.
 *
 * Two rules run through it, and they are what separate a designed scale from stock Material:
 *  - **Optical tracking**: letter-spacing tightens as size grows (−0.5sp at display sizes, 0 at
 *    body, +0.1sp at the smallest labels). Uniform tracking is what makes large headings look
 *    loose and small labels look cramped.
 *  - **Tightened leading on headings**: display/headline line-height sits near 1.15–1.25× rather
 *    than Material's airier default, so multi-line titles read as one block instead of drifting
 *    apart. Body and label keep generous leading for scanability.
 */
private fun scale(
    size: Double,
    lineHeight: Double,
    weight: FontWeight,
    tracking: Double,
) = TextStyle(
    fontFamily = Inter,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    fontWeight = weight,
    letterSpacing = tracking.sp,
)

val RankridgeTypography = Typography(
    displayLarge = scale(57.0, 64.0, FontWeight.Bold, -1.0),
    displayMedium = scale(45.0, 52.0, FontWeight.Bold, -0.75),
    displaySmall = scale(36.0, 44.0, FontWeight.Bold, -0.5),

    headlineLarge = scale(32.0, 38.0, FontWeight.Bold, -0.5),
    headlineMedium = scale(28.0, 34.0, FontWeight.Bold, -0.4),
    headlineSmall = scale(24.0, 30.0, FontWeight.Bold, -0.3),

    titleLarge = scale(21.0, 27.0, FontWeight.SemiBold, -0.2),
    titleMedium = scale(16.0, 22.0, FontWeight.SemiBold, -0.1),
    titleSmall = scale(14.0, 20.0, FontWeight.SemiBold, 0.0),

    bodyLarge = scale(16.0, 24.0, FontWeight.Normal, 0.0),
    bodyMedium = scale(14.0, 21.0, FontWeight.Normal, 0.0),
    bodySmall = scale(12.5, 18.0, FontWeight.Normal, 0.05),

    labelLarge = scale(14.0, 20.0, FontWeight.SemiBold, 0.0),
    labelMedium = scale(12.0, 16.0, FontWeight.Medium, 0.1),
    labelSmall = scale(11.0, 15.0, FontWeight.Medium, 0.1),
)

/** Wordmark lockup used on the auth screens and the launcher-adjacent brand moments. */
val BrandWordmark = TextStyle(
    fontFamily = Inter,
    fontSize = 25.sp,
    lineHeight = 30.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = (-0.4).sp,
    textAlign = TextAlign.Center,
)

/**
 * Dedicated style for the big numbers on metric tiles and score cards. Same family, but Bold with
 * negative tracking so a figure like "92%" reads as one dense token rather than three loose glyphs.
 */
val MetricNumber = TextStyle(
    fontFamily = Inter,
    fontSize = 26.sp,
    lineHeight = 30.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = (-0.6).sp,
)
