package com.wilmer2602.aiusage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wilmer2602.aiusage.data.AppDatabase
import com.wilmer2602.aiusage.data.UsageEvent
import com.wilmer2602.aiusage.repository.UsageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsageViewModel(private val repository: UsageRepository) : ViewModel() {
    val allEvents = repository.getAllEvents()

    private val _totalDuration = MutableStateFlow<Int>(0)
    val totalDuration: StateFlow<Int> = _totalDuration.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getTotalDuration().collect { duration ->
                _totalDuration.value = duration ?: 0
            }
        }
    }

    fun insertEvent(event: UsageEvent) {
        viewModelScope.launch {
            repository.insertEvent(event)
        }
    }
}

class UsageViewModelFactory(private val database: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = UsageRepository(database)
        return UsageViewModel(repository) as T
    }
}
