## Why

When the user requests to move photos marked for deletion to the Android system trash, if they deny the system permission dialog, dismiss it, or if it gets interrupted, the photos are not moved to the system trash. However, the application currently clears the local database queue unconditionally, meaning these photos disappear from the delete queue within the app but remain in the device's main gallery. This causes the photos to reappear in the active swipe deck during subsequent reload sessions, creating a confusing and redundant user experience.

## What Changes

- Modify Jetpack Compose `ActivityResult` launchers in the Swipe and Completion screens to check for a successful result code (`Activity.RESULT_OK`) before completing the trash request.
- Ensure that if the system trash request is denied or canceled by the user, the database's delete queue is preserved so the user can retry.

## Capabilities

### New Capabilities
<!-- None -->

### Modified Capabilities
- `photo-cleaner-app`: Add explicit behavior requirements for handling cancellation or denial of the system MediaStore trash dialog, ensuring that the delete queue is preserved and not cleared from the local database.

## Impact

- **UI Screens**: `SwipeScreen.kt` and `CompletionScreen.kt` will import `android.app.Activity` and check `result.resultCode` in their `rememberLauncherForActivityResult` blocks.
- **Data Layer**: Local DB database entries in `trashed_photos` will remain intact upon cancellation/denial.
