package id.dev.spendless.dashboard.presentation.di

import id.dev.spendless.dashboard.presentation.DashboardViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dashboardPresentationModule = module {
    viewModelOf(::DashboardViewModel)
}