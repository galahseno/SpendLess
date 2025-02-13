package id.dev.spendless.core.presentation.add_transaction

sealed interface AddTransactionEvent {
    data object OnAddTransactionSuccess : AddTransactionEvent
}