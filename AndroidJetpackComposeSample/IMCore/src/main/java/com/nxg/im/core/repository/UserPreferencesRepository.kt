package com.nxg.im.core.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import com.nxg.im.core.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

class UserPreferencesRepository(
    private val userPreferencesStore: DataStore<UserPreferences>,
    context: Context
) {

    companion object {

        private const val TAG: String = "UserPreferencesRepo"
    }

    val userPreferencesFlow: Flow<UserPreferences> = userPreferencesStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading sort order preferences.", exception)
                emit(UserPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

}