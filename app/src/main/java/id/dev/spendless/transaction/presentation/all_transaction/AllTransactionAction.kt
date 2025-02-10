package id.dev.spendless.transaction.presentation.all_transaction

sealed interface AllTransactionAction {
    data object OnBackClick : AllTransactionAction
    data object OnAddTransactionClick : AllTransactionAction
}