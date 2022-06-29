package com.elseboot3909.gcrclient.repository

import org.koin.dsl.module

val repos = module {
    single { ProgressBarRepository() }
    single { StarredRepository(get()) }
    single { ChangedFilesRepository(get(), get()) }
    single { FileDiffRepository(get(), get()) }
    single { CommentsRepository(get(), get()) }
    single { CredentialsRepository() }

    single { ChangeInfoRepository(get()) }
    single { SearchParamsRepository() }
}