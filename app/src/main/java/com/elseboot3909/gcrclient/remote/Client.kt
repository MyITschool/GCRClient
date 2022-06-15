package com.elseboot3909.gcrclient.remote

import com.elseboot3909.gcrclient.ServerData
import com.elseboot3909.gcrclient.credentials.PasswordCrypto
import com.elseboot3909.gcrclient.viewmodel.credentials.CredentialsViewModel
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import org.koin.dsl.module

val client = module {

    fun getClient(serverData: ServerData): HttpClient {
        return HttpClient(Android) {
            defaultRequest {
                url(serverData.serverURL)
            }
            if (serverData.username.isNotEmpty() || serverData.password.toString().isNotEmpty()) {
                install(Auth) {
                    basic {
                        credentials {
                            BasicAuthCredentials(
                                username = serverData.username,
                                password = PasswordCrypto.decryptString(serverData.password),
                            )
                        }
                    }
                }
            }
        }
    }

    factory {
        try {
            get<CredentialsViewModel>().let {
                it.serversList.value?.serverDataList?.get(
                    it.selected.value ?: 0
                ) ?: ServerData.getDefaultInstance()
            }
        } catch (e: Exception) {
            ServerData.getDefaultInstance()
        }
    }

    factory { (params: ServerData?) ->
        getClient(
            params ?: get()
        )
    }
}
