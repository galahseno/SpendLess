package id.dev.spendless.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
    @PrimaryKey
    val categoryId: Int,
    val name: String,
    val type: String
)
