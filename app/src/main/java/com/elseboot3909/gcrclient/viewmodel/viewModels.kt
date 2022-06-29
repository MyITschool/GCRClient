package com.elseboot3909.gcrclient.viewmodel

import com.elseboot3909.gcrclient.viewmodel.home.ChangesViewModel
import com.elseboot3909.gcrclient.viewmodel.search.ProjectsListViewModel
import com.elseboot3909.gcrclient.viewmodel.search.UsersListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModels = module {
    viewModel { StarredViewModel(get()) }
    viewModel { ChangedFilesViewModel(get()) }
    viewModel { FileDiffViewModel(get()) }
    viewModel { ProgressBarViewModel(get()) }
    viewModel { CommentsViewModel(get()) }
    viewModel { ChangeInfoViewModel(get()) }
    viewModel { CredentialsViewModel() }

    single { ChangesViewModel(get(), get()) }
    single { ProjectsListViewModel(get()) }
    single { UsersListViewModel(get()) }
}