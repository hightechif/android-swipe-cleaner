## Context

Currently, the SwipeCleaner application does not have a branded app launcher icon and utilizes the generic Android system fallback icon. This design outlines the implementation of a custom adaptive app launcher icon using standard vector-based Android resource XML files.

## Goals / Non-Goals

**Goals:**
- Implement a custom, high-quality adaptive app launcher icon.
- Use a white Google Material Design "brush" icon symbol for the foreground.
- Use TOA Paint's Trusted Blue (#1E5891) for the background.
- Configure `AndroidManifest.xml` to associate these new assets with the application.

**Non-Goals:**
- Generating legacy rasterized PNG launcher files (e.g., hdpi, xhdpi) since the project's minimum SDK version is 26 (Android 8.0), which natively supports adaptive vector-based launcher icons.
- Creating a dynamic launcher icon switching mechanism at runtime.

## Decisions

### 1. Vector-Only Adaptive Icons
- **Decision**: Only define vector assets (`.xml`) for the app icon, utilizing the `mipmap-anydpi-v26` resource directory.
- **Rationale**: The project has a `minSdk` of 26. Since Android 8.0+ natively supports adaptive XML icons, providing raster assets (PNGs) is redundant and unnecessarily increases the APK size.
- **Alternatives Considered**: Creating legacy PNG icons for backwards compatibility (rejected due to target audience device configuration and the overhead of generating multiple raster resolutions).

### 2. Centering and Scaling the Foreground Vector
- **Decision**: Define the foreground vector drawable with a `108dp` viewport and wrap the `24dp` brush icon path in a `<group>` with `scaleX="2.0"`, `scaleY="2.0"`, and `translateX="30.0"`, `translateY="30.0"`.
- **Rationale**: Adaptive icons require a 108dp x 108dp canvas with a centered 66dp safe zone. Translating the center of the 24dp brush (`12, 12`) to the center of the 108dp grid (`54, 54`) and scaling by `2.0` produces a centered `48dp` icon. This is safely within the `66dp` circular safe zone, guaranteeing that launcher masks (such as squircle, round, teardrop) will not crop the foreground logo.

### 3. Separation of Colors and Drawables
- **Decision**: Define the background color `#1E5891` inside `values/colors.xml` and reference it from the adaptive icon definitions.
- **Rationale**: Keeps resource definitions modular. Changing the background color in the future will only require updating `colors.xml`.

## Risks / Trade-offs

- **[Risk]**: The logo might look too small on some launchers due to the 48dp scale.
  - **[Mitigation]**: While a larger scale could be used, keeping it at 48dp ensures absolute compliance with the circular safe zone guidelines of Google, preventing any clipping.
