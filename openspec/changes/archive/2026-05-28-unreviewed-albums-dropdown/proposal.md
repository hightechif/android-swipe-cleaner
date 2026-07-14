## Why

Currently, the folder selection dropdown on the Swipe Screen displays all albums on the device, including those where all photos have already been reviewed (kept or trashed), and shows the total photos count rather than the remaining unreviewed count. This clutters the UI with empty folders and makes it difficult for the user to identify which folders actually have pending photos left to clean.

## What Changes

- Filter the albums dropdown list to only display albums that contain at least one unreviewed photo (neither kept in the database nor queued in the trash).
- Dynamically update the quantity count shown for each album option in the dropdown to reflect only the number of remaining unreviewed photos (unreviewed count = total photos - kept photos - trashed photos).
- Update the dropdown list and quantity counts dynamically in real-time as the user reviews photos (swipes left or right) or restores them from kept/trash tabs.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `photo-cleaner-app`: Update the folder filtering requirement to specify that the dropdown options must only show folders with unreviewed photos, displaying their unreviewed counts, and update dynamically.

## Impact

- `SwipeViewModel`: Modify how albums are loaded/computed, moving from a static repository query to a dynamically combined reactive flow of media images, kept database entries, and trashed database entries.
- `SwipeScreen`: The UI folder selection dialog will automatically render the updated dynamic albums list and unreviewed photo counts from the ViewModel.
