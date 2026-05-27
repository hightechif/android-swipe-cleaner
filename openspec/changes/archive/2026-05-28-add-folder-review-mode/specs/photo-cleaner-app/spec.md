## MODIFIED Requirements

### Requirement: Swipe Screen Interface
The application SHALL display a stack containing the current photo and preloaded subsequent photos, hosted within a Scaffold containing a Bottom Navigation Bar. The top card SHALL support horizontal drag gestures to keep or delete, and tap gestures to open a Fullscreen Image Viewer overlay. The Bottom Navigation Bar SHALL show badges with count metrics for Kept and Trash tabs. The Swipe Screen SHALL also provide a dropdown/selector to filter the active review stack by a specific folder (album) or show all photos.

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
