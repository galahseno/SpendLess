package id.dev.spendless.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import id.dev.spendless.core.data.CoreRepositoryImpl
import id.dev.spendless.core.data.database.SpendLessDb
import id.dev.spendless.core.data.pref.SettingPreferences
import id.dev.spendless.core.domain.CoreRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

val coreDataModule = module {
    singleOf(::CoreRepositoryImpl).bind<CoreRepository>()

    single { androidContext().dataStore }
    singleOf(::SettingPreferences)

    single {
        // TODO Encrypt database
        Room.databaseBuilder(
            androidApplication(),
            SpendLessDb::class.java,
            "story.db"
        )
            .build()
    }
    single { get<SpendLessDb>().categoryDao() }
    single { get<SpendLessDb>().transactionDao() }
    single { get<SpendLessDb>().userDao() }
}