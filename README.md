# SwipeCleaner

SwipeCleaner is a simple, intuitive, and interactive Android application designed to help users clean their gallery efficiently following the latest Android and Jetpack Compose guidelines. The interface utilizes a Tinder-style swiping gesture:
- **Swipe Left**: Marks the photo to be deleted.
- **Swipe Right**: Marks the photo to be kept (this excludes the photo from future swiping pools).

---

## 🚀 Key Features

- **Tinder-Style Swiping Gestures**: Smooth horizontal card dragging with interactive card rotation and color overlays (red tint for delete, green tint for keep).
- **Safe Batch Trashing**: Left-swiped photos are staged in a local persistent Room database table. Upon completion or emptying, they are moved to the system trash via Android's `MediaStore.createTrashRequest` in a single OS confirmation dialog.
- **Persistent Swipe Progress**: Both right-swiped (kept) and left-swiped (trashed) photo states are saved in local Room database tables, making the session staging queue persistent across application restarts.
- **Onboarding Permission Handling**: Verifies storage permissions at launch. Displays a clean, dedicated screen if permissions are missing, and transitions automatically once granted.
- **Completion Stats & Celebration**: Displays reviewed stats and plays a celebratory confetti Lottie animation.
- **Kept Photos Grid**: Lets users browse kept photos in a grid layout, with the option to reset all progress and start over.

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
│            │                             │                   │
│      SwipeViewModel             KeptPhotosViewModel          │
└────────────────────────┬─────────────────────────────────────┘
                         │ Kotlin Coroutines / Flow
┌────────────────────────▼─────────────────────────────────────┐
│  DOMAIN LAYER (Use Cases)                                    │
│                                                              │
│  GetShuffledPhotoPoolUseCase  (excludes kept & trashed URIs) │
│  MarkImageKeptUseCase         (persist to DB)                │
│  ExecuteTrashRequestUseCase   (MediaStore system dialog)     │
│  GetKeptPhotosUseCase         (from DB)                      │
│  ResetKeptPhotosUseCase       (clear DB)                     │
└────────────────────────┬─────────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────────┐
│  DATA LAYER                                                  │
│                                                              │
│  ┌──────────────────┐ ┌──────────────────┐ ┌───────────────┐ │
│  │MediaStoreReposit │ │KeptPhotosReposit │ │TrashedPhotosR │ │
│  │-queryAllImageUri │ │-insertKeptPhoto  │ │-insertTrashed │ │
│  │-createTrashReque │ │-getKeptPhotos    │ │-getTrashedPho │ │
│  └──────────────────┘ └──────────────────┘ └───────────────┘ │
│           │                    │                   │         │
│   Android MediaStore      Room Database (kept_photos.db)     │
│   (ContentResolver)       - kept_photos & trashed_photos     │
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
     MINUS kept_photos & trashed_photos tables
            │
            ▼
     Shuffle remaining URIs → Active Card Stack
            │
       ┌────┴──────────────────────────────┐
       │           Swipe Screen            │
       ├───────────────────────────────────┤
       │ Swipe Right → Room DB (Kept)      │
       │ Swipe Left  → Room DB (Trashed)   │
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
