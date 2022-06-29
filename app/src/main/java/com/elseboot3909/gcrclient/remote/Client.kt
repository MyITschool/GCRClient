package com.elseboot3909.gcrclient.remote

import com.elseboot3909.gcrclient.ServerData
import com.elseboot3909.gcrclient.credentials.PasswordCrypto
import com.elseboot3909.gcrclient.repository.CredentialsRepository
import com.elseboot3909.gcrclient.viewmodel.CredentialsViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import org.koin.androidx.compose.getViewModel
import org.koin.dsl.module
import kotlin.IndexOutOfBoundsException

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

    factory { get<CredentialsRepository>().currentServerData }

    factory { (params: ServerData?) ->
        getClient(params ?: get())
    }
}
