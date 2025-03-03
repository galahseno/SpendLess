package id.dev.spendless.transaction.presentation

import id.dev.spendless.core.domain.model.transaction.TransactionGroup

data class AllTransactionState(
    val allTransactions: List<TransactionGroup> = emptyList(),
    val showBottomSheet: Boolean = false
)
