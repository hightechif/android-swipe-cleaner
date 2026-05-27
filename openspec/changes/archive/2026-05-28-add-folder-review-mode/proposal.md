## Why

Currently, the application only allows users to review all photos across their entire phone gallery. For users with large galleries, swiping through thousands of photos is exhausting and unmanageable. Introducing folder-based review and a more structured kept photo gallery allows users to clean up specific folders sequentially and navigate their kept photos with ease.

## What Changes

- **Folder Selection on Swipe Screen**: Add a dropdown folder selector in the Swipe screen header allowing users to swipe through a specific album or all photos.
- **Kept Screen View Modes**: Add a dropdown selector on the Kept Photos tab with two options:
  - **All Kept Photos**: Displays a flat grid of all kept photos.
  - **Kept Photos Albums**: Displays a grid of folders that contain kept photos. Tapping a folder drills down to display only the kept photos inside that folder.
- **Updated Kept Screen Gestures**:
  - Single tap on a kept photo opens the fullscreen zoomable viewer.
  - Long press on a kept photo displays the confirmation dialog to restore the photo back to the swipe deck.

## Capabilities

### New Capabilities
<!-- None -->

### Modified Capabilities
- `photo-cleaner-app`: Update Swipe Screen and Kept Photos Screen requirements to support folder selection, gallery/album views, and tap/long-press interactions.

## Impact

- **UI Layer (`SwipeScreen.kt`)**: Implement album selectors on Swipe screen, add view mode switcher on Kept screen, add sub-view for kept albums, and update click handlers to use `combinedClickable`.
- **Data Layer (`MediaStoreRepository.kt`)**: Add methods to fetch distinct albums and retrieve images belonging to a specific bucket ID.
- **ViewModel (`SwipeViewModel.kt`)**: Maintain state for active album selection, albums list, and load photo pools accordingly.
