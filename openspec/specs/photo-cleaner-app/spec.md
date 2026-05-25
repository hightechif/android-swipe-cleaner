# photo-cleaner-app Specification

## Purpose
TBD - created by archiving change create-swipe-cleaner-app. Update Purpose after archive.
## Requirements
### Requirement: Permission Check and Onboarding
The application SHALL check for storage permissions (`READ_MEDIA_IMAGES` and `READ_MEDIA_VISUAL_USER_SELECTED` on Android 13+, and `READ_EXTERNAL_STORAGE` on older APIs) on launch. If permission is not granted, the application SHALL display a Permission Onboarding Screen with an "Allow Permission" button. If permission is granted, the application SHALL automatically navigate to the Swipe Screen.

#### Scenario: First Launch without Permissions
- **WHEN** the user launches the application and permissions have not been granted
- **THEN** the application displays the Permission Onboarding Screen with the "Allow Permission" button.

#### Scenario: Launch with Permissions Pre-Granted
- **WHEN** the user launches the application and permissions are already granted
- **THEN** the application navigates directly to the Swipe Screen.

### Requirement: Swipe Screen Interface
The Swipe Screen SHALL display a stack containing the current photo and preloaded subsequent photos. The top card SHALL support horizontal drag gestures: swiping left to add the photo to the in-memory delete queue, or swiping right to add the photo to the kept database.

#### Scenario: Swipe Right to Keep
- **WHEN** the user drags the photo card right past the threshold and releases it
- **THEN** the card animates off the screen to the right, and the photo is saved to the kept photos database.

#### Scenario: Swipe Left to Delete
- **WHEN** the user drags the photo card left past the threshold and releases it
- **THEN** the card animates off the screen to the left, and the photo URI is added to the in-memory delete queue.

### Requirement: Completion Screen Statistics and Celebration
When the photo pool is exhausted, the application SHALL transition to a Completion Screen. The Completion Screen SHALL display the session stats (total reviewed, total marked for deletion) and SHALL play a celebratory Lottie confetti animation regardless of the outcome of the deletion dialog.

#### Scenario: Session Completed
- **WHEN** the user swipes the last photo in the pool
- **THEN** the application navigates to the Completion Screen and plays the confetti Lottie animation.

### Requirement: Kept Photos Screen
The application SHALL provide a Kept Photos Screen displaying a grid layout of all photos currently marked as kept in the database.

#### Scenario: Open Kept Photos Grid
- **WHEN** the user requests to see kept photos
- **THEN** the application displays a grid view containing all kept photo images from the database.

