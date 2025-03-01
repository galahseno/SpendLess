package id.dev.spendless.dashboard.presentation

import id.dev.spendless.core.domain.model.CategoryWithEmoji
import id.dev.spendless.core.domain.model.LargestTransaction
import id.dev.spendless.core.domain.model.TransactionGroup

data class DashboardState(
    val username: String = "",
    val balance: String = "0",
    val negativeBalance: Boolean = true,
    val previousWeekSpend: String = "0",
    val largestTransactionCategoryAllTime: CategoryWithEmoji = CategoryWithEmoji(),
    val largestTransaction: LargestTransaction = LargestTransaction(),
    val allTransactions: List<TransactionGroup> = emptyList(),
    val showBottomSheet: Boolean = false
    //
)
