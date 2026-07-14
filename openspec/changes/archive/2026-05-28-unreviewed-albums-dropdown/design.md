## Context

The Swipe Screen folder selection dropdown shows all albums on the device, regardless of whether they contain any pending, unreviewed photos. The photo counts in this list are also static total counts. When photos are reviewed, these counts do not decrease, and fully reviewed folders do not disappear from the list.

## Goals / Non-Goals

**Goals:**
- Dynamically filter the folder selector list on the Swipe Screen to only show folders that contain at least one unreviewed photo.
- Compute the quantity of unreviewed photos dynamically (unreviewed = total photos in folder - kept photos in folder - trashed photos in folder).
- Keep the dropdown list and counts synchronized in real-time by reactively collecting database flow emissions on swipe/restore events.

**Non-Goals:**
- Changing the folder view behavior in the Kept Photos tab (which displays reviewed/kept folders only).
- Changing the MediaStore content resolver database queries (the underlying media repository queries).

## Decisions

### 1. Reactive Combine Flow in SwipeViewModel
We will use Kotlin's `combine` flow operator to merge three data sources:
1. `_mediaImages` (StateFlow containing all media images with their bucket IDs and paths).
2. `keptPhotos` (StateFlow from Room database flow of kept photo entities).
3. `trashedPhotosRepository.getTrashedPhotosFlow()` (Flow from Room database of trashed photo entities).

Whenever any of these lists emit a new value (e.g., when a photo is swiped to kept/trashed, or restored, or when trash is emptied), we will recalculate the unreviewed albums and counts in memory:
- Filter out media images whose URIs are in the `kept` or `trashed` sets.
- Group the remaining unreviewed images by `bucketId`.
- Construct `Album` instances with the unreviewed photo counts and the first image in each bucket group as the cover photo.
- Order the computed albums list alphabetically by name.

*Alternative Considered*: Modify the database queries to do SQL-level joins. However, since the media storage is queried via MediaStore content resolver queries and the kept/trashed status is stored in local Room databases, doing joins at the repository/database level is highly complex and inefficient. Computing it reactively in-memory is cleaner, simpler, and very fast.

## Risks / Trade-offs

- **[Performance on Large Galleries]** → If the device has tens of thousands of photos, executing the filter check on each item on every swipe could potentially cause UI lag.
  - *Mitigation*: We convert the list of kept/trashed photo URIs to a `HashSet` before looping, allowing `O(1)` check time per photo URI. The entire combine logic runs inside `viewModelScope.launch`, which processes off the main thread.
