package id.dev.spendless.transaction.data.di

import id.dev.spendless.transaction.data.TransactionRepositoryImpl
import id.dev.spendless.transaction.domain.TransactionRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val transactionDataModule = module {
    singleOf(::TransactionRepositoryImpl).bind<TransactionRepository>()
}