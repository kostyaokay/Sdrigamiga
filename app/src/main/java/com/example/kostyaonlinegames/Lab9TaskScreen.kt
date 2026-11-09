package com.example.kostyaonlinegames

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class QuestTask(val id: Long, val title: String, val isCompleted: Boolean = false, val description: String, val currentProgress: Int, val maxProgress: Int) {
    val progressPercent: Int get() = if (isCompleted) 100 else ((currentProgress.toFloat() / maxProgress) * 100).toInt()
}

class Lab9ViewModel : androidx.lifecycle.ViewModel() {
    private val _tasks = MutableStateFlow<List<QuestTask>>(emptyList())
    val tasks: StateFlow<List<QuestTask>> = _tasks.asStateFlow()
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1500)
            _tasks.value = listOf(
                QuestTask(1, "Зачистка пещер", false, "Убить 10 гоблинов.", 4, 10),
                QuestTask(2, "Травничество", false, "Собрать 5 трав.", 3, 5),
                QuestTask(3, "Потерянный меч", false, "Найти меч в руинах.", 0, 1),
                QuestTask(4, "Помощь деревне", false, "Защитить деревню.", 8, 10)
            )
            _isLoading.value = false
        }
    }

    fun toggleTask(id: Long) {
        _tasks.update { list -> list.map { if (it.id == id) it.copy(isCompleted = !it.isCompleted, currentProgress = if (!it.isCompleted) it.maxProgress else 0) else it } }
    }
}

@Composable
fun Lab9TaskScreen(onBack: () -> Unit, vm: Lab9ViewModel = viewModel()) {
    val tasks by vm.tasks.collectAsStateWithLifecycle()
    val isLoading by vm.isLoading.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F1FB)).padding(16.dp)) {
        AnimatedActionButton("← На главную", Color(0xFF5E35B1), Modifier.fillMaxWidth(), onBack)
        Spacer(Modifier.height(16.dp))
        Text("Журнал квестов", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { PulsingDots() }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(items = tasks, key = { it.id }) { task -> QuestTaskRow(task = task, onToggle = { vm.toggleTask(task.id) }) }
            }
        }
    }
}

@Composable
fun QuestTaskRow(task: QuestTask, onToggle: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val alphaValue by animateFloatAsState(targetValue = if (task.isCompleted) 0.8f else 1f, animationSpec = tween(300), label = "TaskAlpha")

    Card(
        modifier = Modifier.fillMaxWidth().alpha(alphaValue).animatedClickableCard { onToggle() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (task.isCompleted) Color(0xFFE8F5E9) else Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = task.isCompleted, onCheckedChange = null, colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50)))
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(task.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (task.isCompleted) Color(0xFF2E7D32) else Color(0xFF1A1A2E))
                        Text("${task.progressPercent}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (task.isCompleted) Color(0xFF4CAF50) else Color(0xFF5E35B1))
                    }
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(progress = { task.progressPercent / 100f }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)), color = if (task.isCompleted) Color(0xFF4CAF50) else Color(0xFF5E35B1))
                    Spacer(Modifier.height(4.dp))
                    Text(if (task.isCompleted) "✅ Выполнено" else "${task.currentProgress} / ${task.maxProgress}", fontSize = 12.sp, color = if (task.isCompleted) Color(0xFF4CAF50) else Color.Gray)
                }
                IconButton(onClick = { expanded = !expanded }) { Icon(Icons.Default.Info, contentDescription = "Подробнее", tint = Color.Gray) }
            }
            AnimatedVisibility(visible = expanded, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                Text(text = task.description, fontSize = 14.sp, color = Color(0xFF555555), modifier = Modifier.padding(top = 12.dp))
            }
        }
    }
}

@Composable
fun PulsingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) { index ->
            val scale by infiniteTransition.animateFloat(initialValue = 0.5f, targetValue = 1.2f, animationSpec = infiniteRepeatable(animation = tween(600), repeatMode = RepeatMode.Reverse, initialStartOffset = StartOffset(index * 150)), label = "DotScale")
            Box(modifier = Modifier.size(16.dp).background(Color(0xFF5E35B1), CircleShape).alpha(scale))
        }
    }
}