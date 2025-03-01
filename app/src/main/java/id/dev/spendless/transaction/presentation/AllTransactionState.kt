package id.dev.spendless.transaction.presentation

import id.dev.spendless.core.domain.model.TransactionGroup

data class AllTransactionState(
    val allTransactions: List<TransactionGroup> = emptyList(),
    val showBottomSheet: Boolean = false
)
