# unit-tests Specification

## Purpose

Defines the testability requirements for the SwipeCleaner codebase. All business logic layers — ViewModel, Repository, UseCase, DAO, and MediaStore — SHALL be covered by automated JVM unit tests runnable via `./gradlew testDebugUnitTest` without an Android emulator.

## Requirements

### Requirement: ViewModel swipe decisions are testable
The `SwipeViewModel` SHALL expose swipe-left (trash) and swipe-right (keep) state transitions that can be verified under JVM tests without an Android emulator.

#### Scenario: Swipe left moves photo to trash queue
- **WHEN** `swipeLeft()` is called with a valid photo in the current stack
- **THEN** the photo is added to the `TrashedPhotosRepository` and the current index is advanced

#### Scenario: Swipe right keeps the photo
- **WHEN** `swipeRight()` is called
- **THEN** the kept count is incremented without calling the trash repository

#### Scenario: Undo restores last trashed photo
- **WHEN** `restoreFromTrash(uri)` is called after a swipe-left action
- **THEN** the photo is removed from trash via the repository and re-added to the displayed pool

### Requirement: Repository CRUD operations are testable
The `TrashedPhotosRepository` SHALL expose insert, delete, and list operations that can be verified using a mocked DAO.

#### Scenario: Add photo to trash
- **WHEN** `insertTrashedPhoto(uri)` is called
- **THEN** the DAO's insert method is called with an entity containing the correct URI

#### Scenario: Delete photo from trash
- **WHEN** `deleteTrashedPhoto(uri)` is called
- **THEN** the DAO's delete-by-URI method is called with the correct URI

#### Scenario: Get all trashed photos
- **WHEN** `getTrashedPhotos()` is called
- **THEN** a list of all entities from the DAO is returned

### Requirement: UseCase filtering logic is testable
`GetShuffledPhotoPoolUseCase` SHALL filter photo lists deterministically so tests can assert exact output given fixed input.

#### Scenario: Filter excludes kept photos
- **WHEN** a media list is provided that includes URIs already in the kept-photos set
- **THEN** only non-kept, non-trashed URIs are present in the returned list

#### Scenario: Filter excludes trashed photos
- **WHEN** a media list is provided that includes URIs already in the trashed-photos set
- **THEN** only non-kept, non-trashed URIs are present in the returned list

#### Scenario: Returns empty list when all filtered out
- **WHEN** all media URIs are present in either the kept or trashed sets
- **THEN** the returned list is empty

### Requirement: DAO queries are verified against an in-memory database
`TrashedPhotoDao` SHALL be tested using a Room in-memory database (via Robolectric) to ensure SQL query correctness without an emulator.

#### Scenario: Insert and retrieve entity
- **WHEN** an entity is inserted into the in-memory database
- **THEN** it can be retrieved via `getAllTrashedPhotos()` with all fields intact

#### Scenario: Delete entity
- **WHEN** an entity is deleted by URI
- **THEN** it is no longer present in the table

#### Scenario: Get all returns empty list when table is empty
- **WHEN** no entities have been inserted
- **THEN** `getAllTrashedPhotos()` returns an empty list

#### Scenario: Get all returns all inserted entities
- **WHEN** multiple entities have been inserted
- **THEN** `getAllTrashedPhotos()` returns all of them

### Requirement: MediaStore repository contract is testable
`MediaStoreRepository` SHALL expose a stable interface contract that can be verified via mocked implementations without Android system dependencies.

#### Scenario: Query returns list of URIs
- **WHEN** `queryAllImageUris()` is called and images exist
- **THEN** a non-empty list of URI strings is returned

#### Scenario: Query returns empty list when no images
- **WHEN** `queryAllImageUris()` is called and no images exist
- **THEN** an empty list is returned
