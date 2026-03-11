package com.wilmer2602.aiusage.repository

import com.wilmer2602.aiusage.data.AppDatabase
import com.wilmer2602.aiusage.data.UsageEvent
import kotlinx.coroutines.flow.Flow

class UsageRepository(private val database: AppDatabase) {
    private val dao = database.usageEventDao()

    fun getAllEvents(): Flow<List<UsageEvent>> = dao.getAllEvents()

    suspend fun insertEvent(event: UsageEvent) = dao.insert(event)

    suspend fun updateEvent(event: UsageEvent) = dao.update(event)

    suspend fun deleteEvent(event: UsageEvent) = dao.delete(event)

    fun getTotalDuration(): Flow<Int?> = dao.getTotalDuration()
}
