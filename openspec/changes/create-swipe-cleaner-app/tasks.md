## 1. Project Setup and Configuration

- [ ] 1.1 Configure `build.gradle.kts` files to add Koin, Room, Lottie, Coil, and Compose Navigation dependencies.
- [ ] 1.2 Setup Android Manifest storage permissions, application class, and basic theme.
- [ ] 1.3 Initialize Koin dependency injection container in custom `Application` class.

## 2. Room Database and Data Layer

- [ ] 2.1 Create the Room database entity `KeptPhoto` with URI and timestamp fields.
- [ ] 2.2 Implement Room database DAO `KeptPhotoDao` with query, insert, and delete methods.
- [ ] 2.3 Create `AppDatabase` class and write `KeptPhotosRepository` interface and implementation.
- [ ] 2.4 Implement `MediaStoreRepository` to query gallery images and execute batch trash request.

## 3. Domain Layer and DI Module Setup

- [ ] 3.1 Implement `GetShuffledPhotoPoolUseCase` to query all gallery URIs, subtract kept URIs, and return shuffled list.
- [ ] 3.2 Implement `MarkImageKeptUseCase` and `ResetKeptPhotosUseCase` to interact with Room database repository.
- [ ] 3.3 Implement `ExecuteTrashRequestUseCase` to invoke `MediaStoreRepository` trash request.
- [ ] 3.4 Define and load Koin modules for repositories, database, use cases, and ViewModels.

## 4. UI Layer - Navigation and Permission Onboarding

- [ ] 4.1 Create Compose-based navigation graph (`NavHost`) linking Permission, Swipe, Completion, and Kept Photos screens.
- [ ] 4.2 Create `PermissionScreen` checking storage permissions and displaying onboarding layout.
- [ ] 4.3 Implement permission request launchers and auto-transition logic to the Swipe Screen.

## 5. UI Layer - Swipe Screen (Card Stack and Drag Gesture)

- [ ] 5.1 Implement `SwipeViewModel` managing current pool index, session stats, delete queue, and navigation actions.
- [ ] 5.2 Build `SwipeableCard` composable with custom horizontal pointer input detection.
- [ ] 5.3 Implement rotation/tilt and colored overlay transitions (red left, green right) on card drag.
- [ ] 5.4 Implement card dismissal animation and scale up animation for the card directly underneath.

## 6. UI Layer - Completion and Kept Photos Screens

- [ ] 6.1 Create `CompletionScreen` showing session stats and playing a Lottie confetti animation automatically.
- [ ] 6.2 Bind "Move to Trash" button to execute MediaStore system trash request.
- [ ] 6.3 Bind "Reset Progress" button to purge database and return to Swipe Screen.
- [ ] 6.4 Implement `KeptPhotosScreen` to show kept photos grid and a back/reset option.

## 7. Verification and Cleanup

- [ ] 7.1 Verify permission requests work on Android 12, 13, and 14.
- [ ] 7.2 Verify swiping behavior, card rotation, and overlay visual feedback.
- [ ] 7.3 Verify Room database exclusion on fresh launch.
- [ ] 7.4 Verify MediaStore system trash request dialog triggers correctly.
