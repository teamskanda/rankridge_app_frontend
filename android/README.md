# Rankridge — Android app

Native Android client (Kotlin + Jetpack Compose, Material 3, MVVM). **Demo build — UI only, no backend.**

## Current scope

Authentication UI only, built to production structure:

- **Login** — phone number (`+91`, 10 digits) → **Send OTP**.
- **Create account** — full name, role selector (all roles per the architecture), phone number → **Send OTP**.
- **OTP** — 6-cell code entry, resend countdown (30s), verify → Home.
- **Home** — placeholder landing screen after verification; role dashboards come next.

There is **no backend and no data layer**. Phone/OTP flows are simulated in the view models
(a short delay drives the spinner). Any correctly-formatted 10-digit number and any 6-digit OTP
are accepted. Input validation lives in `core/validation/Validators.kt` (pure Kotlin, unit-tested).

## Theme

Brand palette is **Aqua Peacock** — deep peacock teal/aqua (primary), warm gold (secondary) and
emerald (tertiary), anchored by the navy pulled directly from the RankRidge emblem's circular
badge (`#011832`). All auth screens and the launcher icon use the emblem
(`RankRidge Peacock Flute Emblem`). Material You dynamic color is intentionally disabled so the
brand colors always show. A coherent dark theme (navy-based) is included. See
`ui/theme/Color.kt` for the full palette.

## Open & build

Requires **Android Studio** (Ladybug or newer) on Windows.

1. Open Android Studio → **Open** → select this `android/` folder (not the repo root).
2. Let Gradle sync. Android Studio provisions the Gradle wrapper automatically on first sync; a
   committed `gradle-wrapper.jar` / `gradlew` is intentionally omitted.
3. Run on an emulator or device (Run ▶).
4. Unit tests: `Validators` logic is covered under `app/src/test/`.

## Placeholders to finalise

- **App ID / package** — currently `com.rankridge.app`. Placeholder; set the real ID before any
  Play Store upload. `in.rankridge.*` can't be a Kotlin/Java package (`in` is a reserved keyword) —
  if an `.in`-style Play Store ID is wanted, keep this source namespace and change only `applicationId`.
- **Launcher icon** — now the real RankRidge emblem as an adaptive icon (navy background +
  inset foreground, generated at all 5 densities) plus a legacy/round fallback. If the source
  artwork changes, regenerate `mipmap-*/ic_launcher*.png` from the new emblem at the same inset
  ratio (~62%). The same emblem is reused in-app as the brand mark (`BrandMark` composable in
  `ui/auth/components/AuthComponents.kt`, backed by `drawable-xxhdpi/img_brand_emblem.webp`).
- **Backend** — none yet. When one exists, reintroduce a data layer and have the view models call it
  instead of simulating.

## Structure

```
app/src/main/java/com/rankridge/app/
  core/validation/      input validation (pure Kotlin, unit-tested)
  ui/auth/              UserRole (UI model)
  ui/auth/components/   shared Compose fields, OTP input, button, dropdown
  ui/auth/login/        LoginScreen + ViewModel + UiState
  ui/auth/register/     CreateAccountScreen + ViewModel + UiState
  ui/auth/otp/          OtpScreen + ViewModel + UiState
  ui/home/              placeholder landing
  ui/navigation/        NavHost + routes
  ui/theme/             Material 3 theme (Sea Breeze)
```
