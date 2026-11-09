package com.example.kostyaonlinegames

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class GameEvent(val id: Int, val title: String, val category: String, val status: String)

@Composable
fun Lab5ListScreen(onBack: () -> Unit = {}) {
    val pageSize = 20
    val maxItems = 100
    var items by remember { mutableStateOf(emptyList<GameEvent>()) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun loadMore() {
        if (isLoading || items.size >= maxItems) return
        isLoading = true
        scope.launch {
            delay(800)
            val startIndex = items.size + 1
            val endIndex = (startIndex + pageSize - 1).coerceAtMost(maxItems)
            val gameNames = listOf("Битва драконов", "Космический рейд", "Остров сокровищ", "Гонки молний", "Подводный мир")
            val categories = listOf("Турнир", "Матч", "Песочница", "Соревновательный")

            val newItems = (startIndex..endIndex).map { i ->
                GameEvent(i, "${gameNames[(i - 1) % gameNames.size]} #$i", categories[(i - 1) % categories.size], "Активно")
            }
            items = items + newItems
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadMore() }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F1FB)).padding(16.dp)) {
        AnimatedActionButton("← Вернуться в меню", Color(0xFF5E35B1), Modifier.fillMaxWidth(), onBack)
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Лента событий", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
            Text("${items.size} / $maxItems", color = Color(0xFF757575), fontSize = 14.sp)
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items = items, key = { it.id }) { event ->
                GameEventCard(event)
            }
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                    if (isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    } else if (items.size < maxItems) {
                        AnimatedActionButton("Загрузить ещё", Color(0xFF5E35B1), Modifier.fillMaxWidth(), ::loadMore)
                    } else {
                        Text("Больше нет данных", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun GameEventCard(event: GameEvent) {
    val (icon, iconBgColor) = when (event.category) {
        "Турнир" -> "🏆" to Color(0xFFFFF3E0)
        "Матч" -> "⚔️" to Color(0xFFE3F2FD)
        "Песочница" -> "🏗️" to Color(0xFFE8F5E9)
        "Соревновательный" -> "🎯" to Color(0xFFFCE4EC)
        else -> "🎮" to Color(0xFFEDE7F6)
    }

    Card(
        modifier = Modifier.fillMaxWidth().animatedClickableCard { },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(iconBgColor, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                Text(icon, fontSize = 24.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E), fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text("${event.category} · ${event.status}", color = Color(0xFF757575), fontSize = 13.sp)
            }
        }
    }
}