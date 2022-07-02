package com.elseboot3909.gcrclient.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.appendPathSegments
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

/**
 * **Endpoint documentation:** [Gerrit](https://gerrit-review.googlesource.com/Documentation/rest-api-projects.html)
 *
 * This object contains functions, that are located in /projects/ REST endpoint.
 */
object ProjectsAPI : KoinComponent {

    /**
     * **Endpoint documentation:** [Gerrit](https://gerrit-review.googlesource.com/Documentation/rest-api-accounts.html#star-change)
     *
     * This function throws a request to get a list of all projects.
     */
    suspend fun listProjects(): HttpResponse {
        return get<HttpClient> {
            parametersOf(null)
        }.get {
            url {
                appendPathSegments("a/projects/")
                parameters.append("d", "")
                parameters.append("all", "")
            }
        }
    }
}
