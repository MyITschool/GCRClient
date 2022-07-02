package com.elseboot3909.gcrclient.remote.api

import com.elseboot3909.gcrclient.ServerData
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

/**
 * **Endpoint documentation:** [Gerrit](https://gerrit-review.googlesource.com/Documentation/rest-api-config.html)
 *
 * This object contains functions, that are located in /config/ REST endpoint.
 */
object ConfigAPI : KoinComponent {

    /**
     * **Endpoint documentation:** [Gerrit](https://gerrit-review.googlesource.com/Documentation/rest-api-config.html#get-version)
     *
     * This function throws a request to get version of gerrit server.
     *  @param serverData data of the server to which to send the request.
     *  Required for log in to check if gerrit server is real.
     *  Put [ServerData.serverURL_] empty to get current data.
     */
    suspend fun getVersion(serverData: ServerData): HttpResponse {
        return get<HttpClient> {
            parametersOf(serverData)
        }.get {
            url { appendPathSegments("config/server/version") }
        }
    }
}
