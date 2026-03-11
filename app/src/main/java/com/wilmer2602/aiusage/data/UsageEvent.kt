package com.wilmer2602.aiusage.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usage_events")
data class UsageEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // READING, VIDEO, AI_TOOL, CLI, BROWSER, DISCUSSION, OTHER
    val appName: String? = null,
    val toolName: String? = null,
    val toolMode: String? = null, // Chat, Generate, Search, Code, Analyze, Other
    val timestamp: Long,
    val durationMinutes: Int = 0,
    val notes: String? = null
)
