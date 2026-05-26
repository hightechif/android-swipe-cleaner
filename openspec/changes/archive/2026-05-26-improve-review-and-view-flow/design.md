## Context

The current SwipeCleaner application allows swiping card stacks to keep or delete photos. However, users need a way to preview photos in detail and review their choices incrementally without completing the entire session.

## Goals / Non-Goals

**Goals:**
- Implement a zoomable and pannable image viewer overlay.
- Replace the current screen navigation with a unified bottom navigation bar containing Swipe, Kept, and Trash tabs.
- Provide a milestone popup every 20 swipes to review and empty trash.
- Support real-time restoring/resetting of swiped photos back to the active swipe stack.

**Non-Goals:**
- Supporting general folder management (external to the standard gallery photo provider).
- Modifying image metadata or editing files inside the viewer.

## Decisions

### Decision 1: Image Viewer Implementation (Overlay vs Navigation Screen)
- **Choice**: Implemented as a full-screen overlay component inside the `SwipeScreen`.
- **Alternatives Considered**: A separate Compose navigation destination.
- **Rationale**: Keeping the viewer as an overlay within the screen makes the transition extremely fast, preserves the local card stack animatable states, and avoids passing deep file paths or index parameters across navigation routes.

### Decision 2: Bottom Navigation hosting Swipe, Kept, and Trash
- **Choice**: Integrate the tabs inside `SwipeScreen` using a `Scaffold` and a tab selection state.
- **Alternatives Considered**: Keeping them as separate standalone navigation screens with normal deep links.
- **Rationale**: A tabbed layout lets the user easily toggle between lists. Keeping them in the same screen makes sharing the `SwipeViewModel` extremely natural, allowing direct and animated updates of badge counters.

### Decision 3: Undo/Restore insertion point
- **Choice**: Insert restored/undone photos at the `currentIndex` of the `photoPool` list.
- **Alternatives Considered**: Appending back to the end of the pool.
- **Rationale**: Placing the restored photo at `currentIndex` causes it to immediately reappear on top of the swipe stack, which is the most intuitive user behavior.

### Decision 4: Single kept photo deletion in DB
- **Choice**: Add single-item deletion to Room DAO: `@Query("DELETE FROM kept_photos WHERE uri = :uri")`.
- **Alternatives Considered**: Clearing database and reloading.
- **Rationale**: Deleting a single kept photo allows selective undoing of kept photos without losing the overall session progress.

## Risks / Trade-offs

- **[Risk] Gesture Conflicts**: Drag gestures for swiping might trigger or conflict with tap/drag/pinch gestures in the card.
  - *Mitigation*: Ensure the Fullscreen Image Viewer is rendered as a higher Z-index overlay that completely blocks and consumes touch events when open.
- **[Risk] High-Memory Usage**: Displaying high-resolution grids of kept/deleted photos along with the active swipe stack could lead to memory issues.
  - *Mitigation*: Use LazyVerticalGrid for kept/deleted lists and Coil's async image loader which handles memory caching automatically.
