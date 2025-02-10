package id.dev.spendless.core.data.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingPreferencesImpl(
    private val dataStore: DataStore<Preferences>
) : SettingPreferences {

    override fun getUserStatus(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[USER_ID_KEY] ?: -1
        }
    }

    override fun getUserSession(): Flow<UserSession> {
        return dataStore.data.map {
            UserSession(
                username = it[USER_NAME_KEY] ?: "",
                expensesFormat = it[EXPENSES_FORMAT_KEY] ?: "",
                currencySymbol = it[CURRENCY_KEY] ?: "",
                decimalSeparator = it[DECIMAL_SEPARATOR_KEY] ?: "",
                thousandSeparator = it[THOUSAND_SEPARATOR_KEY] ?: "",
            )
        }
    }

    override suspend fun saveRegisterSession(
        userId: Int,
        username: String,
        expensesFormat: String,
        currencySymbol: String,
        decimalSeparator: String,
        thousandSeparator: String
    ) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = username
            preferences[EXPENSES_FORMAT_KEY] = expensesFormat
            preferences[CURRENCY_KEY] = currencySymbol
            preferences[DECIMAL_SEPARATOR_KEY] = decimalSeparator
            preferences[THOUSAND_SEPARATOR_KEY] = thousandSeparator
        }
    }

    override suspend fun saveLoginSession(
        userId: Int,
        username: String,
    ) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = username
        }
    }

    private companion object {
        val USER_ID_KEY = intPreferencesKey("user_id")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val EXPENSES_FORMAT_KEY = stringPreferencesKey("expenses_format")
        val CURRENCY_KEY = stringPreferencesKey("Currency")
        val DECIMAL_SEPARATOR_KEY = stringPreferencesKey("decimal_separator")
        val THOUSAND_SEPARATOR_KEY = stringPreferencesKey("thousand_separator")

        val BIOMETRICS_KEY = booleanPreferencesKey("biometrics")
        val SESSION_EXPIRED_KEY = intPreferencesKey("session_expired")
        val LOCKED_DURATION_KEY = intPreferencesKey("locked_duration")
    }
}