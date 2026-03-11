package com.wilmer2602.aiusage.ui.screens.profile

import androidx.compose.foundation.layout.Arrangements
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wilmer2602.aiusage.ui.components.AvatarView
import com.wilmer2602.aiusage.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    val scores by viewModel.avatarScores.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangements.spacedBy(16.dp)
    ) {
        Text(
            text = "用户画像",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Avatar
        AvatarView(
            visualScore = scores.visual,
            audioScore = scores.audio,
            languageScore = scores.language,
            motorScore = scores.motor,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        // Scores display
        Text(
            text = "视觉: ${scores.visual}% | 听觉: ${scores.audio}% | 语言: ${scores.language}% | 运动: ${scores.motor}%",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "拖拽旋转头像，双指缩放",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
