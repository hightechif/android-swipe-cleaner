## 1. Database & Repository Layer Updates

- [x] 1.1 Add single photo deletion query `deleteKeptPhoto(uri: String)` to `KeptPhotoDao` Room DAO.
- [x] 1.2 Expose `deleteKeptPhoto(uri: String)` method in `KeptPhotosRepository` and implement it in `KeptPhotosRepositoryImpl`.

## 2. Shared State & Navigation Redesign in SwipeViewModel

- [x] 2.1 Add `SwipeTab` enum (SWIPE, KEPT, TRASH) and track the active tab in `SwipeUiState`.
- [x] 2.2 Implement `restoreFromTrash(uri: String)` to remove a photo from the delete queue and re-insert it back into `photoPool` at the current index.
- [x] 2.3 Implement `restoreFromKept(uri: String)` to delete a photo from the database and re-insert it back into `photoPool` at the current index.
- [x] 2.4 Track swipe counts to trigger the 20-swipe milestone dialog state in the UI state.
- [x] 2.5 Expose a method to update the active tab in `SwipeViewModel`.

## 3. UI Components & Screen Updates

- [x] 3.1 Update `SwipeableCard` to detect tap gestures alongside horizontal drags, triggering card tap events.
- [x] 3.2 Implement the Fullscreen Image Viewer overlay in `SwipeScreen` supporting pinch-to-zoom, pan with boundary clamping, double-tap zoom, and tap-to-dismiss.
- [x] 3.3 Create a Bottom Navigation Bar layout using Compose `NavigationBar` inside a `Scaffold` on `SwipeScreen`.
- [x] 3.4 Integrate the Swipe card deck, Kept Photos grid, and Trash Queue grid as selectable tab views.
- [x] 3.5 Implement dynamic count badge display on the Kept and Trash bottom navigation bar items.
- [x] 3.6 Implement the milestone check-in dialog/bottom sheet appearing every 20 swipes with choices to review, empty trash, or continue swiping.
