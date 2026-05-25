## Context

The Swipe Cleaner Android application aims to provide a fast, fun, and easy way to clean up the local photo gallery using horizontal swipe gestures (swipe left to delete, swipe right to keep). The application must follow modern Android guidelines, using Jetpack Compose, Material 3, and Koin for dependency injection.

## Goals / Non-Goals

**Goals:**
- Provide a responsive Tinder-style swiping interface with rotating cards and color overlays (red for delete, green for keep).
- Use `MediaStore` to directly query and trash images, with batch confirmation for deletion.
- Persist the list of kept images locally using Room DB to filter them out of subsequent pools.
- Keep dependency injection lightweight using Koin.
- Celebrate session completion using a Lottie confetti animation regardless of trash approval status.
- Allow viewing all kept photos and resetting all progress (clearing Room DB) to start over.

**Non-Goals:**
- Advanced media filters, folders, or screenshot/duplicate detection (postponed for simplicity).
- Direct file system modification bypassing Android's `MediaStore`.
- Cloud syncing or backup of swiping progress.

## Decisions

### 1. Swiping UI Card Gestures (Option C)
- **Decision**: Use a custom `Modifier.pointerInput` with `detectHorizontalDragGestures` instead of standard `SwipeToDismiss` or `AnchoredDraggableState`.
- **Rationale**: Gives full custom control over card rotation/tilt and opacity/tint of overlays.
- **Rotation formula**: `rotationZ = (dragOffset / screenWidth) * 15f` (tilts card up to 15 degrees).
- **Overlay opacity**: `alpha = min(abs(dragOffset) / (screenWidth * 0.4f), 1f)` (full overlay tint by 40% screen width drag).
- **Alternatives considered**:
  - `AnchoredDraggableState`: Harder to calculate dynamic tilt/rotation alongside custom overlay fades.
  - `SwipeToDismiss`: Very limited customization, does not support stacking and rotation aesthetics.

### 2. Dependency Injection
- **Decision**: Use Koin for DI.
- **Rationale**: Meets user preference for a lightweight and clean Kotlin-first DI framework.
- **Implementation**: Set up `appModule` containing Room DB instance, repositories, UseCases, and ViewModels, started in `Application.onCreate()`.
- **Alternatives considered**: Hilt (ruled out per user request).

### 3. Gallery Access & Data Lifecycle (Thread B)
- **Decision**: Query the Android `MediaStore` ContentResolver for all image URIs, query Room DB for kept image URIs, subtract the kept URIs from the list, shuffle the remaining URIs, and load them in an in-memory queue.
- **Rationale**: Shuffling the URIs in-memory is memory-efficient compared to loading bitmap data, while Room DB ensures kept images are never shown again on subsequent launches. Every new launch starts a fresh random pool of un-reviewed photos.
- **Alternatives considered**:
  - Room DB storing all images (redundant replication of MediaStore).
  - Shuffling all images directly in SQL (MediaStore queries don't support custom SQLite sorting/shuffling efficiently).

### 4. Deletion Strategy (Option B / Thread A)
- **Decision**: Maintain a session-level delete queue (list of URIs). When the user completes the session and clicks "Move to Trash" on the Completion Screen, trigger `MediaStore.createTrashRequest` (Android 11+).
- **Rationale**: Moves files to the Android system trash (restorable within 30 days) and requires only a single OS confirmation dialog for the entire batch.
- **Celebration Flow**: Celebrate with a Lottie confetti animation on the Completion Screen regardless of whether the user confirms or cancels the OS trash dialog.
- **Alternatives considered**:
  - Immediate deletion (`ContentResolver.delete`): Risk of data loss with no undo.
  - Custom internal app trash: Redundant duplication of Android 11+'s built-in Trash API.

### 5. Database Schema
- **Database**: Room DB (`kept_photos.db`).
- **Entity**: `KeptPhoto`
  - `uri` (String, Primary Key)
  - `keptAt` (Long, Unix timestamp)

```
┌──────────────────────────────────────────────┐
│                  KeptPhoto                   │
├──────────────────────────────────────────────┤
│ PK uri: String                               │
│    keptAt: Long                              │
└──────────────────────────────────────────────┘
```

## Risks / Trade-offs

| Risk | Mitigation |
| :--- | :--- |
| **Accidental Swipe Left (Delete)** | Photos are not deleted immediately; they are added to an in-memory delete queue, and the actual move to Trash happens via a system-level batch dialog at the end, which the user can cancel. |
| **Android 14+ Partial Permissions** | If the user grants partial access (`READ_MEDIA_VISUAL_USER_SELECTED`), the `MediaStore` query only returns selected photos. The app SHALL work seamlessly with this subset. |
| **Memory Leak from Image Card Bitmaps** | Preload only 2-3 cards ahead using Coil. Coil automatically caches and recycles memory when cards are disposed. |
