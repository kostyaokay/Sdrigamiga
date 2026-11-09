package com.example.kostyaonlinegames

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kostyaonlinegames.data.*
import com.example.kostyaonlinegames.ui.theme.KostyaOnlineGamesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KostyaOnlineGamesTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val context = LocalContext.current

                    // Инициализируем ViewModel и Repository на уровне всего приложения
                    val notesRepository = remember { NotesFileRepository(context.filesDir) }
                    val notesViewModel: NotesViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                @Suppress("UNCHECKED_CAST")
                                return NotesViewModel(notesRepository) as T
                            }
                        }
                    )

                    var screen by remember { mutableStateOf("lobby") }
                    var playerClass by rememberSaveable { mutableStateOf("Броненосец") }
                    var playerWeapon by rememberSaveable { mutableStateOf("Щит и булава") }

                    when (screen) {
                        "lobby" -> GameLobbyScreen(
                            playerClass, playerWeapon,
                            notesViewModel = notesViewModel,
                            onOpenProfile = { screen = "profile" },
                            onOpenShop = { screen = "shop" },
                            onOpenLab4 = { screen = "lab4" }, onOpenLab5 = { screen = "lab5" },
                            onOpenLab6 = { screen = "lab6" }, onOpenLab7 = { screen = "lab7" },
                            onOpenLab8 = { screen = "lab8" }, onOpenLab9 = { screen = "lab9" },
                            onOpenLab10 = { screen = "lab10" },
                            onOpenLab11 = { screen = "lab11" }
                        )
                        "profile" -> GameProfileScreen(onBack = { screen = "lobby" })

                        // ПЕРЕДАЕМ VM В МАГАЗИН
                        "shop" -> GameShopScreen(onBack = { screen = "lobby" }, notesViewModel = notesViewModel)

                        "lab4" -> Lab4StateScreen(onBack = { screen = "lobby" })
                        "lab5" -> Lab5ListScreen(onBack = { screen = "lobby" })
                        "lab6" -> Lab6NavScreen(onExitToLobby = { screen = "lobby" })
                        "lab7" -> Lab7NetworkScreen(onBack = { screen = "lobby" })

                        // ЛОГИКА СМЕНЫ ПЕРСОНАЖА
                        "lab8" -> Lab8Screen(
                            currentClass = playerClass,
                            currentWeapon = playerWeapon,
                            onSaveProfile = { newClass, newWeapon ->
                                // Если класс или оружие изменились - пишем в дневник
                                if (newClass != playerClass || newWeapon != playerWeapon) {
                                    notesViewModel.saveNote(
                                        Note(
                                            title = "Смена экипировки героя",
                                            body = "Персонаж изменил класс с «$playerClass» на «$newClass» и оружие с «$playerWeapon» на «$newWeapon»."
                                        )
                                    )
                                }
                                playerClass = newClass
                                playerWeapon = newWeapon
                                screen = "lobby"
                            },
                            onBack = { screen = "lobby" }
                        )

                        "lab9" -> Lab9TaskScreen(onBack = { screen = "lobby" })
                        "lab10" -> Lab10PostsScreen(onBack = { screen = "lobby" })
                        "lab11" -> Lab11NotesScreen(onBack = { screen = "lobby" }, notesViewModel)
                    }
                }
            }
        }
    }
}

data class GameRoom(val id: String, val title: String, val players: Int, val maxPlayers: Int, val emoji: String)

@Composable
fun GameLobbyScreen(
    playerClass: String, playerWeapon: String,
    notesViewModel: NotesViewModel,
    onOpenProfile: () -> Unit = {}, onOpenShop: () -> Unit = {},
    onOpenLab4: () -> Unit = {}, onOpenLab5: () -> Unit = {}, onOpenLab6: () -> Unit = {},
    onOpenLab7: () -> Unit = {}, onOpenLab8: () -> Unit = {}, onOpenLab9: () -> Unit = {},
    onOpenLab10: () -> Unit = {}, onOpenLab11: () -> Unit = {}
) {
    val context = LocalContext.current
    var sortByPlayers by remember { mutableStateOf(false) }
    val rooms = remember(sortByPlayers) {
        val base = listOf(
            GameRoom("1", "Битва драконов", 3, 4, "🐉"), GameRoom("2", "Космический рейд", 2, 4, "🚀"),
            GameRoom("3", "Остров сокровищ", 1, 6, "🏝️"), GameRoom("4", "Гонки молний", 5, 8, "⚡"),
            GameRoom("5", "Подводный мир", 4, 4, "🐬")
        )
        if (sortByPlayers) base.sortedByDescending { it.players } else base
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().animatedClickableCard { onOpenProfile() }.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFEDE7F6)), contentAlignment = Alignment.Center) { Text("🎮", fontSize = 28.sp) }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Привет, Kostyan!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("$playerClass · $playerWeapon", fontSize = 14.sp, color = Color.Gray)
            }
            Text("›", fontSize = 26.sp, color = Color.Gray)
        }

        Spacer(Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth().shadow(6.dp, RoundedCornerShape(18.dp)), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF5E35B1))) {
            Row(modifier = Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("🌐", "1247", "Онлайн"); StatItem("🏆", "42", "Побед"); StatItem("⭐", "Ур. 8", "Уровень")
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AnimatedActionButton("🛒 Магазин", Color(0xFF5E35B1), Modifier.weight(1f), onOpenShop)
            OutlinedButton(onClick = { Toast.makeText(context, "Создаём лобби...", Toast.LENGTH_SHORT).show() }, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(16.dp)) { Text("Создать", fontWeight = FontWeight.Bold) }
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LazyColumn(modifier = Modifier.weight(1.4f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item { AnimatedActionButton("Прокачка", Color(0xFF00897B), Modifier.fillMaxWidth(), onOpenLab4) }
                item { AnimatedActionButton("Ивенты", Color(0xFFF57C00), Modifier.fillMaxWidth(), onOpenLab5) }
                item { AnimatedActionButton("Бестиарий", Color(0xFF5E35B1), Modifier.fillMaxWidth(), onOpenLab6) }
                item { AnimatedActionButton("Команда", Color(0xFFD81B60), Modifier.fillMaxWidth(), onOpenLab7) }
                item { AnimatedActionButton("Герой", Color(0xFF00897B), Modifier.fillMaxWidth(), onOpenLab8) }
                item { AnimatedActionButton("Квесты", Color(0xFF607D8B), Modifier.fillMaxWidth(), onOpenLab9) }
                item { AnimatedActionButton("События (Сеть)", Color(0xFF1976D2), Modifier.fillMaxWidth(), onOpenLab10) }
                item { AnimatedActionButton("Дневник", Color(0xFF8E24AA), Modifier.fillMaxWidth(), onOpenLab11) }
            }

            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Игры", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { sortByPlayers = !sortByPlayers }, modifier = Modifier.size(32.dp)) { Text("🔀", fontSize = 18.sp) }
                }
                Spacer(Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(rooms, key = { it.id }) { room ->
                        Card(
                            modifier = Modifier.fillMaxWidth().animatedClickableCard {
                                Toast.makeText(context, "Входим в ${room.title}...", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) { Text(room.emoji, fontSize = 20.sp); Spacer(Modifier.width(8.dp)); Text(room.title, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                                Spacer(Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("${room.players}/${room.maxPlayers}", fontSize = 11.sp, color = Color.Gray)
                                    Text(if (room.players >= room.maxPlayers) "Полная" else "Войти", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5E35B1))
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Made in 21 auditory BY Kostyan 😎", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable fun StatItem(emoji: String, value: String, label: String) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(emoji, fontSize = 22.sp); Spacer(Modifier.height(4.dp)); Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold); Text(label, color = Color(0xFFD1C4E9), fontSize = 12.sp) } }