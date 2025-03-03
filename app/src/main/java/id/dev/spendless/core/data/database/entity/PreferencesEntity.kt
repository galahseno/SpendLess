package id.dev.spendless.core.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preferences")
data class PreferencesEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: Int,
    val expensesFormat: String,
    val currencySymbol: String,
    val decimalSeparator: String,
    val thousandSeparator: String,

    val biometricPromptEnable: Boolean,
    val sessionExpiryDuration: Long,
    val lockedOutDuration: Long
)
