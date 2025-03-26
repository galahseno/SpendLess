package id.dev.spendless.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.dev.spendless.core.data.database.entity.TransactionEntity
import id.dev.spendless.core.domain.model.transaction.Balance
import id.dev.spendless.core.domain.model.transaction.EncryptedCategoryWithEmoji
import id.dev.spendless.core.domain.model.transaction.EncryptedLargestTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createTransaction(transactionEntity: TransactionEntity): Long

    @Query("SELECT amount, amountIv FROM transactions WHERE user_id = :userId")
    fun getTotalBalance(userId: Int): Flow<List<Balance>>

    @Query(
        "SELECT categoryName, categoryEmoji, amount, categoryNameIv, categoryEmojiIv, amountIv " +
                "FROM transactions WHERE user_id = :userId"
    )
    fun getLargestTransactionCategoryAllTime(userId: Int): Flow<List<EncryptedCategoryWithEmoji?>>


    @Query(
        "SELECT transactionName, amount, createdAt, transactionNameIv, amountIv" +
                " FROM transactions WHERE user_id = :userId"
    )
    fun getLargestTransaction(userId: Int): Flow<List<EncryptedLargestTransaction?>>

    @Query(
        "SELECT amount, amountIv FROM transactions WHERE user_id = :userId " +
                "AND createdAt >= :startOfPreviousWeek AND createdAt < :startOfCurrentWeek"
    )
    fun getTotalSpentPreviousWeek(
        userId: Int,
        startOfPreviousWeek: Long, startOfCurrentWeek: Long
    ): Flow<List<Balance>>

    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY createdAt DESC")
    fun getAllTransactions(userId: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY createdAt DESC Limit 20")
    fun getLatestTransactions(userId: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND createdAt >= :startDate")
    suspend fun getTransactionsAfter(userId: Int, startDate: Long): List<TransactionEntity>

    @Query("""
    SELECT * FROM transactions 
    WHERE user_id = :userId 
    AND datetime(createdAt / 1000, 'unixepoch') 
        BETWEEN datetime(:startDate, 'unixepoch') 
        AND datetime(:endDate, 'unixepoch')
    ORDER BY createdAt DESC
    """)
    suspend fun getTransactionsBetweenDates(
        userId: Int,
        startDate: Long,
        endDate: Long
    ): List<TransactionEntity>
    //1743003798893
    //1740762000

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND strftime('%Y', datetime(createdAt / 1000, 'unixepoch')) = :year " +
            "AND strftime('%m', datetime(createdAt / 1000, 'unixepoch')) = :month")
    suspend fun getTransactionsByMonth(userId: Int, year: String, month: String): List<TransactionEntity>
}