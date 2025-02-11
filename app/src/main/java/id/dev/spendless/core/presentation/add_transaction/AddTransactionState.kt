package id.dev.spendless.core.presentation.add_transaction

import id.dev.spendless.core.presentation.ui.transaction.TransactionTypeEnum

data class AddTransactionState(
    val transactionName: String = "",
    val selectedTransactionType: TransactionTypeEnum = TransactionTypeEnum.Expenses,
    //
)
