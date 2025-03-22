package id.dev.spendless.transaction.presentation.di

import id.dev.spendless.transaction.presentation.AllTransactionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val transactionPresentationModule = module {
    viewModelOf(::AllTransactionViewModel)
}