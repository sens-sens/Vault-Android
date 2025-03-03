package com.androsmith.vault.data.datasource


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import androidx.datastore.preferences.preferencesDataStore


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class LocalUserPreferenceDataSource @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        val DEFAULT_CATEGORY_KEY = stringPreferencesKey("default_category")
    }

    val defaultCategory: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[DEFAULT_CATEGORY_KEY]
        }

    suspend fun setDefaultCategory(category: String) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_CATEGORY_KEY] = category
        }
    }
}