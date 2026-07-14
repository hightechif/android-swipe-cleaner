## 1. Database & Repository Layer Updates

- [x] 1.1 Create `TrashedPhotoEntity` and `TrashedPhotoDao` inside `com.hightechif.swipecleaner.data.db`.
- [x] 1.2 Register `TrashedPhotoEntity` and `TrashedPhotoDao` in `AppDatabase.kt`.
- [x] 1.3 Create `TrashedPhotosRepository` interface and its implementation.
- [x] 1.4 Register the repository in `AppModule.kt`.

## 2. Usecase & Swipe Engine Updates

- [x] 2.1 Update `GetShuffledPhotoPoolUseCase` to filter out both kept photos and trashed photos from the database.
- [x] 2.2 Inject `TrashedPhotosRepository` into `SwipeViewModel` and update `AppModule.kt` dependency declaration.

## 3. SwipeViewModel Implementation Updates

- [x] 3.1 Expose a Flow-backed `deleteQueue` flow from the database in `SwipeViewModel` UI state.
- [x] 3.2 Update `swipeLeft()` to insert the photo URI into the `trashed_photos` database table.
- [x] 3.3 Update `restoreFromTrash(uri)` to delete the photo URI from `trashed_photos` database table.
- [x] 3.4 Update `onTrashRequestCompleted()` to clear all entries in `trashed_photos` database table after deletion is processed.
- [x] 3.5 Update `loadPhotoPool(keepDeleteQueue: Boolean)` to read from the `trashed_photos` database table and filter them out.
- [x] 3.6 Fix the bug in `resetAllKeptPhotos()` to call `loadPhotoPool(keepDeleteQueue = true)` to avoid wiping the trashed queue when resetting kept photos.
