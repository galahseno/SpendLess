package id.dev.spendless.auth.data.di

import id.dev.spendless.auth.data.AuthRepositoryImpl
import id.dev.spendless.auth.domain.AuthRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule = module {
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
}