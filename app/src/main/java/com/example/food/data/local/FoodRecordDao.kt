package com.example.food.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: FoodRecord): Long

    @Update
    suspend fun updateRecord(record: FoodRecord)

    @Delete
    suspend fun deleteRecord(record: FoodRecord)

    @Query("SELECT * FROM food_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<FoodRecord>>

    @Query("SELECT * FROM food_records WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteRecords(): Flow<List<FoodRecord>>

    @Query("SELECT * FROM food_records WHERE id = :id LIMIT 1")
    suspend fun getRecordById(id: Long): FoodRecord?

    @Query("UPDATE food_records SET isFavorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: Long, isFav: Boolean)

    @Query("SELECT SUM(calories) FROM food_records WHERE timestamp >= :startOfDay")
    fun getTodayTotalCalories(startOfDay: Long): Flow<Int?>
}
