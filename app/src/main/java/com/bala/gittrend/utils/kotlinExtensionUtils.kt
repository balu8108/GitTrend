package com.bala.gittrend.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

object kotlinExtensionUtils {
    private const val LOCAL_DATA_STORE = "local data store"
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(LOCAL_DATA_STORE)
}