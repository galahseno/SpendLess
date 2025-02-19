package id.dev.spendless.dashboard.presentation

sealed interface DashboardAction {
    data object OnShowAllClick: DashboardAction
    data object OnSettingClick: DashboardAction
    data class OnItemTransactionClick(val id: Int): DashboardAction
}