package id.dev.spendless.core.data.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingPreferences(private val dataStore: DataStore<Preferences>) {

    fun getUserSession(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USER_KEY] ?: ""
        }
    }

    suspend fun saveUserSession(username: String) {
        dataStore.edit { preferences ->
            preferences[USER_KEY] = username
        }
    }

    private companion object {
        val USER_KEY = stringPreferencesKey("user_session")
        val EXPENSES_FORMAT_KEY = stringPreferencesKey("expenses_format")
        val CURRENCY_KEY = stringPreferencesKey("Currency")
        val DECIMAL_SEPARATOR_KEY = stringPreferencesKey("decimal_separator")
        val THOUSAND_SEPARATOR_KEY = stringPreferencesKey("thousand_separator")

        val BIOMETRICS_KEY = booleanPreferencesKey("biometrics")
        val SESSION_EXPIRED_KEY = intPreferencesKey("session_expired")
        val LOCKED_DURATION_KEY = intPreferencesKey("locked_duration")
    }
}