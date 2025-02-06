package id.dev.spendless.auth.presentation.di

import id.dev.spendless.auth.presentation.login.LoginViewModel
import id.dev.spendless.auth.presentation.register.RegisterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
}