package id.dev.spendless.core.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    val transactionName: String,
    val categoryEmoji: String,
    val categoryName: String,
    val amount: Double,
    val note: String = "",
    val createdAt: Long,
    val repeat: String
)
