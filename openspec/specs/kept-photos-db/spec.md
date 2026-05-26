# kept-photos-db Specification

## Purpose
TBD - created by archiving change create-swipe-cleaner-app. Update Purpose after archive.
## Requirements
### Requirement: Kept Photos Persistent Storage
The system SHALL maintain a local persistent database using Room to record the URIs of all photos that the user has swiped right (marked as kept). Each entry in the database table SHALL include the URI string (as primary key) and the timestamp of insertion.

#### Scenario: Save Kept Photo URI
- **WHEN** a photo is marked as kept (swiped right)
- **THEN** the system inserts a record with the photo's URI into the database.

### Requirement: Reset Kept Photos Database
The system SHALL provide a function to clear all records from the kept photos database. Clearing the database SHALL make all gallery photos eligible for inclusion in the random pool again during the next session.

#### Scenario: Reset Progress
- **WHEN** the user requests a reset of the kept photos progress
- **THEN** the system purges all entries from the kept photos database table.

### Requirement: Trashed Photos Persistent Storage
The system SHALL maintain a local persistent database table using Room to record the URIs of all photos that the user has swiped left (marked for deletion). Each entry in the database table SHALL include the URI string (as primary key) and the timestamp of insertion.

#### Scenario: Save Trashed Photo URI
- **WHEN** a photo is marked for deletion (swiped left)
- **THEN** the system inserts a record with the photo's URI into the trashed photos database table.

#### Scenario: Remove Trashed Photo URI
- **WHEN** a photo is restored from the trash queue
- **THEN** the system deletes the record with the photo's URI from the trashed photos database table.

### Requirement: Clear Trashed Photos Database
The system SHALL provide a function to clear all records from the trashed photos database table once the user confirms the deletion request via the OS MediaStore trash dialog.

#### Scenario: Clear Trash Progress
- **WHEN** the user completes the MediaStore trash confirmation
- **THEN** the system purges all entries from the trashed photos database table.

