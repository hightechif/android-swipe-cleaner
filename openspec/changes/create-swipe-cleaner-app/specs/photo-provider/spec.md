## ADDED Requirements

### Requirement: Shuffled Photo Pool Retrieval
The system SHALL query the Android `MediaStore` ContentResolver to obtain the URIs of all images in the gallery. It SHALL cross-reference these URIs with the kept photos database to exclude previously kept photos, and then shuffle the remaining URIs to form a random queue for the current session.

#### Scenario: Build Shuffled Session Queue
- **WHEN** the swiping session is initialized
- **THEN** the system queries MediaStore, subtracts URIs that exist in the kept photos database, shuffles the list, and provides the queue.

### Requirement: MediaStore Trashing Execution
The system SHALL process the in-memory delete queue by requesting the Android OS to move the specified photo URIs to the system trash via `MediaStore.createTrashRequest` (for Android 11+) or the appropriate fallback for older versions, resulting in a single system dialog prompting the user to confirm the deletion.

#### Scenario: Move Marked Photos to Trash
- **WHEN** the user triggers the "Move to Trash" command on the Completion Screen
- **THEN** the system requests a MediaStore trash operation for all URIs in the delete queue, showing the OS confirmation dialog.
