package id.dev.spendless.transaction.presentation

sealed interface AllTransactionAction {
    data object OnBackClick : AllTransactionAction
    data object OnExportClick : AllTransactionAction
    data object OnFABClick : AllTransactionAction
    data object OnCloseBottomSheet : AllTransactionAction
    data class OnItemTransactionClick(val id: Int): AllTransactionAction
}