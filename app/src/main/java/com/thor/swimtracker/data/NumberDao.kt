package com.thor.swimtracker.data

import androidx.room.Dao
import androidx.room.Upsert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NumberDao {
    @Upsert
    suspend fun upsert(entry: NumberEntry)

    @Query("SELECT * FROM number_entries WHERE date = :date LIMIT 1")
    fun observeByDate(date: String): Flow<NumberEntry?>

    @Query("SELECT * FROM number_entries ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<NumberEntry>>
}
