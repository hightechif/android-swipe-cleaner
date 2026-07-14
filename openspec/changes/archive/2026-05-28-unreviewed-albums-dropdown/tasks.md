## 1. Implement Dynamic Albums Flow in SwipeViewModel

- [x] 1.1 Import `combine` flow and add `AlbumHelper` private data class in [SwipeViewModel.kt](file:///Users/ridhanfadhilah/Public/Fadhil/mobile/android/github/SwipeCleaner/app/src/main/java/com/hightechif/swipecleaner/ui/viewmodel/SwipeViewModel.kt)
- [x] 1.2 Set up the reactive `combine` collector in the `init` block of [SwipeViewModel.kt](file:///Users/ridhanfadhilah/Public/Fadhil/mobile/android/github/SwipeCleaner/app/src/main/java/com/hightechif/swipecleaner/ui/viewmodel/SwipeViewModel.kt) to update `uiState.albums` dynamically based on unreviewed photos
- [x] 1.3 Update the body of `loadAlbums()` in [SwipeViewModel.kt](file:///Users/ridhanfadhilah/Public/Fadhil/mobile/android/github/SwipeCleaner/app/src/main/java/com/hightechif/swipecleaner/ui/viewmodel/SwipeViewModel.kt) to delegate to `loadMediaImages()`

## 2. Verification and Testing

- [x] 2.1 Run all unit tests using `./gradlew test` to ensure existing functionality remains intact
- [x] 2.2 Add unit tests to verify the new dynamic album selection and count behavior in `SwipeViewModelTest`
