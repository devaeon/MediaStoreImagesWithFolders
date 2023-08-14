package com.devaeon.mediastoreimageswithfolders

import com.devaeon.mediastoreimageswithfolders.repository.Repository
import com.devaeon.mediastoreimageswithfolders.repository.RepositoryImpl
import com.devaeon.mediastoreimageswithfolders.viewModel.MediaViewModel
import org.koin.dsl.bind
import org.koin.dsl.module


val mediaModule = module {

    single {
        RepositoryImpl(get())
    } bind Repository::class

    single {
        MediaViewModel(get())
    }
}

val appModules = listOf(mediaModule)