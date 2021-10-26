package me.padamchopra.todocompose.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import me.padamchopra.todocompose.data.models.Priority
import me.padamchopra.todocompose.util.Constants.PREFERENCE_KEY
import me.padamchopra.todocompose.util.Constants.PREFERENCE_NAME
import java.io.IOException
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(PREFERENCE_NAME)

@ViewModelScoped
class DataStoreRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object PreferenceKeys {
        val sortState = stringPreferencesKey(PREFERENCE_KEY)
    }

    private val dataStore = context.dataStore

    suspend fun persistSortState(priority: Priority) {
        dataStore.edit {
            it[PreferenceKeys.sortState] = priority.name
        }
    }

    val readSortState: Flow<String> = dataStore.data
        .catch {exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            it[PreferenceKeys.sortState] ?: Priority.NONE.name
        }
}