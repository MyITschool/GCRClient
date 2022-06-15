package com.elseboot3909.gcrclient.remote.api

import com.elseboot3909.gcrclient.ServerData
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

/*
*
* */

object ConfigAPI : KoinComponent {
    suspend fun getVersion(serverData: ServerData): HttpResponse {
        return get<HttpClient> {
            parametersOf(serverData)
        }.get {
            url { appendPathSegments("config/server/version") }
        }
    }
}
