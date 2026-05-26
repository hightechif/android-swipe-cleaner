## Why

Currently, photos swiped left (marked for deletion) are tracked in an in-memory queue. If the app process is terminated, the phone restarts, or the user resets all kept photos, these pending deletions are lost, causing previously swiped-left photos to reappear on the active card deck. Tracking these in local database storage ensures swiping progress is never lost.

## What Changes

- **Persistent Trashed Photos Table**: Create a `trashed_photos` database table in Room to store the URIs of photos queued for deletion.
- **Database Operations**: Insert into this table on swiping left, and query it to filter out pending deletions from the active swipe pool.
- **Wipe on Commit**: Delete database entries from `trashed_photos` only after a user executes and completes the MediaStore system trash request.
- **Undo Restoration**: On restoring a photo from the Trash tab, delete its URI from the `trashed_photos` database table and return it to the swipe pool.

## Capabilities

### New Capabilities
- None

### Modified Capabilities
- `photo-cleaner-app`: Modifying the Trash Queue requirements to persist and sync queued-for-deletion items in a local database.

## Impact

- **Database & Repository Layer**: Add `TrashedPhotoEntity`, `TrashedPhotoDao` inside `AppDatabase.kt` and a new `TrashedPhotosRepository` layer.
- **ViewModel & UI**: Update `SwipeViewModel.kt` to flow trashed photos from the database rather than tracking them in an in-memory list.
