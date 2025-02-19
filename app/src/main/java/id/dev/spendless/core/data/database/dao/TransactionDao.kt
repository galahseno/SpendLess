package id.dev.spendless.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.dev.spendless.core.data.database.entity.TransactionEntity
import id.dev.spendless.core.domain.model.CategoryWithEmoji
import id.dev.spendless.core.domain.model.LargestTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createTransaction(transactionEntity: TransactionEntity): Long

    @Query("SELECT SUM(amount) FROM transactions WHERE user_id = :userId")
    fun getTotalBalance(userId: Int): Flow<Double?>

    @Query(
        "SELECT categoryName, categoryEmoji FROM transactions WHERE amount < 0 " +
                "AND user_id = :userId GROUP BY categoryName ORDER BY SUM(amount) ASC LIMIT 1"
    )
    fun getLargestTransactionCategoryAllTime(userId: Int): Flow<CategoryWithEmoji?>

    @Query(
        "SELECT transactionName, amount, createdAt FROM transactions WHERE amount < 0 " +
                "AND user_id = :userId ORDER BY amount ASC LIMIT 1"
    )
    fun getLargestTransaction(userId: Int): Flow<LargestTransaction?>

    @Query(
        "SELECT SUM(amount) FROM transactions WHERE user_id = :userId " +
                "AND createdAt >= :startOfPreviousWeek AND createdAt < :startOfCurrentWeek " +
                "AND amount < 0"
    )
    fun getTotalSpentPreviousWeek(
        userId: Int,
        startOfPreviousWeek: Long, startOfCurrentWeek: Long
    ): Flow<Double?>

    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY createdAt DESC")
    fun getAllTransactions(userId: Int): Flow<List<TransactionEntity>>
}