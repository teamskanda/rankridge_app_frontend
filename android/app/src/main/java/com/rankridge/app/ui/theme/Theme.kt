package com.rankridge.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = AquaPrimary,
    onPrimary = OnAquaPrimary,
    primaryContainer = AquaPrimaryContainer,
    onPrimaryContainer = OnAquaPrimaryContainer,
    secondary = GoldSecondary,
    onSecondary = OnGoldSecondary,
    secondaryContainer = GoldSecondaryContainer,
    onSecondaryContainer = OnGoldSecondaryContainer,
    tertiary = EmeraldTertiary,
    onTertiary = OnEmeraldTertiary,
    tertiaryContainer = EmeraldTertiaryContainer,
    onTertiaryContainer = OnEmeraldTertiaryContainer,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightBackground,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    // Tonal roles — previously unset and therefore falling back to Material's baseline purple.
    surfaceContainerLowest = LightSurfaceContainerLowest,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest,
    surfaceDim = LightSurfaceDim,
    surfaceBright = LightSurfaceBright,
    surfaceTint = AquaPrimary,
    outlineVariant = LightOutlineVariant,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    inversePrimary = DarkPrimary,
    scrim = BrandScrim,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
)

private val DarkColors = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    surfaceDim = DarkSurfaceDim,
    surfaceBright = DarkSurfaceBright,
    surfaceTint = DarkPrimary,
    outlineVariant = DarkOutlineVariant,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    inversePrimary = AquaPrimary,
    scrim = BrandScrim,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
)

private val RankridgeShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun RankridgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Dynamic (Material You) color is intentionally off so the Aqua Peacock brand always shows.
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = RankridgeTypography,
        shapes = RankridgeShapes,
        content = content,
    )
}

/**
 * Shared fill for the top app bar and bottom navigation bar — the full-strength Aqua Peacock
 * primary, so the chrome reads as deliberately branded. White content
 * ([rankridgeBarContentColor]) clears WCAG AA on both the light (#007480, 5.58:1) and dark
 * (#123E47, 11.4:1) fills.
 */
@Composable
fun rankridgeBarColor(): Color = if (isSystemInDarkTheme()) AquaBarDark else AquaBarLight

/** Content colour (icons/text) for anything drawn on [rankridgeBarColor]. */
val rankridgeBarContentColor: Color = Color.White

/**
 * Card fill, which deliberately is NOT `colorScheme.surface`.
 *
 * `surface` equals `background` in this theme, so a card painted with it separated from the page
 * only by a drop shadow — which reads as washed-out rather than layered. Light mode now puts a
 * true white card on the aqua-tinted page; dark mode lifts the card a step above the near-black
 * background. In both cases the separation comes from tone, with the shadow only reinforcing it.
 */
@Composable
fun rankridgeCardColor(): Color =
    if (isSystemInDarkTheme()) DarkSurfaceContainer else LightSurfaceContainerLowest

/**
 * Hairline border drawn around cards. Professional dashboards define edges with a 1dp low-contrast
 * outline instead of a heavier shadow — it survives on both light and dark pages and keeps the
 * surface from looking inflated.
 */
@Composable
fun rankridgeCardBorder(): Color =
    if (isSystemInDarkTheme()) DarkOutlineVariant.copy(alpha = 0.45f) else LightOutlineVariant.copy(alpha = 0.55f)
