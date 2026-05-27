## 1. Data Layer and Use Cases

- [x] 1.1 Add `Album` and `MediaImage` models, and update `MediaStoreRepository` to query all distinct albums and images by bucket ID
- [x] 1.2 Update `GetShuffledPhotoPoolUseCase` to support querying photos filtered by an optional `bucketId`

## 2. ViewModel Updates

- [x] 2.1 Add selected album state, loaded albums list, and album selector methods to `SwipeViewModel`

## 3. Swipe Screen UI Updates

- [x] 3.1 Implement the interactive folder selector button and dialog/sheet in [SwipeScreen.kt](file:///Users/ridhanfadhilah/Public/Fadhil/mobile/android/github/SwipeCleaner/app/src/main/java/com/hightechif/swipecleaner/ui/screen/SwipeScreen.kt)
- [x] 3.2 Update Swipe header layout to display the folder selection, and trigger photo pool reload on change
- [x] 3.3 Hoist App Header and folder selection UI to the top-level of the Swipe Screen tab to keep it visible in all states

## 4. Kept Screen UI Updates

- [x] 4.1 Update [KeptPhotosScreen.kt](file:///Users/ridhanfadhilah/Public/Fadhil/mobile/android/github/SwipeCleaner/app/src/main/java/com/hightechif/swipecleaner/ui/screen/KeptPhotosScreen.kt) (and KeptTabContent in `SwipeScreen.kt`) to support Kept view modes ("All Kept Photos" vs "Kept Photos Albums")
- [x] 4.2 Implement the Kept Albums grid and drill-down sub-grid views
- [x] 4.3 Update photo card clicks on Kept screen to use `combinedClickable`: single-tap for zoom viewer, long-press for restore prompt

## 5. Testing and Validation

- [x] 5.1 Verify that swiping folders filters cards correctly on the active Swipe deck
- [x] 5.2 Verify Kept Photos view mode A/B toggle and album drill-down functions correctly
- [x] 5.3 Verify kept photo single-tap (zoom) and long-press (restore confirmation) works as expected
- [x] 5.4 Run automated unit tests to verify the project builds and runs cleanly
