package com.elseboot3909.gcrclient.credentials.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking


internal class SelectedDataStore(context: Context) {
    private val name = "selectedServer"

    private val Context.selectionConfig: DataStore<Preferences> by preferencesDataStore(name)

    private val selectedServer = intPreferencesKey(name)

    private val dataStore = context.selectionConfig

    val selected: Flow<Int> = dataStore.data.map { it[selectedServer] ?: 0 }

    fun updateSelected(i: Int) {
        runBlocking {
            dataStore.edit {
                it[selectedServer] = i
            }
        }
    }
}