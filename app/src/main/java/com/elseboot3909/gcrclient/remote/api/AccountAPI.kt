package com.elseboot3909.gcrclient.remote.api

import com.elseboot3909.gcrclient.ServerData
import com.elseboot3909.gcrclient.entity.external.AccountInfo
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

/* AccountAPI module represents the implementation of ./Documentation/rest-api-accounts.html
*
* */
object AccountAPI : KoinComponent {

    /* Put Default Star On Change - ./Documentation/rest-api-accounts.html#star-change */
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

    /* Remove Default Star From Change - ./Documentation/rest-api-accounts.html#unstar-change */
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


    /* Get Account - ./Documentation/rest-api-accounts.html#get-account */
    suspend fun getAccount(serverData: ServerData, accountInfo: AccountInfo): HttpResponse {
        return get<HttpClient> {
                    parametersOf(if (serverData.serverURL.isNotEmpty()) serverData else null)
                }.get {
                    url {
                        appendPathSegments("accounts/${accountInfo.username}")
                    }
                }
    }

    /* Get Account Details - ./Documentation/rest-api-accounts.html#get-detail */
    suspend fun getAccountDetails(serverData: ServerData, accountInfo: AccountInfo): HttpResponse {
        return get<HttpClient> {
            parametersOf(if (serverData.serverURL.isNotEmpty()) serverData else null)
        }.get {
            url {
                appendPathSegments("a/accounts/${accountInfo.username}/detail")
            }
        }
    }

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
