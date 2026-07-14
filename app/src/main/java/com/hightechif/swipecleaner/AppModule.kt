package com.hightechif.swipecleaner

import androidx.room.Room
import com.hightechif.swipecleaner.data.repository.KeptPhotosRepository
import com.hightechif.swipecleaner.data.repository.MediaStoreRepository
import com.hightechif.swipecleaner.data.repository.TrashedPhotosRepository
import com.hightechif.swipecleaner.data.source.local.AppDatabase
import com.hightechif.swipecleaner.domain.repository.IKeptPhotosRepository
import com.hightechif.swipecleaner.domain.repository.IMediaStoreRepository
import com.hightechif.swipecleaner.domain.repository.ITrashedPhotosRepository
import com.hightechif.swipecleaner.domain.use_case.AddToTrashInteractor
import com.hightechif.swipecleaner.domain.use_case.AddToTrashUseCase
import com.hightechif.swipecleaner.domain.use_case.ClearTrashedPhotosInteractor
import com.hightechif.swipecleaner.domain.use_case.ClearTrashedPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.ExecuteTrashRequestInteractor
import com.hightechif.swipecleaner.domain.use_case.ExecuteTrashRequestUseCase
import com.hightechif.swipecleaner.domain.use_case.GetAllMediaImagesInteractor
import com.hightechif.swipecleaner.domain.use_case.GetAllMediaImagesUseCase
import com.hightechif.swipecleaner.domain.use_case.GetFilteredAlbumsInteractor
import com.hightechif.swipecleaner.domain.use_case.GetFilteredAlbumsUseCase
import com.hightechif.swipecleaner.domain.use_case.GetKeptPhotosInteractor
import com.hightechif.swipecleaner.domain.use_case.GetKeptPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.GetShuffledPhotoPoolInteractor
import com.hightechif.swipecleaner.domain.use_case.GetShuffledPhotoPoolUseCase
import com.hightechif.swipecleaner.domain.use_case.GetTrashedPhotosInteractor
import com.hightechif.swipecleaner.domain.use_case.GetTrashedPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.MarkImageKeptInteractor
import com.hightechif.swipecleaner.domain.use_case.MarkImageKeptUseCase
import com.hightechif.swipecleaner.domain.use_case.ResetKeptPhotosInteractor
import com.hightechif.swipecleaner.domain.use_case.ResetKeptPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.RestoreFromKeptInteractor
import com.hightechif.swipecleaner.domain.use_case.RestoreFromKeptUseCase
import com.hightechif.swipecleaner.domain.use_case.RestoreFromTrashInteractor
import com.hightechif.swipecleaner.domain.use_case.RestoreFromTrashUseCase
import com.hightechif.swipecleaner.ui.feature.kept.KeptPhotosViewModel
import com.hightechif.swipecleaner.ui.feature.swipe.SwipeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "kept_photos.db"
        ).fallbackToDestructiveMigration().build()
    }

    // DAOs
    single { get<AppDatabase>().keptPhotoDao() }
    single { get<AppDatabase>().trashedPhotoDao() }

    // Repositories
    singleOf(::KeptPhotosRepository) bind IKeptPhotosRepository::class
    singleOf(::TrashedPhotosRepository) bind ITrashedPhotosRepository::class
    single<IMediaStoreRepository> { MediaStoreRepository(androidContext()) }

    // Use Cases
    factoryOf(::GetShuffledPhotoPoolInteractor) bind GetShuffledPhotoPoolUseCase::class
    factoryOf(::MarkImageKeptInteractor) bind MarkImageKeptUseCase::class
    factoryOf(::ResetKeptPhotosInteractor) bind ResetKeptPhotosUseCase::class
    factoryOf(::GetKeptPhotosInteractor) bind GetKeptPhotosUseCase::class
    factoryOf(::ExecuteTrashRequestInteractor) bind ExecuteTrashRequestUseCase::class
    factoryOf(::GetTrashedPhotosInteractor) bind GetTrashedPhotosUseCase::class
    factoryOf(::AddToTrashInteractor) bind AddToTrashUseCase::class
    factoryOf(::RestoreFromTrashInteractor) bind RestoreFromTrashUseCase::class
    factoryOf(::ClearTrashedPhotosInteractor) bind ClearTrashedPhotosUseCase::class
    factoryOf(::RestoreFromKeptInteractor) bind RestoreFromKeptUseCase::class
    factoryOf(::GetAllMediaImagesInteractor) bind GetAllMediaImagesUseCase::class
    factoryOf(::GetFilteredAlbumsInteractor) bind GetFilteredAlbumsUseCase::class

    // ViewModels
    viewModelOf(::SwipeViewModel)
    viewModelOf(::KeptPhotosViewModel)
}
