package id.dev.spendless.dashboard.data.di

import id.dev.spendless.dashboard.data.DashboardRepositoryImpl
import id.dev.spendless.dashboard.domain.DashboardRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dashboardDataModule = module {
    singleOf(::DashboardRepositoryImpl).bind<DashboardRepository>()
}