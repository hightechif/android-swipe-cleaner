package com.hightechif.swipecleaner

import androidx.room.Room
import com.hightechif.swipecleaner.data.db.AppDatabase
import com.hightechif.swipecleaner.data.repository.KeptPhotosRepository
import com.hightechif.swipecleaner.data.repository.KeptPhotosRepositoryImpl
import com.hightechif.swipecleaner.data.repository.TrashedPhotosRepository
import com.hightechif.swipecleaner.data.repository.TrashedPhotosRepositoryImpl
import com.hightechif.swipecleaner.data.repository.MediaStoreRepository
import com.hightechif.swipecleaner.data.repository.MediaStoreRepositoryImpl
import com.hightechif.swipecleaner.domain.usecase.ExecuteTrashRequestUseCase
import com.hightechif.swipecleaner.domain.usecase.GetKeptPhotosUseCase
import com.hightechif.swipecleaner.domain.usecase.GetShuffledPhotoPoolUseCase
import com.hightechif.swipecleaner.domain.usecase.MarkImageKeptUseCase
import com.hightechif.swipecleaner.domain.usecase.ResetKeptPhotosUseCase
import com.hightechif.swipecleaner.ui.viewmodel.KeptPhotosViewModel
import com.hightechif.swipecleaner.ui.viewmodel.SwipeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "kept_photos.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    // Dao
    single { get<AppDatabase>().keptPhotoDao() }
    single { get<AppDatabase>().trashedPhotoDao() }

    // Repositories
    single<KeptPhotosRepository> { KeptPhotosRepositoryImpl(get()) }
    single<TrashedPhotosRepository> { TrashedPhotosRepositoryImpl(get()) }
    single<MediaStoreRepository> { MediaStoreRepositoryImpl(androidContext()) }

    // UseCases
    single { GetShuffledPhotoPoolUseCase(get(), get(), get()) }
    single { MarkImageKeptUseCase(get()) }
    single { ResetKeptPhotosUseCase(get()) }
    single { GetKeptPhotosUseCase(get()) }
    single { ExecuteTrashRequestUseCase(get()) }

    // ViewModels
    viewModel { SwipeViewModel(get(), get(), get(), get(), get()) }
    viewModel { KeptPhotosViewModel(get(), get()) }
}
