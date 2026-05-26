## Context

The Kept Photos tab displays all photos stored in the database. Currently, users can reset individual photos but cannot clear all kept photos at once within the unified session. 

## Goals / Non-Goals

**Goals:**
- Add a "Reset All" button in the Kept Photos header.
- Present a confirmation dialog before clearing all kept photos.
- Delete all kept photos in the local database and reload the swipe card deck.

**Non-Goals:**
- Modifying image assets in any way.
- Affecting the delete queue or media store trash request state.

## Decisions

### Decision 1: Place "Reset All" in the header of the Kept Photos tab
- **Choice**: Display a reset icon (e.g. `Icons.Default.DeleteSweep` or a reset icon) in the header of the Kept tab.
- **Alternatives Considered**: A floating button in the grid or a button in the app settings.
- **Rationale**: An icon button in the header matches the design pattern used in the original `KeptPhotosScreen` and keeps the screen layout clean.

### Decision 2: Reloading the deck and navigating back to Swipe tab on reset
- **Choice**: Upon confirming reset, execute database wipe, reload the photo pool, and switch `activeTab` to `SwipeTab.SWIPE`.
- **Alternatives Considered**: Remaining on the Kept Photos tab (which will then show an empty state).
- **Rationale**: Switching back to the Swipe Deck gives the user immediate visual confirmation that the photos have returned to the stack, making the transition seamless.

## Risks / Trade-offs

- **[Risk] Accidental Wipe**: A user might click the reset all button accidentally, losing their session progress.
  - *Mitigation*: Require a clear, explicit confirmation dialog before executing the database delete.
