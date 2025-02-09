package id.dev.spendless.core.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Int,
    val userId: Int,
    val categoryId: Int,
    val transactionName: String,
    val amount: Int,
    val note: String = "",
    val createdAt: Long,
    // val repeat
)
