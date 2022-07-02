package com.elseboot3909.gcrclient.repository

import android.text.TextUtils
import kotlinx.coroutines.flow.MutableStateFlow

class SearchParamsRepository {

    var selectedProjects = ArrayList<String>()
    var preSelectedProjects = ArrayList<String>()
    val selectedProjectsCounter = MutableStateFlow(0)

    var selectedUsers = ArrayList<Int>()
    var preSelectedUsers = ArrayList<Int>()
    val selectedUsersCounter = MutableStateFlow(0)

    val searchString = MutableStateFlow("")
    val oldSearchString = MutableStateFlow("")

    val queryString = MutableStateFlow("")

    val offset = MutableStateFlow(0)

    fun hardReset() {
        offset.value = 0
        queryString.value = ""
        selectedUsers = ArrayList()
        selectedProjects = ArrayList()
        oldSearchString.value = ""
        searchString.value = ""
    }

    fun buildQueryString() {
        val queryList = ArrayList<String>()
        selectedProjects.let {
            if (it.size != 0) queryList.add("(" + TextUtils.join(" OR ", it.map { project -> "project:$project" }) + ")")
        }
        selectedUsers.let {
            if (it.size != 0) queryList.add("(" + TextUtils.join(" OR ", it.map { id -> "owner:$id" }) + ")")
        }
        searchString.value.let {
            if (it.isNotEmpty()) queryList.add(it)
        }
        queryString.value = TextUtils.join(" AND ", queryList)
    }

}