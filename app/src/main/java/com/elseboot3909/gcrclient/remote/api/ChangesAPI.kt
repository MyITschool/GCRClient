package com.elseboot3909.gcrclient.remote.api

import com.elseboot3909.gcrclient.entity.external.*
import com.elseboot3909.gcrclient.entity.internal.QueryParams
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

/* ChangesAPI module represents the implementation of ./Documentation/rest-api-changes.html */
object ChangesAPI : KoinComponent {

    /* This is the list of default option for app to work properly */
    private val defaultOptions by lazy {
        listOf(
            "DETAILED_ACCOUNTS",
            "LABELS",
            "ALL_REVISIONS",
            "CURRENT_COMMIT",
            "DETAILED_LABELS"
        )
    }

    /* Query Changes - ./Documentation/rest-api-changes.html#list-changes */
    suspend fun queryChanges(queryParams: QueryParams): HttpResponse {
        return get<HttpClient> {
            parametersOf(null)
        }.get {
            url {
                appendPathSegments("a/changes/")
                if (queryParams.q.isNotEmpty()) {
                    encodedParameters.append("q", queryParams.q)
                }
                for (o in queryParams.o.ifEmpty { defaultOptions }) {
                    parameters.append("o", o)
                }
                parameters.append("n", queryParams.n.toString())
                parameters.append("S", queryParams.S.toString())
            }
        }
    }

    /* Get Change - ./Documentation/rest-api-changes.html#get-change */
    suspend fun getChange(changeInfo: ChangeInfo): HttpResponse {
        return get<HttpClient> {
            parametersOf(null)
        }.get {
            url {
                appendPathSegments("a/changes/")
                appendEncodedPathSegments(changeInfo.id)
                for (o in defaultOptions) {
                    parameters.append("o", o)
                }
            }
        }
    }


    /* Get Reviewer - ./Documentation/rest-api-changes.html#get-reviewer */
    suspend fun getReviewer(
        changeInfo: ChangeInfo,
        accountInfo: AccountInfo
    ): HttpResponse {
        return get<HttpClient> {
            parametersOf(null)
        }.get {
            url {
                appendPathSegments("a/changes/")
                appendEncodedPathSegments(changeInfo.id)
                appendPathSegments("/reviewers/")
                appendEncodedPathSegments(accountInfo.username)
            }
        }
    }

    /* Add Reviewer - ./Documentation/rest-api-changes.html#add-reviewer */
    suspend fun addReviewer(
        changeInfo: ChangeInfo,
        reviewerInput: ReviewerInput
    ): HttpResponse {
        return get<HttpClient> {
            parametersOf(null)
        }.post {
            url {
                appendPathSegments("a/changes/")
                appendEncodedPathSegments(changeInfo.id)
                appendPathSegments("/reviewers/")
            }
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(reviewerInput))
        }
    }

    /* Set Review - ./Documentation/rest-api-changes.html#review-input */
    suspend fun setReview(
        changeInfo: ChangeInfo,
        reviewInput: ReviewInput
    ): HttpResponse {
        return get<HttpClient> {
            parametersOf(null)
        }.post {
            url {
                appendPathSegments("a/changes/")
                appendEncodedPathSegments(changeInfo.id)
                appendPathSegments("/revisions/current/review")
            }
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(reviewInput))
        }
    }

    /* List Files - ./Documentation/rest-api-changes.html#list-files */
    suspend fun listFiles(
        changeInfo: ChangeInfo,
        revision: String,
        base: Int
    ): HttpResponse {
        return get<HttpClient> {
            parametersOf(null)
        }.get {
            url {
                appendPathSegments("a/changes/")
                appendEncodedPathSegments(changeInfo.id)
                appendPathSegments("/revisions/")
                appendEncodedPathSegments(revision)
                appendPathSegments("/files")
                if (base != 0) {
                    parameters.append("base", base.toString())
                }
            }
        }
    }

    /* Get Diff - ./Documentation/rest-api-changes.html#get-diff */
    suspend fun getDiff(
        changeInfo: ChangeInfo,
        revision: String,
        file: String,
        base: Int
    ): HttpResponse {
        return get<HttpClient> {
            parametersOf(null)
        }.get {
            url {
                appendPathSegments("a/changes/")
                appendEncodedPathSegments(changeInfo.id)
                appendPathSegments("/revisions/")
                appendEncodedPathSegments(revision)
                appendPathSegments("/files/")
                appendEncodedPathSegments(file)
                appendPathSegments("/diff")
                if (base != 0) {
                    parameters.append("base", base.toString())
                }
            }
        }
    }

    /* List Change Comments - ./Documentation/rest-api-changes.html#list-change-comments */
    suspend fun listChangeComments(changeInfo: ChangeInfo): HttpResponse {
        return get<HttpClient> {
            parametersOf(null)
        }.get {
            url {
                appendPathSegments("a/changes/")
                appendEncodedPathSegments(changeInfo.id)
                appendPathSegments("/comments")
                parameters.append("enable-context", "true")
                parameters.append("context-padding", "2")
            }
        }
    }

    /* List Change Comments - ./Documentation/rest-api-changes.html#list-change-comments */
    suspend fun setCommitMessage(changeInfo: ChangeInfo, commitMessageInput: CommitMessageInput): HttpResponse {
        return get<HttpClient> {
            parametersOf(null)
        }.put {
            url {
                appendPathSegments("a/changes/")
                appendEncodedPathSegments(changeInfo.id)
                appendPathSegments("/message")
            }
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(commitMessageInput))
        }
    }

    /* Delete Vote - ./Documentation/rest-api-changes.html#delete-vote */
    suspend fun deleteVote(
        changeInfo: ChangeInfo,
        _account_id: Int,
        label: String,
    ): HttpResponse {
        return get<HttpClient> {
            parametersOf(null)
        }.post {
            url {
                appendPathSegments("a/changes/")
                appendEncodedPathSegments(changeInfo.id)
                appendPathSegments("/reviewers/")
                appendEncodedPathSegments(_account_id.toString())
                appendPathSegments("/votes/")
                appendEncodedPathSegments(label)
                appendPathSegments("/delete")
            }
        }
    }

}
