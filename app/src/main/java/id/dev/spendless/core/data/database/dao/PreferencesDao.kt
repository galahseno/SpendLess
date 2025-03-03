package id.dev.spendless.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.dev.spendless.core.data.database.entity.PreferencesEntity

@Dao
interface PreferencesDao {
    @Query("SELECT * FROM preferences WHERE user_id = :userId")
    suspend fun getPreferences(userId: Int): PreferencesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(preferencesEntity: PreferencesEntity): Long
}