## Why

Users currently have no quick method to restore all kept photos back into their swiping pool from the Kept Photos tab. They must either reset photos individually or perform a full application reset. A "Restore All" action will allow restarting or re-auditing all kept photos at once.

## What Changes

- **Restore All Button**: Add a "Restore All" action button (e.g. standard delete-sweep or reset icon) to the header bar of the Kept Photos tab.
- **Reset Confirmation Dialog**: Show a confirmation alert dialog when the user clicks the "Restore All" button to prevent accidental resets.
- **Progress Reset & Reload**: On confirmation, delete all records from the kept photos database and automatically refresh/reload the swiping card pool, switching the active tab back to the Swipe Deck.

## Capabilities

### New Capabilities
- None

### Modified Capabilities
- `photo-cleaner-app`: Modifying the Kept Photos Screen requirements to support resetting all kept photos at once.

## Impact

- **UI & ViewModel Layer**: `SwipeScreen.kt` (adding the header action, dialog, and click callbacks) and `SwipeViewModel.kt` (adding `resetAllKept` action and reloading the pool).
