package id.dev.spendless.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import id.dev.spendless.core.data.CoreRepositoryImpl
import id.dev.spendless.core.data.database.SpendLessDb
import id.dev.spendless.core.data.database.SpendLessDbPassphrase
import id.dev.spendless.core.data.pref.SettingPreferencesImpl
import id.dev.spendless.core.data.pref.model.PinPromptAttemptPref
import id.dev.spendless.core.data.pref.model.UserSecurityPref
import id.dev.spendless.core.data.pref.model.UserSessionPref
import id.dev.spendless.core.data.pref.serializer.DataStoreSerializer
import id.dev.spendless.core.data.security.AesEncryptionService
import id.dev.spendless.core.data.security.KeyManager
import id.dev.spendless.core.domain.CoreRepository
import id.dev.spendless.core.domain.EncryptionService
import id.dev.spendless.core.domain.SettingPreferences
import net.sqlcipher.database.SupportFactory
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

private const val USER_SESSION_PREFERENCES_NAME = "user_session_preferences"
private const val USER_SECURITY_PREFERENCES_NAME = "user_security_preferences"
private const val PIN_PROMPT_PREFERENCES_NAME = "pin_prompt_preferences"
private const val PREFERENCES_DATA_STORE_NAME = "user_preferences"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_DATA_STORE_NAME
)

val userSessionDataStoreQualifier = named(USER_SESSION_PREFERENCES_NAME)
val userSecurityDataStoreQualifier = named(USER_SECURITY_PREFERENCES_NAME)
val pinPromptDataStoreQualifier = named(PIN_PROMPT_PREFERENCES_NAME)
val preferencesDataStoreQualifier = named(PREFERENCES_DATA_STORE_NAME)

val coreDataModule = module {
    singleOf(::CoreRepositoryImpl).bind<CoreRepository>()

    single(qualifier = userSessionDataStoreQualifier) {
        DataStoreFactory.create(
            serializer = DataStoreSerializer(
                encryptionService = get(),
                serializer = UserSessionPref.serializer(),
                defaultValueProvider = { UserSessionPref() }
            ),
            produceFile = { androidContext().dataStoreFile(USER_SESSION_PREFERENCES_NAME) }
        )
    }

    single(qualifier = userSecurityDataStoreQualifier) {
        DataStoreFactory.create(
            serializer = DataStoreSerializer(
                encryptionService = get(),
                serializer = UserSecurityPref.serializer(),
                defaultValueProvider = { UserSecurityPref() }
            ),
            produceFile = { androidContext().dataStoreFile(USER_SECURITY_PREFERENCES_NAME) }
        )
    }

    single(qualifier = pinPromptDataStoreQualifier) {
        DataStoreFactory.create(
            serializer = DataStoreSerializer(
                encryptionService = get(),
                serializer = PinPromptAttemptPref.serializer(),
                defaultValueProvider = { PinPromptAttemptPref() }
            ),
            produceFile = { androidContext().dataStoreFile(PIN_PROMPT_PREFERENCES_NAME) }
        )
    }

    single(qualifier = preferencesDataStoreQualifier) {
        androidContext().dataStore
    }

    single {
        SettingPreferencesImpl(
            get(qualifier = preferencesDataStoreQualifier),
            get(qualifier = userSessionDataStoreQualifier),
            get(qualifier = userSecurityDataStoreQualifier),
            get(qualifier = pinPromptDataStoreQualifier)
        )
    }.bind<SettingPreferences>()

    singleOf(::SpendLessDbPassphrase)

    single {
        val supportFactory = SupportFactory(get<SpendLessDbPassphrase>().getPassphrase())
        Room.databaseBuilder(
            androidApplication(),
            SpendLessDb::class.java,
            "spendless.db"
        )
            .openHelperFactory(supportFactory)
            .build()
    }
    single { get<SpendLessDb>().transactionDao() }
    single { get<SpendLessDb>().userDao() }
    single { get<SpendLessDb>().preferencesDao() }

    single {
        val passphrase = get<SpendLessDbPassphrase>().getPassphrase()
        KeyManager.getOrCreateSecretKey(passphrase)
    }
    singleOf(::AesEncryptionService).bind<EncryptionService>()
}