## Why

Currently, when the Fullscreen Image Viewer (zoom/pan overlay) is active on either the Swipe screen or Kept Photos screen, initiating an Android system back gesture or back button press closes the entire screen (or exits the app) instead of dismissing the fullscreen overlay.

## What Changes

- Intercept Android system back button/gesture actions when the Fullscreen Image Viewer is active.
- Dismiss the Fullscreen Image Viewer overlay when a back press is intercepted, keeping the host screen active.

## Capabilities

### New Capabilities
<!-- None -->

### Modified Capabilities
- `photo-cleaner-app`: Update back-navigation behavior when the Fullscreen Image Viewer is open on the Swipe Deck or Kept Photos screens to dismiss the viewer rather than closing the screen/app.

## Impact

- **UI Layer**: Modifies `SwipeScreen.kt` and `KeptPhotosScreen.kt` to use Jetpack Compose's `BackHandler` utility when the zoom viewer is active.
