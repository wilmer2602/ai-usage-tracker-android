package com.wilmer2602.aiusage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wilmer2602.aiusage.data.AppDatabase
import com.wilmer2602.aiusage.data.AvatarScores
import com.wilmer2602.aiusage.data.UsageEvent
import com.wilmer2602.aiusage.repository.UsageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: UsageRepository) : ViewModel() {
    private val _avatarScores = MutableStateFlow(AvatarScores(0, 0, 0, 0))
    val avatarScores: StateFlow<AvatarScores> = _avatarScores.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allEvents.collect { events ->
                computeScores(events)
            }
        }
    }

    private fun computeScores(events: List<UsageEvent>) {
        val weights = mapOf(
            "READING" to listOf(1.0, 0.0, 0.0, 0.0),
            "VIDEO" to listOf(0.6, 0.8, 0.2, 0.1),
            "AI_TOOL" to listOf(0.4, 0.2, 0.7, 0.3),
            "CLI" to listOf(0.2, 0.0, 0.6, 0.8),
            "BROWSER" to listOf(0.7, 0.0, 0.3, 0.2),
            "DISCUSSION" to listOf(0.0, 0.7, 0.8, 0.1),
            "OTHER" to listOf(0.1, 0.1, 0.1, 0.1)
        )
        val sums = DoubleArray(4) { 0.0 }
        events.forEach { event ->
            val w = weights[event.type] ?: weights["OTHER"]!!
            val dur = event.durationMinutes.toDouble()
            sums[0] += dur * w[0]
            sums[1] += dur * w[1]
            sums[2] += dur * w[2]
            sums[3] += dur * w[3]
        }
        val max = sums.maxOrNull() ?: 1.0
        val scaled = sums.map { if (max > 100) (it * 100 / max) else it }
            .map { it.toInt().coerceIn(0, 100) }
        _avatarScores.value = AvatarScores(scaled[0], scaled[1], scaled[2], scaled[3])
    }
}

class ProfileViewModelFactory(private val repository: UsageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(repository) as T
    }
}
