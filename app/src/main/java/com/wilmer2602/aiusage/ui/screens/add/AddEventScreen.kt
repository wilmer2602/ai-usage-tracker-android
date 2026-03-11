package com.wilmer2602.aiusage.ui.screens.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wilmer2602.aiusage.data.UsageEvent
import com.wilmer2602.aiusage.viewmodel.UsageViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    viewModel: UsageViewModel
) {
    var typeExpanded by remember { mutableStateOf(false) }
    val types = listOf("READING", "VIDEO", "AI_TOOL", "CLI", "BROWSER", "DISCUSSION", "OTHER")
    var selectedType by remember { mutableStateOf(types.first()) }

    var toolExpanded by remember { mutableStateOf(false) }
    val tools = listOf("ChatGPT", "Claude", "Gemini", "Kimi", "Copilot", "其他")
    var selectedTool by remember { mutableStateOf(tools.first()) }
    var customTool by remember { mutableStateOf("") }

    var modeExpanded by remember { mutableStateOf(false) }
    val modes = listOf("Chat", "Generate", "Search", "Code", "Analyze", "Other")
    var selectedMode by remember { mutableStateOf(modes.first()) }

    var duration by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("添加 AI 使用事件", style = MaterialTheme.typography.headlineSmall)

            // Event type dropdown
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = !typeExpanded }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("类型") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    types.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedType = type
                                typeExpanded = false
                            }
                        )
                    }
                }
            }

            // Tool dropdown
            ExposedDropdownMenuBox(
                expanded = toolExpanded,
                onExpandedChange = { toolExpanded = !toolExpanded }
            ) {
                OutlinedTextField(
                    value = if (selectedTool == "其他") customTool else selectedTool,
                    onValueChange = { 
                        if (selectedTool == "其他") customTool = it
                    },
                    readOnly = selectedTool != "其他",
                    label = { Text("AI 工具") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toolExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = toolExpanded,
                    onDismissRequest = { toolExpanded = false }
                ) {
                    tools.forEach { tool ->
                        DropdownMenuItem(
                            text = { Text(tool) },
                            onClick = {
                                selectedTool = tool
                                if (tool != "其他") customTool = ""
                                toolExpanded = false
                            }
                        )
                    }
                }
            }

            // Mode dropdown
            ExposedDropdownMenuBox(
                expanded = modeExpanded,
                onExpandedChange = { modeExpanded = !modeExpanded }
            ) {
                OutlinedTextField(
                    value = selectedMode,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("使用方式") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modeExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = modeExpanded,
                    onDismissRequest = { modeExpanded = false }
                ) {
                    modes.forEach { mode ->
                        DropdownMenuItem(
                            text = { Text(mode) },
                            onClick = {
                                selectedMode = mode
                                modeExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it.filter { ch -> ch.isDigit() } },
                label = { Text("时长 (分钟)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("备注 (可选)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = {
                        customTool = ""
                        duration = ""
                        notes = ""
                    }
                ) {
                    Text("清空")
                }
                Button(
                    onClick = {
                        val durationInt = duration.toIntOrNull() ?: 0
                        val toolName = if (selectedTool == "其他") customTool.ifBlank { null } else selectedTool
                        val event = UsageEvent(
                            type = selectedType,
                            appName = null,
                            toolName = toolName,
                            timestamp = System.currentTimeMillis(),
                            durationMinutes = durationInt,
                            notes = notes.ifBlank { null }
                        )
                        viewModel.insertEvent(event)
                        customTool = ""
                        duration = ""
                        notes = ""
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("保存")
                }
            }
        }
    }
}
