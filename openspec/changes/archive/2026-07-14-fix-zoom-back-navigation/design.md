## Context

The application has a Fullscreen Image Viewer overlay that appears when tapping on a card in the Swipe deck or a kept photo in the grid. However, there is currently no custom back-navigation handling for this overlay. As a result, when the overlay is active and the user presses the system back button (or performs a back gesture), the Android system propagates the event, causing the app to exit or the parent screen to close.

## Goals / Non-Goals

**Goals:**
- Intercept the system back button/gesture actions when the Fullscreen Image Viewer overlay is open.
- Dismiss the overlay on back press and remain on the host screen.

**Non-Goals:**
- Overriding back navigation when other popups (such as reset dialogs or the empty trash prompt) are shown, since those are managed by standard `AlertDialog` components.

## Decisions

### 1. Integrate `BackHandler` Composable
- **Decision**: Use `androidx.activity.compose.BackHandler` in both `SwipeScreen.kt` and `KeptPhotosScreen.kt`.
- **Rationale**: `BackHandler` is the standard Compose API to intercept back key events at the UI component level. We can dynamically control its interception using the `enabled` parameter:
  ```kotlin
  BackHandler(enabled = activeViewerUri != null) {
      // Action: set activeViewerUri to null
  }
  ```
  This is extremely simple, declarative, and requires no custom navigation routing updates.
- **Alternatives Considered**: Modifying MainActivity `onBackPressed()` (rejected as it breaks single-responsibility and screen-level state encapsulation).

## Risks / Trade-offs

- **[Risk]**: `BackHandler` could intercept back presses even after the overlay is closed.
  - **[Mitigation]**: By setting the `enabled` parameter directly to `activeViewerUri != null`, the back handler automatically disables itself as soon as `activeViewerUri` is cleared.
