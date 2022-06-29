package com.elseboot3909.gcrclient.remote.api

import com.elseboot3909.gcrclient.ServerData
import com.elseboot3909.gcrclient.entity.external.AccountInfo
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.delete
import io.ktor.client.statement.HttpResponse
import io.ktor.http.appendPathSegments
import io.ktor.http.appendEncodedPathSegments
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

/**
 * **Endpoint documentation:** [Gerrit](https://gerrit-review.googlesource.com/Documentation/rest-api-accounts.html)
 *
 * This object contains functions, that are located in /accounts/ REST endpoint.
 */
object AccountAPI : KoinComponent {

    /**
     * **Endpoint documentation:** [Gerrit](https://gerrit-review.googlesource.com/Documentation/rest-api-accounts.html#star-change)
     *
     * This function throws a request to put star on the change.
     *  @param changeInfo the object with proper change id to put a star on
     */
    suspend fun putDefaultStarOnChange(changeInfo: ChangeInfo): HttpResponse {
        return get<HttpClient> {
            parametersOf(null)
        }.put {
            url {
                appendPathSegments("a/accounts/self/starred.changes/")
                appendEncodedPathSegments(changeInfo.id)
            }
        }
    }

    /**
     * **Endpoint documentation:** [Gerrit](https://gerrit-review.googlesource.com/Documentation/rest-api-accounts.html#unstar-change)
     *
     * This function throws a request to remove star from the change.
     *  @param changeInfo the object with proper change id to remove a star from
     */
    suspend fun removeDefaultStarFromChange(changeInfo: ChangeInfo): HttpResponse {
        return get<HttpClient> {
            parametersOf(null)
        }.delete {
            url {
                appendPathSegments("a/accounts/self/starred.changes/")
                appendEncodedPathSegments(changeInfo.id)
            }
        }
    }


    /**
     * **Endpoint documentation:** [Gerrit](https://gerrit-review.googlesource.com/Documentation/rest-api-accounts.html#get-account)
     *
     * This function throws a request to get basic account info.
     *  @param serverData data of the server to which to send the request.
     *  Required for log in to get real username of account.
     *  Put [ServerData.serverURL_] empty to get current data.
     *  @param accountInfo info of account to get it's own data.
     */
    suspend fun getAccount(serverData: ServerData, accountInfo: AccountInfo): HttpResponse {
        return get<HttpClient> {
                    parametersOf(if (serverData.serverURL.isNotEmpty()) serverData else null)
                }.get {
                    url {
                        appendPathSegments("accounts/${accountInfo.username}")
                    }
                }
    }

    /**
     * **Endpoint documentation:** [Gerrit](https://gerrit-review.googlesource.com/Documentation/rest-api-accounts.html#get-detail)
     *
     * This function throws a request to get full account info.
     *  @param serverData data of the server to which to send the request.
     *  Required for log in to check if authorization data is correct.
     *  Put [ServerData.serverURL_] empty to get current data.
     *  @param accountInfo info of account to get it's own data.
     */
    suspend fun getAccountDetails(serverData: ServerData, accountInfo: AccountInfo): HttpResponse {
        return get<HttpClient> {
            parametersOf(if (serverData.serverURL.isNotEmpty()) serverData else null)
        }.get {
            url {
                appendPathSegments("a/accounts/${accountInfo.username}/detail")
            }
        }
    }

    /**
     * **Endpoint documentation:** [Gerrit](https://gerrit-review.googlesource.com/Documentation/rest-api-accounts.html#star-change)
     *
     * This function throws a request to put star on the change.
     *  @param changeInfo the object with proper change id to put a star on
     */
    suspend fun queryAccount(): HttpResponse {
        return get<HttpClient> {
            parametersOf(null)
        }.get {
            url {
                appendPathSegments("a/accounts/")
                parameters.append("q", "is:active OR is:inactive")
                parameters.append("o", "DETAILS")
            }
        }
    }

}
