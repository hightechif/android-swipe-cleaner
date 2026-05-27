## 1. Update Launcher Results in UI

- [x] 1.1 Import `android.app.Activity` and update `rememberLauncherForActivityResult` in [SwipeScreen.kt](file:///Users/ridhanfadhilah/Public/Fadhil/mobile/android/github/SwipeCleaner/app/src/main/java/com/hightechif/swipecleaner/ui/screen/SwipeScreen.kt) to verify `result.resultCode == Activity.RESULT_OK`
- [x] 1.2 Import `android.app.Activity` and update `rememberLauncherForActivityResult` in [CompletionScreen.kt](file:///Users/ridhanfadhilah/Public/Fadhil/mobile/android/github/SwipeCleaner/app/src/main/java/com/hightechif/swipecleaner/ui/screen/CompletionScreen.kt) to verify `result.resultCode == Activity.RESULT_OK`

## 2. Verify and Test

- [x] 2.1 Manually verify that allowing the system trash dialog deletes the items and clears the local queue
- [x] 2.2 Manually verify that denying or cancelling the system trash dialog keeps the queue populated and preserves the local database
- [x] 2.3 Run automated tests to verify the project builds and runs cleanly
