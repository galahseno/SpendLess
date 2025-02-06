package id.dev.spendless.settings.data.di

import id.dev.spendless.settings.data.SettingRepositoryImpl
import id.dev.spendless.settings.domain.SettingsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val settingsDataModule = module {
    singleOf(::SettingRepositoryImpl).bind<SettingsRepository>()
}