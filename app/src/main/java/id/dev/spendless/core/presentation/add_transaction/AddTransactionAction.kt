package id.dev.spendless.core.presentation.add_transaction

import id.dev.spendless.core.presentation.ui.transaction.TransactionTypeEnum

sealed interface AddTransactionAction {
    data object OnCloseModalSheet: AddTransactionAction
    data class OnTransactionTypeSelected(val type: TransactionTypeEnum) : AddTransactionAction
}