package id.dev.spendless.dashboard.domain

import id.dev.spendless.core.domain.model.Transaction
import id.dev.spendless.core.domain.model.CategoryWithEmoji
import id.dev.spendless.core.domain.model.LargestTransaction
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getTotalBalance(): Flow<Double?>
    fun getLargestTransactionCategoryAllTime(): Flow<CategoryWithEmoji?>
    fun getLargestTransaction(): Flow<LargestTransaction?>
    fun getTotalSpentPreviousWeek(): Flow<Double?>
    fun getAllTransactions(): Flow<List<Transaction>>
}