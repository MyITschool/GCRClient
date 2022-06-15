package com.elseboot3909.gcrclient.repository.search

import android.text.TextUtils
import android.util.Log
import com.elseboot3909.gcrclient.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow

class SearchParamsRepository {

    val selectedProjectsCounter = MutableStateFlow(0)
    var selectedProjects = ArrayList<String>()
    val selectedUsersCounter = MutableStateFlow(0)
    var selectedUsers = ArrayList<Int>()
    var searchString = ""

    private var oldSearchString = ""
    private var oldSelectedProjects = ArrayList<String>()
    private var oldSelectedUsers = ArrayList<Int>()

    val queryString = MutableStateFlow("")
    val offset = MutableStateFlow(0)

    init {
        queryString.value = ""
    }

    fun appendSelectedProjects(projectName: String) {
        selectedProjects.add(projectName)
        selectedProjectsCounter.value++
    }

    fun removeSelectedProject(projectName: String) {
        selectedProjects.remove(projectName)
        selectedProjectsCounter.value--
    }

    fun appendSelectedUser(_account_id: Int) {
        selectedUsers.add(_account_id)
        selectedUsersCounter.value++
    }

    fun removeSelectedUser(_account_id: Int) {
        selectedUsers.remove(_account_id)
        selectedUsersCounter.value--
    }

    fun applyUsers() {
        oldSelectedUsers = selectedUsers
    }

    fun restoreUsers() {
        selectedUsers = oldSelectedUsers
        selectedUsersCounter.value = oldSelectedUsers.size
    }

    fun applyProjects() {
        oldSelectedProjects = selectedProjects
    }

    fun restoreProjects() {
        selectedProjects = oldSelectedProjects
        selectedProjectsCounter.value = oldSelectedProjects.size
    }

    fun applySearchParams() {
        oldSearchString = searchString
        applyProjects()
        buildQueryString()
    }

    fun restoreSearchParams() {
        searchString = oldSearchString
        restoreProjects()
    }

    fun erase() {
        selectedProjects.clear(); searchString = ""; queryString.value = ""; offset.value = 0
    }

    private fun buildQueryString() {
        val queryList = ArrayList<String>()
        selectedProjects.let {
            if (it.size != 0) queryList.add("(" + TextUtils.join(" OR ", it.map { project -> "project:$project" }) + ")")
        }
        selectedUsers.let {
            if (it.size != 0) queryList.add("(" + TextUtils.join(" OR ", it.map { id -> "owner:$id" }) + ")")
        }
        searchString.let {
            if (it.isNotEmpty()) queryList.add(it)
        }
        queryString.value = TextUtils.join(" AND ", queryList)
        Log.e(Constants.LOG_TAG, queryString.value)
    }

}