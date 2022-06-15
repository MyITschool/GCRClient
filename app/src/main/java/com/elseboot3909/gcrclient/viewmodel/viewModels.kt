package com.elseboot3909.gcrclient.viewmodel

import com.elseboot3909.gcrclient.repository.diff.FileDiffRepository
import com.elseboot3909.gcrclient.viewmodel.comments.CommentsViewModel
import com.elseboot3909.gcrclient.viewmodel.credentials.CredentialsViewModel
import com.elseboot3909.gcrclient.viewmodel.diff.FilesViewModel
import com.elseboot3909.gcrclient.viewmodel.home.ChangesViewModel
import com.elseboot3909.gcrclient.viewmodel.home.StarredViewModel
import com.elseboot3909.gcrclient.viewmodel.search.ProjectsListViewModel
import com.elseboot3909.gcrclient.viewmodel.search.UsersListViewModel
import org.koin.dsl.module

val viewModels = module {
    single { StarredViewModel(get()) }
    single { ChangesViewModel(get(), get()) }
    single { ProjectsListViewModel(get()) }
    single { CredentialsViewModel() }
    single { FilesViewModel(get(), get(), get()) }
    single { FileDiffRepository(get(), get(), get()) }
    single { CommentsViewModel(get(), get()) }
    single { UsersListViewModel(get()) }
}