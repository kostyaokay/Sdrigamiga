package com.example.kostyaonlinegames

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Lab7NetworkScreen(
    viewModel: Lab7ViewModel = viewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            if (uiState is FriendsUiState.Success || uiState is FriendsUiState.Refreshing) {
                FloatingActionButton(
                    onClick = { viewModel.refresh() },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Обновить", tint = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F1FB))
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("← На главную", color = Color(0xFF1A1A2E), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Text("Найти сокомандника поблизости", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
            Spacer(Modifier.height(16.dp))

            when (val state = uiState) {
                is FriendsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is FriendsUiState.Success -> {
                    FriendsList(state.friends)
                }

                is FriendsUiState.Refreshing -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        FriendsList(state.friends)
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color(0x80FFFFFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(shape = RoundedCornerShape(16.dp)) {
                                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator()
                                    Spacer(Modifier.height(8.dp))
                                    Text("Поиск друзей...")
                                }
                            }
                        }
                    }
                }

                is FriendsUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val emoji = when (state.errorType) {
                            "network" -> "📡"
                            "timeout" -> "⏳"
                            else -> "💥"
                        }
                        Text(emoji, fontSize = 64.sp)
                        Spacer(Modifier.height(16.dp))
                        Text("Ошибка!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        Text(state.message, fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { viewModel.retry() }, modifier = Modifier.fillMaxWidth(0.6f).height(50.dp)) {
                            Text("Повторить запрос")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FriendsList(friends: List<Friend>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(friends) { friend ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Аватарка (просто цветной кружок)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE7F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(friend.nickname.first().toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5E35B1))
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(friend.nickname, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E), fontSize = 16.sp)
                            Text("Ур. ${friend.level}", fontSize = 12.sp, color = Color.Gray)
                        }

                        Spacer(Modifier.height(4.dp))

                        // Статус друга
                        Text(
                            text = friend.status,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(friend.statusColor)
                        )
                    }
                }
            }
        }
    }
}