## MODIFIED Requirements

### Requirement: Kept Photos Screen
The application SHALL provide a Kept Photos Screen/Tab displaying a grid layout of all photos currently marked as kept in the database, allowing users to restore individual photos or all kept photos back into the active swipe stack.

#### Scenario: Open Kept Photos Grid
- **WHEN** the user requests to see kept photos by tapping the Kept Photos tab
- **THEN** the application displays a grid view containing all kept photo images from the database.

#### Scenario: Reset Single Kept Photo
- **WHEN** the user taps a kept photo in the grid and confirms reset
- **THEN** the application removes the photo from the kept database, inserts it back into the active photo pool at the current index, and decrements the kept count.

#### Scenario: Reset All Kept Photos
- **WHEN** the user requests to restore all kept photos and confirms the reset dialog
- **THEN** the application clears all entries in the kept photos database, reloads the swiping photo pool, and navigates back to the Swipe Deck.
