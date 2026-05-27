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
The Swipe Screen SHALL display a stack containing the current photo and preloaded subsequent photos, hosted within a Scaffold containing a Bottom Navigation Bar. The top card SHALL support horizontal drag gestures to keep or delete, and tap gestures to open a Fullscreen Image Viewer overlay. The Bottom Navigation Bar SHALL show badges with count metrics for Kept and Trash tabs. The Swipe Screen SHALL also provide a dropdown/selector to filter the active review stack by a specific folder (album) or show all photos. The folder selection dropdown options SHALL display only those folders containing unreviewed photos (neither kept nor trashed), showing their unreviewed photos count, and SHALL update dynamically in real-time.

#### Scenario: Swipe Right to Keep
- **WHEN** the user drags the photo card right past the threshold and releases it
- **THEN** the card animates off the screen to the right, and the photo is saved to the kept photos database.

#### Scenario: Swipe Left to Delete
- **WHEN** the user drags the photo card left past the threshold and releases it
- **THEN** the card animates off the screen to the left, and the photo URI is added to the in-memory delete queue.

#### Scenario: Open Fullscreen Image Viewer
- **WHEN** the user taps the top active card in the swipe stack
- **THEN** the application opens the Fullscreen Image Viewer overlay showing that photo.

#### Scenario: Close Fullscreen Image Viewer
- **WHEN** the user taps the screen inside the Fullscreen Image Viewer overlay
- **THEN** the application dismisses the overlay and returns to the Swipe Screen interface.

#### Scenario: Switch Tabs in Session
- **WHEN** the user taps a tab in the bottom navigation bar
- **THEN** the application switches the active screen view to the corresponding tab (Swipe Deck, Kept Photos, or Trash Queue).

#### Scenario: Filter Swipe Deck by Folder
- **WHEN** the user selects a specific folder/album from the folder selection dropdown on the Swipe screen
- **THEN** the application reloads the swipe photo pool containing only the photos inside that folder, excluding already reviewed photos, and resets the swipe stack to the first index.

#### Scenario: Folder Dropdown Contains Only Unreviewed Folders and Dynamically Updated Count
- **WHEN** the user opens the folder selection dropdown
- **THEN** the application only displays folders containing unreviewed photos, with their unreviewed counts (total photos minus kept and trashed photos in that folder), updating dynamically when reviews change.

### Requirement: Completion Screen Statistics and Celebration
When the photo pool is exhausted, the application SHALL transition to a Completion Screen. The Completion Screen SHALL display the session stats (total reviewed, total marked for deletion) and SHALL play a celebratory Lottie confetti animation regardless of the outcome of the deletion dialog.

#### Scenario: Session Completed
- **WHEN** the user swipes the last photo in the pool
- **THEN** the application navigates to the Completion Screen and plays the confetti Lottie animation.

#### Scenario: Review Remaining Photos Preserves Trash
- **WHEN** the user clicks "Review Remaining Photos" on the completion screen/view
- **THEN** the application reloads the photo pool, filters out all photos currently in the delete queue, and preserves the delete queue list.

### Requirement: Kept Photos Screen
The application SHALL provide a Kept Photos Screen/Tab displaying a grid layout of photos currently marked as kept in the database, allowing users to restore individual photos or all kept photos back into the active swipe stack. The screen SHALL support switching view modes between a flat list of all kept photos and a grouped folder list.

#### Scenario: Open Kept Photos Grid
- **WHEN** the user requests to see kept photos by tapping the Kept Photos tab
- **THEN** the application displays a grid view containing all kept photo images from the database.

#### Scenario: Switch Kept View to Albums
- **WHEN** the user selects "Kept Photos Albums" from the kept view mode dropdown
- **THEN** the application displays a grid of folder items, where each item shows the folder name, a cover photo from the kept photos in that folder, and the count of kept photos in that folder.

#### Scenario: Drill Down into Kept Album
- **WHEN** the user selects a folder item from the folder grid
- **THEN** the application displays a grid showing only the kept photos belonging to that folder, along with a back navigation button.

#### Scenario: Zoom Kept Photo on Tap
- **WHEN** the user performs a single tap on a kept photo in any view
- **THEN** the application opens the Fullscreen Image Viewer overlay allowing them to zoom and pan.

#### Scenario: Reset Kept Photo on Long Press
- **WHEN** the user performs a long press on a kept photo and confirms the reset dialog
- **THEN** the application removes the photo from the kept database, inserts it back into the active photo pool at the current index, and decrements the kept count.

#### Scenario: Reset All Kept Photos
- **WHEN** the user requests to restore all kept photos and confirms the reset dialog
- **THEN** the application clears all entries in the kept photos database, reloads the swiping photo pool, and navigates back to the Swipe Deck.

### Requirement: Trash Queue Screen
The application SHALL provide a Trash Queue Screen/Tab displaying a grid layout of all photos currently queued for deletion in the database, allowing users to restore individual photos or empty the trash to delete them.

#### Scenario: Open Trash Queue Grid
- **WHEN** the user selects the Trash Queue tab
- **THEN** the application displays a grid of photos currently queued for deletion.

#### Scenario: Restore Single Trash Photo
- **WHEN** the user taps a photo in the Trash grid
- **THEN** the application removes the photo from the delete database table and inserts it back into the active photo pool at the current index.

#### Scenario: Delete All Trash Photos
- **WHEN** the user taps the "Delete Photos" button in the Trash tab
- **THEN** the application prompts the MediaStore system trash request dialog to delete all photos in the delete queue.

#### Scenario: Delete All Trash Photos Accepted
- **WHEN** the user confirms and allows the MediaStore system trash request dialog
- **THEN** the application receives a successful activity result and clears the local delete database table.

#### Scenario: Delete All Trash Photos Cancelled or Denied
- **WHEN** the user denies or cancels the MediaStore system trash request dialog
- **THEN** the application receives a cancelled activity result, preserves the local delete database table, and does not clear the delete queue.

#### Scenario: Trash Queue Persistent Across Restart
- **WHEN** the user restarts the application after marking photos for deletion
- **THEN** the application retains those photos in the Trash Queue tab and excludes them from the active Swipe Deck stack.

### Requirement: Milestone Swipe Prompts
The application SHALL monitor the user's swiping progress and trigger a non-blocking check-in prompt after every 50 swipes.

#### Scenario: Milestone Prompt Trigger
- **WHEN** the sum of kept and deleted photos in the session reaches a multiple of 50
- **THEN** the application displays a milestone prompt dialog.

#### Scenario: Milestone Prompt Action - Review
- **WHEN** the user selects "Review" from the milestone dialog
- **THEN** the application dismisses the dialog and switches the active tab to the Trash or Kept tab.

#### Scenario: Milestone Prompt Action - Empty Trash
- **WHEN** the user selects "Empty Trash" from the milestone dialog
- **THEN** the application dismisses the dialog and triggers the MediaStore trash dialog for the delete queue.

