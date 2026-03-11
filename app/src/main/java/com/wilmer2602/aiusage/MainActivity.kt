package com.wilmer2602.aiusage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.wilmer2602.aiusage.data.AppDatabase
import com.wilmer2602.aiusage.repository.UsageRepository
import com.wilmer2602.aiusage.ui.screens.MainScreen
import com.wilmer2602.aiusage.ui.theme.AIUsageTrackerTheme
import com.wilmer2602.aiusage.viewmodel.ProfileViewModel
import com.wilmer2602.aiusage.viewmodel.UsageViewModel
import com.wilmer2602.aiusage.viewmodel.UsageViewModelFactory
import com.wilmer2602.aiusage.viewmodel.ProfileViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(this)
        val repository = UsageRepository(database)
        val usageViewModel = ViewModelProvider(this, UsageViewModelFactory(database)).get(UsageViewModel::class.java)
        val profileViewModel = ViewModelProvider(this, ProfileViewModelFactory(repository)).get(ProfileViewModel::class.java)

        setContent {
            AIUsageTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(usageViewModel, profileViewModel)
                }
            }
        }
    }
}
