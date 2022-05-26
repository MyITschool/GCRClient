package com.elseboot3909.gcrclient.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchParamsViewModel : ViewModel() {

    private val _searchStr by lazy {
        MutableLiveData("")
    }

    fun setSearchStr(searchStr: String) {
        _searchStr.postValue(searchStr)
    }

    fun getSearchStr() : LiveData<String> {
        return _searchStr
    }

}