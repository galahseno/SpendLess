package id.dev.spendless.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import id.dev.spendless.core.data.database.dao.CategoryDao
import id.dev.spendless.core.data.database.dao.TransactionDao
import id.dev.spendless.core.data.database.dao.UserDao
import id.dev.spendless.core.data.database.entity.CategoryEntity
import id.dev.spendless.core.data.database.entity.TransactionEntity
import id.dev.spendless.core.data.database.entity.UserEntity

@Database(
    version = 1,
    entities = [CategoryEntity::class, TransactionEntity::class, UserEntity::class],
    exportSchema = false
)
abstract class SpendLessDb: RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun userDao(): UserDao
}