package id.dev.spendless.main.di

import id.dev.spendless.SpendLessApp
import id.dev.spendless.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<CoroutineScope> {
        (androidApplication() as SpendLessApp).applicationScope
    }
    viewModelOf(::MainViewModel)
}