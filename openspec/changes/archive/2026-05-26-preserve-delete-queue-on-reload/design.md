## Context

When the user completes a swipe session and clicks "Review Remaining Photos," they expect to swipe only the remaining un-swiped gallery photos. However, because photos swiped left (marked for deletion) have not yet been permanently deleted via the MediaStore trash API, they are re-queried and re-added to the swipe stack, which is confusing.

## Goals / Non-Goals

**Goals:**
- Add `keepDeleteQueue` option to the `loadPhotoPool` function in `SwipeViewModel`.
- If `keepDeleteQueue` is true, preserve the contents of the `deleteQueue` and filter these items out from the newly loaded `photoPool`.
- Wire `loadPhotoPool(keepDeleteQueue = true)` to the "Review Remaining Photos" button click in `SwipeScreen`.

**Non-Goals:**
- Persistent storage of the `deleteQueue` across app relaunches (since the MediaStore trash process is designed to be session-based).

## Decisions

### Decision 1: In-memory filtering instead of database persistence for deleted queue
- **Choice**: Filter `photoPool` by removing items present in `deleteQueue` during reload.
- **Alternatives Considered**: Creating a database table for deleted photos.
- **Rationale**: Since the trash queue is temporary and meant to be finalized in the same session, adding Room tables for delete states introduces unnecessary disk read/write overhead and schema migration complexity. Filtering in-memory is fast and keeps the architecture lightweight.

## Risks / Trade-offs

- **[Risk] High-Memory or Large Queue Overhead**: If the user has a massive `deleteQueue`, filtering it in-memory might cause slight overhead.
  - *Mitigation*: The `deleteQueue` typically holds dozens or hundreds of items, which is trivial to filter using a `Set` in Kotlin (`pool.filter { it !in deleteQueueSet }`).
