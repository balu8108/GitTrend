package com.bala.gittrend.datastore

import androidx.datastore.preferences.core.longPreferencesKey

object DataStorePreferenceKeys {
    val LAST_SUCCESS_API_CALL = longPreferencesKey("last_success_api_call")
}