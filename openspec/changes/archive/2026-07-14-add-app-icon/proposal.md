## Why

Currently, the application does not have a customized launcher icon and relies on the default Android system launcher icon (`@android:drawable/sym_def_app_icon`). Adding a customized, modern launcher icon makes the app feel professional, unique, and visually polished on the user's home screen.

## What Changes

- Add a custom adaptive launcher icon for the SwipeCleaner application.
- Use the standard Google Material Design "brush" icon symbol (white) for the foreground layer.
- Use TOA Paint's Trusted Blue (#1E5891) for the background layer.
- Update `AndroidManifest.xml` to reference the new adaptive launcher icons.

## Capabilities

### New Capabilities
- `app-icon`: Custom launcher icon displaying a white paint brush on a Trusted Blue background, supporting Android adaptive icon layouts (API 26+).

### Modified Capabilities
<!-- None -->

## Impact

- **Android Resources**: Adds `colors.xml` under `values/`, `ic_launcher_foreground.xml` under `drawable/`, and adaptive icon definitions under `mipmap-anydpi-v26/`.
- **AndroidManifest.xml**: Modifies `android:icon` and `android:roundIcon` in the `<application>` element.
