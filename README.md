# SwipeCleaner

SwipeCleaner is a simple, intuitive, and interactive Android application designed to help users clean their gallery efficiently following the latest Android and Jetpack Compose guidelines. The interface utilizes a Tinder-style swiping gesture:
- **Swipe Left**: Marks the photo to be deleted.
- **Swipe Right**: Marks the photo to be kept (this excludes the photo from future swiping pools).

---

## 🚀 Key Features

- **Tinder-Style Swiping Gestures**: Smooth horizontal card dragging with interactive card rotation and color overlays (red tint for delete, green tint for keep).
- **Safe Batch Trashing (Option B / Thread A)**: Left-swiped photos are held in an in-memory queue. Upon review completion, they are moved to the system trash via Android's `MediaStore.createTrashRequest` in a single OS confirmation dialog.
- **Persistent Keep-State (Thread B)**: Right-swiped photos are saved in a local Room database. They are excluded from future random pool generations on app launches.
- **Onboarding Permission Handling (Thread E)**: Verifies storage permissions at launch. Displays a clean, dedicated screen if permissions are missing, and transitions automatically once granted.
- **Completion Stats & Celebration (Thread C)**: Displays reviewed stats and plays a celebratory confetti Lottie animation.
- **Kept Photos Grid (Thread C)**: Lets users browse kept photos in a grid layout, with the option to reset all progress and start over.

---

## 🛠 Tech Stack

- **UI Framework**: Jetpack Compose & Material 3
- **Dependency Injection**: Koin
- **Database**: Room Database
- **Media Retrieval & Deletion**: Android MediaStore (ContentResolver)
- **Image Loading**: Coil 3
- **Animations**: Lottie Compose
- **Concurrency**: Kotlin Coroutines & Flows
- **Navigation**: Jetpack Compose Navigation

---

## 📐 System Architecture

The project is structured following Clean Architecture and MVVM patterns:

```
┌──────────────────────────────────────────────────────────────┐
│  UI LAYER (Compose)                                          │
│                                                              │
│  PermissionScreen   SwipeScreen   CompletionScreen           │
│  KeptPhotosScreen                                            │
│       │                  │               │                   │
│  PermissionVM      SwipeViewModel  CompletionVM              │
│                    KeptPhotosVM                              │
└────────────────────────┬─────────────────────────────────────┘
                         │ Kotlin Coroutines / Flow
┌────────────────────────▼─────────────────────────────────────┐
│  DOMAIN LAYER (Use Cases)                                    │
│                                                              │
│  GetShuffledPhotoPoolUseCase  (excludes kept URIs)           │
│  MarkImageKeptUseCase         (persist to DB)                │
│  MarkImageForDeletionUseCase  (add to in-memory queue)       │
│  ExecuteTrashRequestUseCase   (MediaStore system dialog)     │
│  GetKeptPhotosUseCase         (from DB)                      │
│  ResetKeptPhotosUseCase       (clear DB)                     │
└────────────────────────┬─────────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────────┐
│  DATA LAYER                                                  │
│                                                              │
│  ┌────────────────────────┐   ┌────────────────────────────┐ │
│  │  MediaStoreRepository  │   │  KeptPhotosRepository      │ │
│  │  - queryAllImageUris() │   │  - insert(uri)             │ │
│  │  - createTrashRequest()│   │  - getAllKeptUris()         │ │
│  │    → IntentSender      │   │  - deleteAll()             │ │
│  └────────────────────────┘   └────────────────────────────┘ │
│           │                              │                   │
│    Android MediaStore              Room Database             │
│    (ContentResolver)               (kept_photos.db)          │
└──────────────────────────────────────────────────────────────┘
```

### Swiping & Data Lifecycle Flow

```
App Launch
    │
    ▼
Check Permission
    ├── DENIED  → PermissionScreen
    └── GRANTED
            │
            ▼
     Query MediaStore (All Images)
     MINUS kept_photos table (Room DB)
            │
            ▼
     Shuffle remaining URIs → In-memory Queue
            │
       ┌────┴──────────────────────────────┐
       │           Swipe Screen            │
       ├───────────────────────────────────┤
       │ Swipe Right → Room DB (Kept)      │
       │ Swipe Left  → In-memory List      │
       └────┬──────────────────────────────┘
            │ (Queue Exhausted)
            ▼
     Completion Screen
     (Confetti Lottie plays automatically)
     ┌─────────────────────────────────────┐
     │  - "Move to Trash" (MediaStore request)
     │  - "See Kept Photos"                │
     │  - "Reset Progress & Start Over"    │
     └─────────────────────────────────────┘
```
