package com.elseboot3909.gcrclient.remote.api

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
object ProjectsAPI : KoinComponent {
    /* List Projects - ./Documentation/rest-api-projects.html#list-projects */
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
