package id.dev.spendless.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.dev.spendless.core.data.database.entity.UserEntity

@Dao
interface UserDao {
    @Query("Select Exists(Select * From user Where username = :username)")
    suspend fun isUserExist(username: String): Boolean

    @Query("SELECT * FROM user WHERE id = :userId")
    suspend fun getUserById(userId: Int): UserEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createUser(user: UserEntity): Long

    @Query("Select * From user Where username = :username")
    suspend fun loginAccount(username: String): UserEntity?
}