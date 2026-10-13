package com.rankridge.app.ui.theme

import androidx.compose.ui.graphics.Color

// Brand palette — "Aqua Peacock": deep peacock teal/aqua (primary), warm gold (secondary) and
// emerald (tertiary), anchored by the navy pulled directly from the RankRidge emblem's circular
// badge. Pairs with the peacock-feather + flute mark used across launcher icon and auth screens.

// Light
val AquaPrimary = Color(0xFF007480)
val OnAquaPrimary = Color(0xFFFFFFFF)
val AquaPrimaryContainer = Color(0xFFB8EAF0)
val OnAquaPrimaryContainer = Color(0xFF001F24)
val GoldSecondary = Color(0xFF8A6D1B)
val OnGoldSecondary = Color(0xFFFFFFFF)
val GoldSecondaryContainer = Color(0xFFF6E3AC)
val OnGoldSecondaryContainer = Color(0xFF2B2000)
val EmeraldTertiary = Color(0xFF2E7D54)
val OnEmeraldTertiary = Color(0xFFFFFFFF)
val EmeraldTertiaryContainer = Color(0xFFB6F2D2)
val OnEmeraldTertiaryContainer = Color(0xFF00210F)
val LightBackground = Color(0xFFF5FBFC)
val LightOnSurface = Color(0xFF0C1F27)
val LightSurfaceVariant = Color(0xFFDCEAEC)
val LightOnSurfaceVariant = Color(0xFF404F52)
val LightOutline = Color(0xFF707F82)

// Top app bar and bottom navigation fill. This is the full-strength brand teal rather than a
// lighter tint: white content on #007480 measures 5.58:1 (WCAG AA for normal text), whereas the
// previous #2E8B99 could only carry black text and read as unstyled chrome.
val AquaBarLight = Color(0xFF007480)
val AquaBarDark = Color(0xFF123E47)

// Dark — navy sampled from the emblem (#011832), lifted slightly for Material elevation legibility.
val DarkPrimary = Color(0xFF4DD9E6)
val DarkOnPrimary = Color(0xFF00363D)
val DarkPrimaryContainer = Color(0xFF00515C)
val DarkOnPrimaryContainer = Color(0xFFB6F1F7)
val DarkSecondary = Color(0xFFE3C567)
val DarkOnSecondary = Color(0xFF3C2F00)
val DarkSecondaryContainer = Color(0xFF584419)
val DarkOnSecondaryContainer = Color(0xFFF8E6B4)
val DarkTertiary = Color(0xFF7FE0AC)
val DarkOnTertiary = Color(0xFF00391F)
val DarkTertiaryContainer = Color(0xFF00522F)
val DarkOnTertiaryContainer = Color(0xFFB6FFD7)
val DarkBackground = Color(0xFF071826)
val DarkSurface = Color(0xFF0D2436)
val DarkOnSurface = Color(0xFFE3EEF2)
val DarkSurfaceVariant = Color(0xFF17324A)
val DarkOnSurfaceVariant = Color(0xFFB9CCD4)
val DarkOutline = Color(0xFF80969F)

// ---------------------------------------------------------------------------------------------
// Tonal surface roles.
//
// Material 3 defines 36 colour roles. Setting only the headline ones leaves the rest on Material's
// BASELINE PURPLE palette — e.g. surfaceContainerLow falls back to #F7F2FA (lavender) and
// surfaceTint to #6750A4, which tints every elevated surface purple. These steps keep the exact
// Aqua Peacock hues already defined above and simply extend them across the roles that were
// previously unset, so the brand applies everywhere instead of only where it was spelled out.
// Light steps run near-white -> dimmed, each a small step along the same aqua hue as
// LightBackground (#F5FBFC).
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFFAFDFE)
val LightSurfaceContainer = Color(0xFFEFF7F9)
val LightSurfaceContainerHigh = Color(0xFFE8F2F4)
val LightSurfaceContainerHighest = Color(0xFFE1EDF0)
val LightSurfaceDim = Color(0xFFD5E4E7)
val LightSurfaceBright = Color(0xFFF9FDFE)
val LightOutlineVariant = Color(0xFFBFD2D5)
val LightInverseSurface = Color(0xFF15353D)
val LightInverseOnSurface = Color(0xFFE8F4F6)

// Dark steps run deepest -> lifted, along the same navy-teal as DarkBackground (#071826).
val DarkSurfaceContainerLowest = Color(0xFF04121C)
val DarkSurfaceContainerLow = Color(0xFF0B2030)
val DarkSurfaceContainer = Color(0xFF102A3C)
val DarkSurfaceContainerHigh = Color(0xFF16344A)
val DarkSurfaceContainerHighest = Color(0xFF1D3F57)
val DarkSurfaceDim = Color(0xFF071826)
val DarkSurfaceBright = Color(0xFF27475C)
val DarkOutlineVariant = Color(0xFF3A5365)
val DarkInverseSurface = Color(0xFFE3EEF2)
val DarkInverseOnSurface = Color(0xFF0D2436)

// Shared
val BrandScrim = Color(0xFF000000)

// Error (shared Material defaults)
val ErrorLight = Color(0xFFBA1A1A)
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFFFDAD6)
val OnErrorContainerLight = Color(0xFF410002)
val ErrorDark = Color(0xFFFFB4AB)
val OnErrorDark = Color(0xFF690005)
val ErrorContainerDark = Color(0xFF93000A)
val OnErrorContainerDark = Color(0xFFFFDAD6)
