package id.dev.spendless.dashboard.presentation

sealed interface DashboardAction {
    data object OnShowAllClick: DashboardAction
}