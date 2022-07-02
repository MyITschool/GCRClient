package com.elseboot3909.gcrclient.viewmodel

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


/**
 * This Koin module contains all ViewModels.
 * In order to get any of them use getViewModel().
 * As lifecycle owner parameter use MasterActivity class object as this is single activity application.
 */
val viewModels = module {
    viewModel { StarredViewModel(get()) }
    viewModel { ChangedFilesViewModel(get()) }
    viewModel { FileDiffViewModel(get()) }
    viewModel { ProgressBarViewModel(get()) }
    viewModel { CommentsViewModel(get()) }
    viewModel { ChangeInfoViewModel(get()) }
    viewModel { ProjectsListViewModel(get()) }
    viewModel { CredentialsViewModel(get()) }
    viewModel { SearchParamsViewModel(get()) }
    viewModel { ChangesListViewModel(get(), get()) }

    single { UsersListViewModel(get()) }
}
