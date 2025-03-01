package id.dev.spendless.dashboard.presentation

sealed interface DashboardAction {
    data object OnShowAllClick: DashboardAction
    data object OnFABClick: DashboardAction
    data object OnSettingClick: DashboardAction
    data object OnCloseBottomSheet: DashboardAction
    data class OnItemTransactionClick(val id: Int): DashboardAction
}