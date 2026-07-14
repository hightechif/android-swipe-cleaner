## MODIFIED Requirements

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

#### Scenario: Trash Queue Persistent Across Restart
- **WHEN** the user restarts the application after marking photos for deletion
- **THEN** the application retains those photos in the Trash Queue tab and excludes them from the active Swipe Deck stack.
