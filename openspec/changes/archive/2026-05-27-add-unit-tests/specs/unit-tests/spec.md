## ADDED Requirements

### Requirement: ViewModel swipe decisions are testable
The `SwipeViewModel` SHALL expose swipe-left (trash) and swipe-right (keep) state transitions that can be verified under JVM tests without an Android emulator.

#### Scenario: Swipe left moves photo to trash queue
- **WHEN** `onSwipeLeft()` is called with a valid photo in the current stack
- **THEN** the photo is added to the `TrashedPhotosRepository` and removed from the displayed stack

#### Scenario: Swipe right keeps the photo
- **WHEN** `onSwipeRight()` is called
- **THEN** the photo is removed from the displayed stack without being added to the trash repository

#### Scenario: Undo restores last trashed photo
- **WHEN** `onUndo()` is called after a swipe-left action
- **THEN** the most-recently trashed photo is restored and re-added to the displayed stack

### Requirement: Repository CRUD operations are testable
The `TrashedPhotosRepository` SHALL expose add, restore, delete, and list operations that can be verified using a mocked DAO.

#### Scenario: Add photo to trash
- **WHEN** `addToTrash(photo)` is called
- **THEN** the DAO's insert method is called with the corresponding entity

#### Scenario: Restore photo from trash
- **WHEN** `restorePhoto(id)` is called
- **THEN** the DAO's delete-by-id method is called and the photo is returned

#### Scenario: Get all trashed photos
- **WHEN** `getTrashedPhotos()` is called
- **THEN** a list of all entities from the DAO is returned mapped to domain models

### Requirement: UseCase filtering logic is testable
`GetMediaUseCase` and `FilteredMediaUseCase` SHALL filter and sort photo lists deterministically so tests can assert exact output given fixed input.

#### Scenario: Filter excludes non-image files
- **WHEN** a media list containing non-image MIME types is provided
- **THEN** only image files are present in the returned list

#### Scenario: Sort by date descending
- **WHEN** a list of photos with mixed `dateModified` values is provided
- **THEN** the returned list is sorted newest-first

### Requirement: DAO queries are verified against an in-memory database
`TrashedPhotoDao` SHALL be tested using a Room in-memory database to ensure SQL query correctness.

#### Scenario: Insert and retrieve entity
- **WHEN** an entity is inserted into the in-memory database
- **THEN** it can be retrieved by its ID with all fields intact

#### Scenario: Delete entity
- **WHEN** an entity is deleted by ID
- **THEN** it is no longer present in the table

#### Scenario: Get all returns empty list when table is empty
- **WHEN** no entities have been inserted
- **THEN** `getAll()` returns an empty list

### Requirement: PhotoProvider cursor mapping is testable
`PhotoProvider` SHALL map `Cursor` rows to domain `Photo` models correctly, testable via mocked cursors.

#### Scenario: Valid cursor row maps to Photo model
- **WHEN** a cursor with valid column values is provided
- **THEN** the returned `Photo` has matching `id`, `uri`, `name`, and `dateModified` fields

#### Scenario: Empty cursor returns empty list
- **WHEN** a cursor with zero rows is provided
- **THEN** the returned list is empty
