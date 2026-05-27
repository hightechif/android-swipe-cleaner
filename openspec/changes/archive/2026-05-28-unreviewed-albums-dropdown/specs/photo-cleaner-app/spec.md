## MODIFIED Requirements

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
