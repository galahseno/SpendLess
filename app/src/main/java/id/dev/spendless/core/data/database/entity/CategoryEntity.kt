package id.dev.spendless.core.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey
    val categoryId: Int,
    val name: String,
    val type: String
)
