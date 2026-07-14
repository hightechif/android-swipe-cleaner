## Context

The application currently queries the Android MediaStore for all images and returns them as a single list. We need to introduce the capability to query folder/album hierarchies from the MediaStore, filter the swipe review stack dynamically, and present a folder-based gallery on the Kept screen.

## Goals / Non-Goals

**Goals:**
- Extend the `MediaStoreRepository` to query all distinct photo albums and retrieve URIs filtered by `BUCKET_ID`.
- Expose a folder selector on the Swipe Screen to choose between "All Photos" and a specific album.
- Update the Kept Screen to support two view modes: "All Kept Photos" and "Kept Photos Albums".
- Implement drill-down browsing for the Kept Albums mode.
- Update the Kept photo interaction: single-tap to view fullscreen (zoom/pan) and long-press to restore.

**Non-Goals:**
- Modifying Room database schemas (keep `KeptPhotoEntity` as is to avoid database migrations). All folder mappings will be resolved dynamically at runtime by intersecting database state with MediaStore metadata.
- Storing individual album structures in the local SQLite database.

## Decisions

### Decision 1: Dynamic MediaStore-to-Database Mapping
Expose `MediaImage` (containing URI, bucket ID, and bucket name) from the repository, and map `KeptPhotoEntity` entries to their respective folders in memory.
- **Rationale**: Since the phone's gallery folders are highly dynamic (folders can be created, deleted, or renamed externally), storing folder names in the SQLite database leads to stale data. Querying MediaStore and joining/grouping URIs in memory provides up-to-date real-time structure and eliminates Room database migration risks.
- **Alternatives Considered**: 
  - *Add `bucketId` column to SQLite database*: Requires Room migration (schema version bump) and custom synchronization logic when files are moved externally.

### Decision 2: UI-Level Drill-down Navigation for Kept Screen
Manage the current tab's drill-down views (view mode, selected album, and back button navigation) using local Composable states rather than full Jetpack Navigation routing.
- **Rationale**: The Kept gallery flow is a nested visual state within a single tab. Managing it locally via `remember { mutableStateOf(...) }` keeps the main `SwipeCleanerApp` NavHost simple, prevents state leaking across tab transitions, and makes state cleanup trivial.

### Decision 3: Jetpack Compose `combinedClickable` for Gestures
Use `Modifier.combinedClickable` on the photo cards in the Kept Photos screen to handle tap and long-press events.
- **Rationale**: This is the standard Compose API for multi-gesture clicks. Single tap invokes `onClick` (triggering zoom overlay), and long press invokes `onLongClick` (triggering the restore dialog).

## Risks / Trade-offs

- **[Risk]** Heavy in-memory grouping on devices with tens of thousands of photos could cause UI frames to drop.
  - **[Mitigation]** The MediaStore queries will be performed entirely off the main thread using `Dispatchers.IO`. The resulting list will be mapped to a HashSet for $O(1)$ lookups, and grouping will be performed during background processing.
