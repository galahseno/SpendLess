package id.dev.spendless.transaction.presentation

sealed interface AllTransactionAction {
    data object OnBackClick : AllTransactionAction
}