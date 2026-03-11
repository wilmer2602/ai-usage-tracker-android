package com.wilmer2602.aiusage.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wilmer2602.aiusage.data.UsageEvent
import com.wilmer2602.aiusage.viewmodel.UsageViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: UsageViewModel
) {
    val events by viewModel.allEvents.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "最近记录",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (events.isEmpty()) {
            Text(
                text = "暂无记录，点击底部\"添加\"开始记录。",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(events.take(50)) { event ->
                    EventCard(event = event)
                }
            }
        }
    }
}

@Composable
fun EventCard(event: UsageEvent) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val typeDisplay = mapOf(
        "READING" to "阅读文章",
        "VIDEO" to "观看视频",
        "AI_TOOL" to "AI 工具",
        "CLI" to "命令行",
        "BROWSER" to "浏览器",
        "DISCUSSION" to "讨论 AI",
        "OTHER" to "其他"
    )
    val modeDisplay = mapOf(
        "Chat" to "对话",
        "Generate" to "生成",
        "Search" to "搜索",
        "Code" to "编码",
        "Analyze" to "分析",
        "Other" to "其他"
    )
    Card(
        modifier = Modifier.padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "类型: ${typeDisplay[event.type] ?: event.type}", style = MaterialTheme.typography.bodyMedium)
            event.toolName?.takeIf { it.isNotBlank() }?.let {
                Text(text = "工具: $it", style = MaterialTheme.typography.bodySmall)
            }
            event.toolMode?.takeIf { it.isNotBlank() }?.let {
                Text(text = "方式: ${modeDisplay[it] ?: it}", style = MaterialTheme.typography.bodySmall)
            }
            Text(text = "时间: ${dateFormat.format(Date(event.timestamp))}", style = MaterialTheme.typography.bodySmall)
            Text(text = "时长: ${event.durationMinutes} 分钟", style = MaterialTheme.typography.bodySmall)
            event.notes?.takeIf { it.isNotBlank() }?.let {
                Text(text = "备注: $it", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
