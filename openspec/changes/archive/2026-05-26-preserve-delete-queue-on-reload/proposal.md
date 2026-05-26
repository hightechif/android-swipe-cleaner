## Why

When reloading the photo pool (e.g., via "Review Remaining Photos"), photos in the in-memory delete queue (swiped left) are cleared and reappear in the active swiping pool because they have not yet been permanently deleted. Users expect these marked-for-deletion photos to remain in the trash queue and not reappear in the active stack when continuing to review remaining photos.

## What Changes

- **Queue Preservation Option**: Update the ViewModel's reload logic to accept an option to keep the active delete queue.
- **Pool Filtering**: When reloading the photo pool while preserving the delete queue, filter out all photos in the delete queue from the active swipe pool.
- **UI Refresh**: Call this updated reload function on the "Review Remaining Photos" button action to ensure swiped-left photos are not re-added to the stack.

## Capabilities

### New Capabilities
- None

### Modified Capabilities
- `photo-cleaner-app`: Modifying the Swipe Screen / Completion Screen requirements to preserve swiped-left photos in the trash queue when reloading the deck.

## Impact

- **ViewModel & UI Layer**: `SwipeViewModel.kt` (adding option to `loadPhotoPool()`), `SwipeScreen.kt` (updating `onResetRemaining` action).
