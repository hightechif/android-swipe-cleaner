# Android Project Guide

> This file provides guidance to AI Agent when working with code in this repository.

---

## Project Identity

```
Application Name    : SwipeCleaner
Package Name        : com.hightechif.swipecleaner
Min SDK             : 26
Target SDK          : 34
Compile SDK         : 34
Version Name        : 1.0
Version Code        : 1
```

---

## Technology Stack

| Layer        | Technology                                              | Notes |
|--------------|---------------------------------------------------------|-------|
| Language     | **Kotlin**                                              | No new Kotlin-Java hybrids |
| UI           | **Jetpack Compose**                                     | Single-module, Gen 2 |
| Architecture | **MVI** (StateFlow + Intent)                            | MVVM-style ViewModel with immutable state |
| DI           | **Koin 3.5.x**                                          | `koin-android` + `koin-androidx-compose` |
| Network      | —                                                       | Local MediaStore only; no remote API |
| Database     | **Room 2.6.x**                                          | Uses KAPT (not KSP) — do not switch |
| Async        | **Coroutines + StateFlow**                              | Flow for data streams; no LiveData for new code |
| Image        | **Coil 2.x** (`coil-compose`)                           | Use `AsyncImage` |
| Animation    | **Lottie** (`lottie-compose`)                           | |
| Navigation   | **Navigation Compose 2.7.x**                            | `NavHostController`, NOT Navigation 3 |
| Build        | **Gradle Kotlin DSL**                                   | Versions hardcoded in `app/build.gradle.kts` |
| Testing      | **JUnit4 + MockK + Coroutines Test + Robolectric**      | Required for ViewModels and UseCases |

---

## Module Structure

Single-module app. All source lives under `app/src/main/java/com/hightechif/swipecleaner/`:

```
com.hightechif.swipecleaner/
├── SwipeCleanerApplication.kt      # Application class, Koin init
├── MainActivity.kt                 # Single activity, Compose entry point
├── AppModule.kt                    # Root Koin module
├── data/
│   ├── source/
│   │   ├── local/                  # Room DAOs, EncryptedSharedPreferences
│   │   └── remote/                 # (reserved; no remote API currently)
│   ├── repository/                 # Repository implementations
│   └── mapper/                     # Entity ↔ Domain mappers
├── domain/
│   ├── model/                      # Domain models
│   ├── repository/                 # Repository interfaces
│   └── use_case/                   # UseCases (one action per class)
└── ui/
    ├── theme/                      # Colors, Type, Shapes, Theme.kt
    ├── component/                  # Reusable composables — suffix: Comp.kt
    └── feature/                    # Screens, organised by feature
        └── [feature]/
            ├── [Feature]Screen.kt
            └── [Feature]ViewModel.kt
```

---

## Architecture Rules — MUST FOLLOW

### Clean Architecture Boundaries

```
Presentation  -->  Domain  <--  Data
(ViewModel)       (UseCase)     (Repository Impl)
```

- **FORBIDDEN**: ViewModel directly importing Data-layer classes
- **FORBIDDEN**: UseCases importing Android framework classes (`android.*`)
- **FORBIDDEN**: Room Entity used directly in a ViewModel — always map to Domain model
- **FORBIDDEN**: Business logic inside a Composable

### ViewModel (MVI Pattern)

```kotlin
data class SwipeUiState(
    val isLoading: Boolean = false,
    val photos: List<Photo> = emptyList(),
    val errorMessage: String? = null
)

sealed interface SwipeIntent {
    data object LoadPhotos : SwipeIntent
    data class KeepPhoto(val photo: Photo) : SwipeIntent
}

class SwipeViewModel(
    private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SwipeUiState())
    val state: StateFlow<SwipeUiState> = _state.asStateFlow()

    fun processIntent(intent: SwipeIntent) { ... }
}
```

### UseCase

```kotlin
class GetPhotosUseCase(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(): List<Photo> = repository.getPhotos()
}
```

---

## Dependency Injection (Koin)

DI is configured with Koin. Module declarations live in `AppModule.kt` and are provided via `startKoin` in the Application class.

```kotlin
val appModule = module {
    single<MediaRepository> { MediaRepositoryImpl(get()) }
    factory { GetPhotosUseCase(get()) }
    viewModel { SwipeViewModel(get()) }
}
```

Inject ViewModels in Composables using `koinViewModel()`, not `hiltViewModel()`.

```kotlin
@Composable
fun SwipeScreen(viewModel: SwipeViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ...
}
```

---

## Compose Rules

```kotlin
// Composables should be stateless whenever possible
@Composable
fun PhotoCard(
    photo: Photo,
    onKeep: () -> Unit,
    modifier: Modifier = Modifier   // modifier always last
) { ... }

// Use collectAsStateWithLifecycle(), NOT collectAsState()
val state by viewModel.state.collectAsStateWithLifecycle()

// Every Composable must have a @Preview
@Preview(showBackground = true)
@Composable
private fun PhotoCardPreview() {
    SwipeCleanerTheme { PhotoCard(photo = fakePhoto, onKeep = {}) }
}
```

### Formatting

Always format Kotlin files using Android Studio's default settings (`Cmd + Option + L` on macOS, `Ctrl + Alt + L` on Windows/Linux) before committing.

---

## Coroutines & Flow

```kotlin
// Use the appropriate Dispatcher
viewModelScope.launch {
    withContext(Dispatchers.IO) { /* I/O operation */ }
}

// Flow transformations belong in the repository, not the ViewModel
fun getPhotos(): Flow<List<Photo>> = dao.getAll()
    .map { entities -> entities.map { it.toDomain() } }
    .flowOn(Dispatchers.IO)

// Use StateFlow for UI state, SharedFlow for one-shot events
private val _events = MutableSharedFlow<SwipeEvent>()
val events: SharedFlow<SwipeEvent> = _events.asSharedFlow()
```

---

## Navigation (Navigation Compose 2.x)

This project uses `androidx.navigation:navigation-compose:2.7.x` — **not Navigation 3**.

```kotlin
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "swipe") {
        composable("swipe") { SwipeScreen(...) }
        composable("kept") { KeptPhotosScreen(...) }
        composable("completion") { CompletionScreen(...) }
    }
}
```

---

## Local Storage (Room)

Room uses **KAPT** — do NOT switch to KSP.

```kotlin
@Entity(tableName = "kept_photos")
data class KeptPhotoEntity(
    @PrimaryKey val id: Long,
    val uri: String
)

@Dao
interface KeptPhotoDao {
    @Query("SELECT * FROM kept_photos")
    fun getAll(): Flow<List<KeptPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: KeptPhotoEntity)
}
```

---

## Code Conventions (Naming)

| Type                | Convention                          | Example                    |
|---------------------|-------------------------------------|----------------------------|
| Composable screen   | PascalCase + "Screen"               | `SwipeScreen`              |
| Reusable component  | PascalCase + "Comp"                 | `SwipeableCardComp`        |
| ViewModel           | PascalCase + "ViewModel"            | `SwipeViewModel`           |
| UseCase             | Verb + noun + "UseCase"             | `GetPhotosUseCase`         |
| Repository interface| Noun + "Repository"                 | `MediaRepository`          |
| Repository impl     | Noun + "RepositoryImpl"             | `MediaRepositoryImpl`      |
| Room Entity         | Noun + "Entity"                     | `KeptPhotoEntity`          |
| Domain model        | Noun only                           | `Photo`                    |
| Koin module val     | camelCase + "Module"                | `appModule`                |

---

## Testing

```kotlin
@RunWith(JUnit4::class)
class SwipeViewModelTest {
    @MockK lateinit var getPhotosUseCase: GetPhotosUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `when loadPhotos succeeds, state contains photos`() = runTest {
        coEvery { getPhotosUseCase() } returns fakePhotos
        val viewModel = SwipeViewModel(getPhotosUseCase)
        assertThat(viewModel.state.value.photos).isEqualTo(fakePhotos)
    }
}
```

---

## Security — REQUIRED

```
❌ DO NOT store API keys, tokens, or secrets in:
   - Source code
   - strings.xml
   - build.gradle.kts
   - Files committed to Git

✅ USE:
   - local.properties
   - BuildConfig
   - EncryptedSharedPreferences
   - Android Keystore
```

### Files Claude Must Not Touch

```
local.properties
google-services.json
keystore.jks / *.keystore
.env*
secrets.gradle.kts
```

---

## Build Commands

```bash
./gradlew assembleDebug            # Debug build
./gradlew assembleRelease          # Release build
./gradlew test                     # All unit tests
./gradlew lint                     # Lint check
./gradlew clean                    # Clean
```

---

## AI Workflow Rules

- **Log every AI-made change** to `ai.log` in the project root — one line per change point, format: `[YYYY-MM-DD HH:MM:SS] <brief description>`. Create the file if it does not exist; never overwrite existing entries.
- **Ask the user about any unclear decision** before proceeding. Do not assume ambiguous requirements, architecture choices, or scope.
- **No Force Pushes**: Strictly prohibited from running `git push --force` or `--force-with-lease` automatically. If a push is rejected due to local/remote divergence, pause, explain the conflict, and ask the user for permission to rebase or merge.

---

## Things Claude Must NOT Do

```
❌ Change compileSdk / targetSdk / minSdk versions
❌ Replace Koin with Hilt (or any other DI framework)
❌ Switch Room annotation processor from KAPT to KSP without explicit instruction
❌ Replace navigation-compose 2.x with Navigation 3
❌ Put business logic inside Composables
❌ Import Android classes (android.*) in the domain layer
❌ Use LiveData for new code — use StateFlow/SharedFlow
❌ Hardcode user-facing strings outside strings.xml
❌ Commit files that contain secrets or credentials
❌ Run git commit without explicit instruction from the user
❌ Run git push to any remote without explicit instruction from the user
❌ Run git push --force or --force-with-lease without explicit instruction from the user
```

## Things Claude Must ALWAYS Do

```
✅ Follow the existing package structure before creating new files
✅ Check whether a similar UseCase already exists before creating a new one
✅ Map: Entity → Domain model, never expose Entity to ViewModel
✅ Add @Preview for every new Composable
✅ Write unit tests for every new ViewModel and UseCase
✅ Use modifier: Modifier = Modifier as the last Composable parameter
✅ Use collectAsStateWithLifecycle(), not collectAsState()
✅ Name reusable components with the Comp suffix
✅ Log every AI-made change to ai.log
```

---

## Important File References

```
app/build.gradle.kts                    → dependencies and SDK versions
app/src/main/java/.../AppModule.kt      → Koin DI modules
app/src/main/AndroidManifest.xml        → permissions & entry points
app/src/main/java/.../ui/theme/         → colors, typography, shapes
app/src/main/java/.../ui/component/     → shared Compose components
```
