package com.thor.swim.tracker.data

import androidx.room.Dao
import androidx.room.Upsert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface NumberDao {
    @Upsert
    suspend fun upsert(entry: NumberEntry)

    @Query("SELECT * FROM number_entries WHERE date = :date LIMIT 1")
    fun observeByDate(date: LocalDate): Flow<NumberEntry?>

    @Query("SELECT * FROM number_entries ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<NumberEntry>>
}
