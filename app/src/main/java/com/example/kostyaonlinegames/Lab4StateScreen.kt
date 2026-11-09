package com.example.kostyaonlinegames

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Lab4StateScreen(onBack: () -> Unit = {}) {
    var isDarkTheme by rememberSaveable { mutableStateOf(false) }
    val bgColor = if (isDarkTheme) Color(0xFF1E1E24) else Color(0xFFF4F1FB)
    val textColor = if (isDarkTheme) Color.White else Color(0xFF1A1A2E)
    val cardColor = if (isDarkTheme) Color(0xFF2C2C35) else Color.White

    Column(modifier = Modifier.fillMaxSize().background(bgColor).padding(16.dp)) {
        // Шапка с кнопкой Назад и переключателем темы
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            AnimatedActionButton("← На главную", Color(0xFF5E35B1), Modifier.weight(1f), onBack)
            Spacer(Modifier.width(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (isDarkTheme) "🌙" else "☀️", color = textColor, fontSize = 14.sp)
                Spacer(Modifier.width(8.dp))
                Switch(checked = isDarkTheme, onCheckedChange = { isDarkTheme = it })
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Прокачка", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor, modifier = Modifier.padding(bottom = 16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.weight(1f)) {
            item { Part1Counter(cardColor, textColor) }
            item { Part2Search(cardColor, textColor) }
            item { Part3ExpandableList(cardColor, textColor) }
            item { Part4FormAndCheckboxes(cardColor, textColor) }
        }
    }
}

@Composable
fun Part1Counter(cardColor: Color, textColor: Color) {
    var strength by rememberSaveable { mutableStateOf(0) }
    var intellect by rememberSaveable { mutableStateOf(0) }
    val maxPoints = 5
    val pointsLeft = maxPoints - (strength + intellect)

    Card(colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("1. Прокачка (макс. $maxPoints)", fontWeight = FontWeight.Bold, color = textColor)
                Text("Осталось: $pointsLeft", color = if (pointsLeft > 0) MaterialTheme.colorScheme.primary else Color.Gray, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))

            // Сила
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("💪 Сила: $strength", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Button(onClick = { if (strength > 0) strength-- }, enabled = strength > 0, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(40.dp)) { Text("-") }
                    Button(onClick = { strength = 0 }, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) { Text("0") }
                    Button(onClick = { if (pointsLeft > 0) strength++ }, enabled = pointsLeft > 0, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(40.dp)) { Text("+") }
                }
            }

            Spacer(Modifier.height(8.dp))
            Divider(color = textColor.copy(alpha = 0.2f))
            Spacer(Modifier.height(8.dp))

            // Интеллект
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("🧠 Интеллект: $intellect", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Button(onClick = { if (intellect > 0) intellect-- }, enabled = intellect > 0, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(40.dp)) { Text("-") }
                    Button(onClick = { intellect = 0 }, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) { Text("0") }
                    Button(onClick = { if (pointsLeft > 0) intellect++ }, enabled = pointsLeft > 0, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(40.dp)) { Text("+") }
                }
            }
        }
    }
}

@Composable
fun Part2Search(cardColor: Color, textColor: Color) {
    var query by rememberSaveable { mutableStateOf("") }
    val options = listOf("Маг", "Рыцарь", "Целитель", "Броненосец", "Убийца")
    Card(colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("2. Класс персонажа", fontWeight = FontWeight.Bold, color = textColor)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { gameClass ->
                    FilterChip(selected = query == gameClass, onClick = { query = if (query == gameClass) "" else gameClass }, label = { Text(gameClass) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary, selectedLabelColor = Color.White))
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (query.isEmpty()) "Не выбран" else "Выбрано: $query", color = textColor, fontSize = 14.sp)
                TextButton(onClick = { query = "" }, enabled = query.isNotEmpty()) { Text("Сброс") }
            }
        }
    }
}

@Composable
fun Part3ExpandableList(cardColor: Color, textColor: Color) {
    val games = listOf(
        "Битва драконов" to "Сразитесь с древним драконом в пещере.",
        "Космический рейд" to "Уничтожьте флот пришельцев на орбите Марса.",
        "Остров сокровищ" to "Найдите потерянное золото пиратов."
    )
    Column {
        Text("3. Раскрывающиеся карточки", fontWeight = FontWeight.Bold, color = textColor)
        Spacer(Modifier.height(8.dp))
        games.forEach { (title, desc) ->
            ExpandableCard(title, desc, cardColor, textColor)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun ExpandableCard(title: String, description: String, cardColor: Color, textColor: Color) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().animatedClickableCard { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontWeight = FontWeight.SemiBold, color = textColor)
                Text(if (expanded) "▲" else "▼", color = textColor)
            }
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Text(description, fontSize = 14.sp, color = textColor.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun Part4FormAndCheckboxes(cardColor: Color, textColor: Color) {
    Card(colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("4. Экипировка", fontWeight = FontWeight.Bold, color = textColor)
            Spacer(Modifier.height(8.dp))
            val options = listOf("Шлем", "Броня", "Меч")
            val selectedOptions = remember { mutableStateListOf<String>() }
            options.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().animatedClickableCard {
                        if (selectedOptions.contains(option)) selectedOptions.remove(option) else selectedOptions.add(option)
                    }
                ) {
                    Checkbox(checked = selectedOptions.contains(option), onCheckedChange = null)
                    Text(option, color = textColor)
                }
            }
            Text("Выбрано: ${selectedOptions.size}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}