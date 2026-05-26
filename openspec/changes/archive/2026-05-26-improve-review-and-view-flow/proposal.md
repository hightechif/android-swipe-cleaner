## Why

Users currently cannot inspect gallery images in detail to determine if they are blurry, out of focus, or identical before deciding to keep or delete them. Furthermore, swiping through the entire gallery before seeing results or emptying trash creates a high-friction experience. 

## What Changes

- **Fullscreen Image Viewer**: Allow users to tap a photo on the swipe deck to open a fullscreen overlay supporting pinch-to-zoom (1.0x to 5.0x), pan (constrained to screen boundaries), double-tap zoom (2.5x), and tap-to-exit.
- **Bottom Navigation Bar**: Add a persistent bottom navigation bar in the main session screen with three tabs: Swipe (active deck), Kept Photos (session/overall kept items), and Trash Queue (current session delete queue).
- **Dynamic Counter Badges**: Show dynamic count badges on the Kept and Trash tabs in the bottom bar to provide real-time feedback on user choices.
- **Real-time Undo/Restore**: Allow users to click photos in the Trash grid or Kept grid to restore them back to the active swipe card stack.
- **Milestone Prompts**: Introduce a non-intrusive prompt after every 20 swipes (e.g. at 20, 40, etc.) suggesting the user review their swiped photos or empty their trash.

## Capabilities

### New Capabilities
- None

### Modified Capabilities
- `photo-cleaner-app`: Modifying the requirements of the Swipe Screen interface, Completion Screen, and Kept Photos Screen into a unified bottom navigation-based review and view flow.

## Impact

- **UI Screens & ViewModels**: `SwipeScreen.kt`, `KeptPhotosScreen.kt`, `SwipeViewModel.kt`, `SwipeCleanerApp.kt`, `SwipeableCard.kt`.
- **Database/Repository Layer**: Add support for deleting individual kept photos from the repository and local Room database.
