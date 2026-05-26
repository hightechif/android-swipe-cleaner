## MODIFIED Requirements

### Requirement: Swipe Screen Interface
The Swipe Screen SHALL display a stack containing the current photo and preloaded subsequent photos, hosted within a Scaffold containing a Bottom Navigation Bar. The top card SHALL support horizontal drag gestures to keep or delete, and tap gestures to open a Fullscreen Image Viewer overlay. The Bottom Navigation Bar SHALL show badges with count metrics for Kept and Trash tabs.

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

### Requirement: Kept Photos Screen
The application SHALL provide a Kept Photos Screen/Tab displaying a grid layout of all photos currently marked as kept in the database, allowing users to restore individual photos back into the active swipe stack.

#### Scenario: Open Kept Photos Grid
- **WHEN** the user requests to see kept photos by tapping the Kept Photos tab
- **THEN** the application displays a grid view containing all kept photo images from the database.

#### Scenario: Reset Single Kept Photo
- **WHEN** the user taps a kept photo in the grid and confirms reset
- **THEN** the application removes the photo from the kept database, inserts it back into the active photo pool at the current index, and decrements the kept count.


## ADDED Requirements

### Requirement: Trash Queue Screen
The application SHALL provide a Trash Queue Screen/Tab displaying a grid layout of all photos currently in the in-memory delete queue, allowing users to restore individual photos or empty the trash to delete them.

#### Scenario: Open Trash Queue Grid
- **WHEN** the user selects the Trash Queue tab
- **THEN** the application displays a grid of photos currently queued for deletion.

#### Scenario: Restore Single Trash Photo
- **WHEN** the user taps a photo in the Trash grid
- **THEN** the application removes the photo from the delete queue and inserts it back into the active photo pool at the current index.

#### Scenario: Delete All Trash Photos
- **WHEN** the user taps the "Delete Photos" button in the Trash tab
- **THEN** the application prompts the MediaStore system trash request dialog to delete all photos in the delete queue.

### Requirement: Milestone Swipe Prompts
The application SHALL monitor the user's swiping progress and trigger a non-blocking check-in prompt after every 20 swipes.

#### Scenario: Milestone Prompt Trigger
- **WHEN** the sum of kept and deleted photos in the session reaches a multiple of 20
- **THEN** the application displays a milestone prompt dialog.

#### Scenario: Milestone Prompt Action - Review
- **WHEN** the user selects "Review" from the milestone dialog
- **THEN** the application dismisses the dialog and switches the active tab to the Trash or Kept tab.

#### Scenario: Milestone Prompt Action - Empty Trash
- **WHEN** the user selects "Empty Trash" from the milestone dialog
- **THEN** the application dismisses the dialog and triggers the MediaStore trash dialog for the delete queue.
