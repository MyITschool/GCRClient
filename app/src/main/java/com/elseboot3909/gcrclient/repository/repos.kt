package com.elseboot3909.gcrclient.repository

import com.elseboot3909.gcrclient.repository.diff.DiffRepository
import com.elseboot3909.gcrclient.repository.diff.FilesRepository
import com.elseboot3909.gcrclient.repository.progress.ProgressBarRepository
import com.elseboot3909.gcrclient.repository.search.SearchParamsRepository
import com.elseboot3909.gcrclient.viewmodel.change.ChangeInfoRepository
import com.elseboot3909.gcrclient.viewmodel.progress.ProgressBarViewModel
import org.koin.dsl.module

val repos = module {
    single { ProgressBarRepository() }
    single { ProgressBarViewModel(get()) }
    single { ChangeInfoRepository(get(), get()) }
    single { SearchParamsRepository() }
    single { DiffRepository() }
    single { FilesRepository(get(), get(), get()) }
}