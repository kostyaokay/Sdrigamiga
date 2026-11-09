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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

data class GameInfo(val id: Int, val title: String, val type: String, val isLocked: Boolean, val description: String)

object GameDatabase {
    val items = listOf(
        GameInfo(1, "🔥 Огненные пещеры", "Локация", false, "Раскалённые тоннели вулкана."),
        GameInfo(2, "🐉 Древний Дракон", "Босс", false, "Огромный ящер. Уязвимость: Ледяная магия."),
        GameInfo(3, "🌲 Тёмный Лес", "Локация", false, "Густой лес, где всегда ночь."),
        GameInfo(4, "🐺 Вожак Стаи", "Босс", false, "Огромный оборотень. Уязвимость: Серебро."),
        GameInfo(5, "🌋 Вулкан Бездны", "Локация", true, "Закрыто."),
        GameInfo(6, "👹 Владыка Бездны", "Босс", true, "Закрыто.")
    )
}

@Composable
fun Lab6NavScreen(onExitToLobby: () -> Unit) {
    val navController = rememberNavController()
    var isDarkMode by remember { mutableStateOf(false) }
    var showLockedMessage by remember { mutableStateOf<String?>(null) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(isDarkMode, { navController.navigate("list") }, { navController.navigate("settings") }, onExitToLobby) }
        composable("list") {
            ListScreen(isDarkMode, { item ->
                if (item.isLocked) showLockedMessage = "Заблокировано!" else navController.navigate("details/${item.id}")
            }, { navController.popBackStack() })
        }
        composable("details/{itemId}", arguments = listOf(navArgument("itemId") { type = NavType.IntType })) { entry ->
            DetailsScreen(entry.arguments?.getInt("itemId") ?: -1, isDarkMode, { navController.popBackStack() })
        }
        composable("settings") { SettingsScreen(isDarkMode, { isDarkMode = it }, { navController.popBackStack() }) }
    }

    if (showLockedMessage != null) {
        AlertDialog(onDismissRequest = { showLockedMessage = null }, title = { Text("Закрыто") }, text = { Text(showLockedMessage!!) }, confirmButton = { TextButton(onClick = { showLockedMessage = null }) { Text("ОК") } })
    }
}

@Composable
fun HomeScreen(isDarkMode: Boolean, onNavigateToList: () -> Unit, onNavigateToSettings: () -> Unit, onBackToLobby: () -> Unit) {
    val bgColor = if (isDarkMode) Color(0xFF1E1E24) else Color(0xFFF4F1FB)
    val textColor = if (isDarkMode) Color.White else Color(0xFF1A1A2E)
    Column(modifier = Modifier.fillMaxSize().background(bgColor).padding(24.dp)) {
        AnimatedActionButton("← Вернуться в меню", Color(0xFF5E35B1), Modifier.fillMaxWidth(), onBackToLobby)
        Spacer(Modifier.height(32.dp))
        Column(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("Справочник", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = textColor)
            Spacer(Modifier.height(32.dp))
            AnimatedActionButton("Открыть бестиарий", Color(0xFF5E35B1), Modifier.fillMaxWidth(), onNavigateToList)
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onNavigateToSettings, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(16.dp)) { Text("Настройки", color = textColor) }
        }
    }
}

@Composable
fun ListScreen(isDarkMode: Boolean, onItemClick: (GameInfo) -> Unit, onBack: () -> Unit) {
    val bgColor = if (isDarkMode) Color(0xFF1E1E24) else Color(0xFFF4F1FB)
    val textColor = if (isDarkMode) Color.White else Color(0xFF1A1A2E)
    val cardColor = if (isDarkMode) Color(0xFF2C2C35) else Color.White
    val lockedColor = if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFEEEEEE)

    Column(modifier = Modifier.fillMaxSize().background(bgColor).padding(16.dp)) {
        AnimatedActionButton("← Назад", Color(0xFF5E35B1), Modifier.fillMaxWidth(), onBack)
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(GameDatabase.items) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().animatedClickableCard { onItemClick(item) },
                    colors = CardDefaults.cardColors(containerColor = if (item.isLocked) lockedColor else cardColor)
                ) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(if (item.isLocked) "🔒 ${item.title}" else item.title, fontWeight = FontWeight.Bold, color = if (item.isLocked) Color.Gray else textColor)
                            Text(if (item.isLocked) "Закрыто" else if (item.type == "Босс") "⚔️ Босс" else "🗺️ Локация", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailsScreen(itemId: Int, isDarkMode: Boolean, onBack: () -> Unit) {
    val bgColor = if (isDarkMode) Color(0xFF1E1E24) else Color(0xFFF4F1FB)
    val textColor = if (isDarkMode) Color.White else Color(0xFF1A1A2E)
    val item = GameDatabase.items.find { it.id == itemId }

    Column(modifier = Modifier.fillMaxSize().background(bgColor).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        if (item != null) {
            Text(item.title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = textColor)
            Spacer(Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF2C2C35) else Color.White)) {
                Text(item.description, color = textColor, modifier = Modifier.padding(16.dp))
            }
            Spacer(Modifier.height(32.dp))
            AnimatedActionButton("Назад", Color(0xFF5E35B1), Modifier.fillMaxWidth(), onBack)
        } else {
            Text("Не найдено", color = Color.Red)
            Spacer(Modifier.height(16.dp))
            AnimatedActionButton("Назад", Color(0xFF5E35B1), Modifier.fillMaxWidth(), onBack)
        }
    }
}

@Composable
fun SettingsScreen(isDarkMode: Boolean, onToggleTheme: (Boolean) -> Unit, onBack: () -> Unit) {
    val bgColor = if (isDarkMode) Color(0xFF1E1E24) else Color(0xFFF4F1FB)
    val textColor = if (isDarkMode) Color.White else Color(0xFF1A1A2E)
    Column(modifier = Modifier.fillMaxSize().background(bgColor).padding(24.dp)) {
        AnimatedActionButton("← Назад", Color(0xFF5E35B1), Modifier.fillMaxWidth(), onBack)
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Тёмная тема", color = textColor, fontSize = 18.sp)
            Switch(checked = isDarkMode, onCheckedChange = onToggleTheme)
        }
    }
}