package id.dev.spendless.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import id.dev.spendless.core.data.database.entity.TransactionEntity

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createTransaction(transactionEntity: TransactionEntity): Long
}