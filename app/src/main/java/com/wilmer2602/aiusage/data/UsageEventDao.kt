package com.wilmer2602.aiusage.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageEventDao {
    @Insert
    suspend fun insert(event: UsageEvent): Long

    @Update
    suspend fun update(event: UsageEvent)

    @Delete
    suspend fun delete(event: UsageEvent)

    @Query("SELECT * FROM usage_events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<UsageEvent>>

    @Query("SELECT * FROM usage_events WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    fun getEventsBetween(start: Long, end: Long): Flow<List<UsageEvent>>

    @Query("SELECT SUM(durationMinutes) FROM usage_events")
    fun getTotalDuration(): Flow<Int?>
}
