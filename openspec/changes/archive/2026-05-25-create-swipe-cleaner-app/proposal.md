## Why

Many users have cluttered phone galleries filled with screenshots, blurry photos, and duplicates, making it tedious to clean up. This project proposes a simple, intuitive, and interactive Android application utilizing a Tinder-like swiping gesture (left to delete via system trash, right to keep) to help users clean their gallery efficiently, one random image at a time, following the latest Android and Jetpack Compose guidelines.

## What Changes

- **NEW** Jetpack Compose-based Swipe Cleaner Android App.
- **NEW** First-launch Permission flow that checks for storage permissions and requires access before showing the swiping interface.
- **NEW** Swipe Screen featuring a Tinder-style card interface using custom drag gesture detection where:
  - Swipe Left: Adds the photo to an in-memory delete queue.
  - Swipe Right: Persists the photo as "kept" in a local Room database.
- **NEW** MediaStore API integration for fetching, shuffling, preloading, and trashing photos.
- **NEW** Room Database integration to track kept photo URIs and exclude them from subsequent session pools.
- **NEW** Dependency Injection framework powered by Koin.
- **NEW** Completion Screen with a Lottie confetti animation celebrating reviewed photos, with options to:
  - Move marked photos to Android System Trash via a single MediaStore trash request dialog.
  - View all kept photos in a grid layout.
  - Reset all kept photos database entries to start reviews over from scratch.
- **NEW** Kept Photos Screen showing a grid layout of all photos that have been swiped right (kept), with the option to go back or reset.

## Capabilities

### New Capabilities
- `photo-cleaner-app`: Handles the main navigation flow, permission check/onboarding view, the swiping UI card-stack animation, and completion/kept photos views.
- `photo-provider`: Handles queries to MediaStore for fetching images, preloading bitmaps using Coil, shuffling image lists, and executing the bulk Android MediaStore trash request dialog.
- `kept-photos-db`: Handles persistent storage of kept photo URIs in Room, checking against them to exclude from current pools, and resetting database records.

### Modified Capabilities

None.

## Impact

- **New Application Codebase**: Initiates the Android application structure under the repository root.
- **APIs and Frameworks**: Uses Android MediaStore APIs, Room Database, Koin DI, Jetpack Compose, Material 3, Jetpack Navigation, Coil 3 (image loading), and Lottie (Lottie-Compose).
- **Permissions**: Requires storage permissions (`READ_MEDIA_IMAGES` and `READ_MEDIA_VISUAL_USER_SELECTED` on Android 13/14+, and `READ_EXTERNAL_STORAGE` on older APIs).
