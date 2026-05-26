## Context

SwipeCleaner follows Clean Architecture + MVVM. The key layers are:
- **UI Layer**: `SwipeActivity`, `SwipeViewModel`
- **Domain Layer**: `GetMediaUseCase`, `FilteredMediaUseCase`
- **Data Layer**: `TrashedPhotosRepository`, `TrashedPhotoDao`, `PhotoProvider`

Currently there are two placeholder files (`DummyUnitTest.kt`, `DummyAndroidTest.kt`) that exist solely to prevent Kapt stub generation failures. The project has no real tests.

All business logic is in the `test` (JVM) source set scope — no Android framework is needed for the core classes, making JUnit 4 + coroutines-test the right choice.

## Goals / Non-Goals

**Goals:**
- Cover `SwipeViewModel` swipe, undo, and trash-queue state transitions
- Cover `TrashedPhotosRepository` CRUD operations with a mocked DAO
- Cover `GetMediaUseCase` / `FilteredMediaUseCase` filtering & sorting logic
- Cover `TrashedPhotoDao` queries with a Room in-memory database
- Cover `PhotoProvider` cursor-to-model mapping
- All tests runnable via `./gradlew test` with no emulator required

**Non-Goals:**
- UI/Espresso instrumentation tests (separate effort)
- End-to-end integration tests
- Code coverage enforcement thresholds (can be added later)

## Decisions

### D1: JUnit 4 over JUnit 5
**Decision**: Use JUnit 4.  
**Rationale**: Android's `testImplementation` stack (especially `androidx.arch.core:core-testing` and `InstantTaskExecutorRule`) has first-class JUnit 4 support. JUnit 5 requires an extra extension engine plugin on Android and adds friction without meaningful benefit here.  
**Alternative considered**: JUnit 5 — rejected due to setup complexity.

### D2: MockK over Mockito-Kotlin for ViewModel tests
**Decision**: Use MockK for coroutine-aware mocking.  
**Rationale**: MockK supports `suspend` functions natively and has a cleaner DSL for Kotlin. Mockito-Kotlin requires extra workarounds for coroutines.  
**Alternative considered**: Mockito-Kotlin — kept as a fallback if MockK conflicts arise.

### D3: Room in-memory database for DAO tests
**Decision**: Use `Room.inMemoryDatabaseBuilder` in JVM tests backed by Robolectric.  
**Rationale**: DAO queries need a real SQLite engine. In-memory Room + Robolectric is the lightest way to achieve this without an emulator.  
**Alternative considered**: Mock the DAO — rejected because it would not test query correctness.

### D4: `testOptions.unitTests.returnDefaultValues = true`
**Decision**: Enable this Gradle flag.  
**Rationale**: Allows AndroidX `LiveData` / `ViewModel` to be instantiated in JVM tests without crashing on unmocked Android statics.  
**Alternative considered**: Full Robolectric for all tests — too slow and heavyweight.

### D5: `InstantTaskExecutorRule` for LiveData / StateFlow
**Decision**: Use `InstantTaskExecutorRule` + `kotlinx-coroutines-test` `TestCoroutineDispatcher` in ViewModel tests.  
**Rationale**: Ensures LiveData updates are synchronous during tests and coroutines complete before assertions.

## Risks / Trade-offs

- **[Risk] Robolectric version mismatch** → Mitigation: Pin Robolectric to the version recommended by the current `compileSdk` (34).
- **[Risk] MockK binary compatibility** → Mitigation: Use `mockk:1.13.x` which is stable against Kotlin 1.9.
- **[Risk] ViewModel constructor injection** → If `SwipeViewModel` relies on `Application` context, tests may require `AndroidX test` application. Mitigation: Refactor to inject `TrashedPhotosRepository` via constructor (already partially done).
- **[Trade-off] Robolectric for DAO tests is slow (~5-10s per class)** → Acceptable for a project of this size.

## Migration Plan

1. Add test dependencies to `app/build.gradle`
2. Enable `testOptions.unitTests.returnDefaultValues = true`
3. Create test classes in `app/src/test/java/com/hightechif/swipecleaner/`
4. Remove `DummyUnitTest.kt` (replaced by real tests)
5. Run `./gradlew test` to verify all pass
