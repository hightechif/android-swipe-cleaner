## 1. Gradle Setup

- [x] 1.1 Add `testOptions.unitTests.returnDefaultValues = true` to `app/build.gradle`
- [x] 1.2 Add `kotlinx-coroutines-test` dependency to `testImplementation`
- [x] 1.3 Add `mockk` dependency to `testImplementation`
- [x] 1.4 Add `androidx.arch.core:core-testing` (InstantTaskExecutorRule) to `testImplementation`
- [x] 1.5 Add `robolectric` dependency to `testImplementation` for Room in-memory DAO tests
- [x] 1.6 Sync Gradle and confirm `./gradlew testDebugUnitTest` compiles successfully

## 2. DAO Tests

- [x] 2.1 Create `TrashedPhotoDaoTest.kt` in `app/src/test/java/com/hightechif/swipecleaner/data/db/`
- [x] 2.2 Implement test: insert entity then retrieve by ID returns matching fields
- [x] 2.3 Implement test: delete entity by ID removes it from the table
- [x] 2.4 Implement test: `getAll()` returns empty list when table is empty
- [x] 2.5 Implement test: `getAll()` returns all inserted entities

## 3. Repository Tests

- [x] 3.1 Create `TrashedPhotosRepositoryTest.kt` in `app/src/test/java/com/hightechif/swipecleaner/data/repository/`
- [x] 3.2 Implement test: `insertTrashedPhoto(uri)` calls DAO insert with correct entity
- [x] 3.3 Implement test: `deleteTrashedPhoto(uri)` calls DAO delete with correct URI
- [x] 3.4 Implement test: `getTrashedPhotos()` maps all DAO entities to domain models

## 4. UseCase Tests

- [x] 4.1 Create `GetShuffledPhotoPoolUseCaseTest.kt` in `app/src/test/java/com/hightechif/swipecleaner/domain/usecase/`
- [x] 4.2 Implement test: filtering excludes kept photos from result
- [x] 4.3 Implement test: filtering excludes trashed photos from result
- [x] 4.4 Skipped — `FilteredMediaUseCase` does not exist as a separate class; logic is in `GetShuffledPhotoPoolUseCase`
- [x] 4.5 Implement test: filtered list matches expected output for given input set

## 5. ViewModel Tests

- [x] 5.1 Create `SwipeViewModelTest.kt` in `app/src/test/java/com/hightechif/swipecleaner/ui/viewmodel/`
- [x] 5.2 Set up `InstantTaskExecutorRule` and `StandardTestDispatcher`
- [x] 5.3 Implement test: `swipeLeft()` calls trash repository insert and advances index
- [x] 5.4 Implement test: `swipeRight()` does not call trash repository and increments keptCount
- [x] 5.5 Implement test: `restoreFromTrash()` calls delete on repository and re-inserts into pool

## 6. MediaStoreRepository / PhotoProvider Tests

- [x] 6.1 Create `MediaStoreRepositoryTest.kt` in `app/src/test/java/com/hightechif/swipecleaner/data/repository/`
- [x] 6.2 Implement test: queryAllImageUris returns list with correct URIs
- [x] 6.3 Implement test: queryAllImageUris returns empty list when no images

## 7. Cleanup and Verification

- [x] 7.1 Delete `DummyUnitTest.kt` (replaced by real test files)
- [x] 7.2 Run `./gradlew testDebugUnitTest` — 25/25 tests pass
- [x] 7.3 Confirm `./gradlew assembleDebug` still succeeds after cleanup
