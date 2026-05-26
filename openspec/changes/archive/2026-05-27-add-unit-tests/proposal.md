## Why

The SwipeCleaner codebase has no automated tests, making it risky to evolve features like the swipe engine, trash queue, and photo filtering logic without introducing regressions. Adding a unit test suite now establishes a safety net before the project grows further.

## What Changes

- Add JUnit 4 + Mockito-Kotlin unit tests for `SwipeViewModel` (swipe decisions, undo, trash state)
- Add JUnit 4 + MockK tests for `TrashedPhotosRepository` (add, restore, delete, list operations)
- Add JUnit 4 tests for `GetMediaUseCase` / `FilteredMediaUseCase` (filtering and sorting logic)
- Add Room in-memory database tests for `TrashedPhotoDao` (CRUD + query correctness)
- Add Kotlin unit tests for `PhotoProvider` (cursor-to-model mapping, edge cases)
- Configure `testOptions.unitTests.returnDefaultValues = true` in Gradle to enable Robolectric-free ViewModel testing
- Replace the placeholder `DummyUnitTest.kt` with real test classes

## Capabilities

### New Capabilities
- `unit-tests`: JVM-level automated test suite covering ViewModel, Repository, UseCase, DAO, and PhotoProvider layers

### Modified Capabilities
- *(none — no spec-level requirement changes)*

## Impact

- **Dependencies**: Add `mockito-kotlin`, `mockk`, `kotlinx-coroutines-test`, `androidx.arch.core:core-testing` to `build.gradle`
- **Source sets**: `app/src/test/java/...` will be populated with real test classes; the dummy file will be removed
- **Build**: Test execution via `./gradlew test` — no instrumentation runner changes
- **No API or UI changes**
