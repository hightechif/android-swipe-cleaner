# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Identity

```text
Application Name    : SwipeCleaner
Package Name        : com.hightechif.swipecleaner
Min SDK             : 26
Target SDK          : 34
Compile SDK         : 34
```

No build flavors are declared (only `debug` / `release`).

## Project Layout

```text
UI Toolkit          : Jetpack Compose
Module Structure    : Single-Module
Dependency Injection: Koin   # deviates from rules/4b (Hilt) — existing project choice, do not migrate unasked
Architecture Rule   : .claude/skills/android-guide/rules/4b-compose-single-module.md
```

Detailed standards live in `.claude/skills/android-guide/rules/` (detection `1`, architecture `4b`, networking `5`, mapping `6`, local storage `7`, code quality `8`, security `9`, unit testing `10`, code review `11`). Read `1-project-detection.md`, then `4b`, then the rule for the layer being changed; read `11` before reviews.

## Agent Workflow

- Log every AI-made change to `ai.log` using `[YYYY-MM-DD HH:MM:SS] <brief description>`.
- Ask before changing ambiguous architecture, dependency, SDK, or module decisions.
- Never force push (`--force` / `--force-with-lease`); if a push is rejected, explain and ask.
- Before creating a repository, database, mapper, UseCase or shared component, check whether one already exists.

## Commands

Single-module Android app (`:app`, package `com.hightechif.swipecleaner`; minSdk 26, compile/target SDK 34).

```bash
./gradlew assembleDebug                 # build debug APK
./gradlew testDebugUnitTest             # all JVM unit tests
./gradlew testDebugUnitTest --tests "com.hightechif.swipecleaner.ui.feature.swipe.SwipeViewModelTest"   # one class
./gradlew testDebugUnitTest --tests "*SwipeViewModelTest.someMethod"                                     # one test
./gradlew connectedDebugAndroidTest     # instrumented tests (only a dummy test exists)
```

No lint/format tooling is configured beyond default Android lint (`./gradlew lintDebug`).

## Architecture

Clean Architecture + MVVM, wired with Koin. The whole DI graph lives in one file, `AppModule.kt`; every new use case, repository or ViewModel must be registered there (`singleOf ... bind`, `factoryOf`, `viewModelOf`).

- `domain/` — pure Kotlin: `model/`, repository interfaces (`I`-prefixed, e.g. `IKeptPhotosRepository`), and `use_case/`. Each use case is split into an interface (`FooUseCase`) and an implementation (`FooInteractor`) in the same package.
- `data/` — repository implementations (no `Impl` suffix, e.g. `KeptPhotosRepository`) and `source/local/` (Room `AppDatabase`, DAOs, entities). Entities expose `toDomain()`; repositories return domain models, never entities.
- `ui/feature/<name>/` — each feature holds its Screen, ViewModel and `*ScreenState`. Shared composables go in `ui/component/` with a `Comp` suffix (`SwipeableCardComp`).
- ViewModels depend only on use cases, never on repositories directly.

### Data flow worth knowing

- Photos come from Android `MediaStore` via `MediaStoreRepository`; the swipe pool is `MediaStore images − kept_photos − trashed_photos`, shuffled (`GetShuffledPhotoPoolInteractor`).
- Swipe right → persisted in Room `kept_photos`; swipe left → staged in Room `trashed_photos` (not deleted yet). Actual deletion happens in one batch via `MediaStore.createTrashRequest`, which needs a system confirmation dialog — surfaced through `PendingSystemAction` (domain model) and launched from the UI via an activity-result launcher.
- Both tables live in a single DB file, `kept_photos.db`, built with `fallbackToDestructiveMigration()` — schema changes wipe user progress, so add real migrations if that matters.
- Navigation (Navigation Compose) is hosted in `ui/SwipeCleanerApp.kt`; screens are Permission → Swipe → Completion, plus KeptPhotos.

## Conventions

- Logging uses Timber (planted in `SwipeCleanerApplication`); don't use `printStackTrace()` or `Log`.
- Collect flows in Compose with `collectAsStateWithLifecycle()`.
- Tests mirror the main source package under `app/src/test`, use Google Truth, `MainDispatcherRule` (`util/`), `sut` naming and Arrange-Act-Assert.

## Spec workflow (OpenSpec)

Changes are driven by OpenSpec (`openspec/specs/`, `openspec/changes/`, archived in `openspec/changes/archive/`). Use the `/opsx:*` commands or `openspec-*` skills (propose → apply → verify → archive) for feature work. `ai.log` is an append-only log of past AI-made changes.
