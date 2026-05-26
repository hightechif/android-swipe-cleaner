## Context

To prevent swiped-left photos (marked for deletion) from returning to the swipe deck if the application process restarts or is terminated by the OS, we will transition the in-memory `deleteQueue` list to a Room-persistent database table.

## Goals / Non-Goals

**Goals:**
- Add `trashed_photos` Room database table and corresponding entity `TrashedPhotoEntity`.
- Define DAO methods to query all, insert, delete single, and delete all.
- Expose a `TrashedPhotosRepository` and wire it to the Koin module.
- Update `SwipeViewModel` to observe `trashed_photos` flow from the database and keep the UI states in sync in real time.
- Update `GetShuffledPhotoPoolUseCase` to filter out both `kept` and `trashed` photos.

**Non-Goals:**
- Storing files in a custom app directory (files must remain in device gallery/MediaStore).
- Storing backup metadata.

## Decisions

### Decision 1: Create a separate `trashed_photos` Room table
- **Choice**: Implemented `TrashedPhotoEntity` as a separate table.
- **Alternatives Considered**: Adding a status column to the `kept_photos` table.
- **Rationale**: Keeping them separate keeps the existing Room migration path clean and preserves the semantic separation of "Kept" vs. "Trash Queue".

### Decision 2: ViewModel Flow binding
- **Choice**: Bind the database-backed `trashed_photos` flow directly into `SwipeViewModel` using Kotlin Coroutines `stateIn()`.
- **Alternatives Considered**: Manual fetching on every swipe.
- **Rationale**: Real-time Flow observation ensures the Trash tab counter badge and grid automatically update without manual reload triggers, keeping the code clean.

## Risks / Trade-offs

- **[Risk] Room Migration issues**: Modifying the Room database schema without updating version numbers will cause crashes on launch for existing users.
  - *Mitigation*: Increment the Room database version inside `AppDatabase.kt` and use fallbackToDestructiveMigration() or a migration script.
