package id.dev.spendless.dashboard.domain

import id.dev.spendless.core.domain.model.transaction.CategoryWithEmoji
import id.dev.spendless.core.domain.model.transaction.LargestTransaction
import id.dev.spendless.core.domain.model.transaction.Transaction
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getTotalBalance(): Flow<Double?>
    fun getLargestTransactionCategoryAllTime(): Flow<CategoryWithEmoji?>
    fun getLargestTransaction(): Flow<LargestTransaction?>
    fun getTotalSpentPreviousWeek(): Flow<Double?>
    fun getLatestTransactions(): Flow<List<Transaction>>
    fun getAllTransactions(): Flow<List<Transaction>>
}