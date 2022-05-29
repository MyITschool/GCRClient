package com.elseboot3909.gcrclient.ui.main

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchParamsViewModel : ViewModel() {

    private val _searchStr by lazy {
        MutableLiveData("")
    }

    private val _searchProjects by lazy {
        MutableLiveData(ArrayList<String>())
    }

    private val _queryList by lazy {
        MutableLiveData("")
    }

    fun setQuery(searchStr: String, searchProjects: ArrayList<String>) {
        _searchStr.postValue(searchStr)
        _searchProjects.postValue(searchProjects)
        val queryList = ArrayList<String>()
        if (searchProjects.size != 0) queryList.add(TextUtils.join(" OR ", searchProjects.map { project -> "project:$project" }))
        if (searchStr.isNotEmpty()) queryList.add(searchStr)
        _queryList.postValue(TextUtils.join("+", queryList))
    }

    fun getQuery(): LiveData<String> {
        return _queryList
    }

    fun getSearchStr(): LiveData<String> {
        return _searchStr
    }

}