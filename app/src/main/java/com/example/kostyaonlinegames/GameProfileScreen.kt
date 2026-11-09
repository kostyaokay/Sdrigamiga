package com.example.kostyaonlinegames

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kostyaonlinegames.ui.theme.KostyaOnlineGamesTheme

@Composable
fun GameProfileScreen(onBack: () -> Unit = {}) {
    var isSubscribed by remember { mutableStateOf(false) }
    var followersCount by remember { mutableStateOf(128) }
    var showMessageDialog by remember { mutableStateOf(false) }
    val messages = remember { mutableStateListOf<String>() }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isWide = maxWidth > 600.dp

        if (isWide) {
            WideProfileLayout(
                onBack = onBack, isSubscribed = isSubscribed, followers = followersCount, messages = messages,
                onToggleSubscribe = { isSubscribed = !isSubscribed; followersCount += if (isSubscribed) 1 else -1 },
                onOpenMessage = { showMessageDialog = true }
            )
        } else {
            NarrowProfileLayout(
                onBack = onBack, isSubscribed = isSubscribed, followers = followersCount, messages = messages,
                onToggleSubscribe = { isSubscribed = !isSubscribed; followersCount += if (isSubscribed) 1 else -1 },
                onOpenMessage = { showMessageDialog = true }
            )
        }
    }

    if (showMessageDialog) {
        MessageDialog(onDismiss = { showMessageDialog = false }, onSend = { messages.add(it) })
    }
}

@Composable
private fun NarrowProfileLayout(onBack: () -> Unit, isSubscribed: Boolean, followers: Int, messages: List<String>, onToggleSubscribe: () -> Unit, onOpenMessage: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(24.dp)); ProfileAvatar(); Spacer(Modifier.height(12.dp)); ProfileNameAndStatus()
        Spacer(Modifier.height(24.dp)); FollowersRow(followers = followers); Spacer(Modifier.height(24.dp))
        SubscribeButtons(isSubscribed, onToggleSubscribe, onOpenMessage)
        Spacer(Modifier.height(16.dp)); MessagesFeed(messages = messages)
        Spacer(Modifier.weight(1f)); BottomBar(); Spacer(Modifier.height(8.dp))
        TextButton(onClick = onBack) { Text("← Назад в лобби", fontSize = 14.sp) }
    }
}

@Composable
private fun WideProfileLayout(onBack: () -> Unit, isSubscribed: Boolean, followers: Int, messages: List<String>, onToggleSubscribe: () -> Unit, onOpenMessage: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(32.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            ProfileAvatar(); Spacer(Modifier.width(20.dp)); ProfileNameAndStatus()
        }
        Spacer(Modifier.height(32.dp)); FollowersRow(followers = followers); Spacer(Modifier.height(24.dp))
        SubscribeButtons(isSubscribed, onToggleSubscribe, onOpenMessage)
        Spacer(Modifier.height(24.dp)); MessagesFeed(messages = messages)
        Spacer(Modifier.weight(1f)); BottomBar(); Spacer(Modifier.height(16.dp))
        TextButton(onClick = onBack) { Text("← Назад в лобби", fontSize = 14.sp) }
    }
}

@Composable
private fun ProfileAvatar() { Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFFEDE7F6)), contentAlignment = Alignment.Center) { Text("🎮", fontSize = 32.sp) } }

@Composable
private fun ProfileNameAndStatus() { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("Kostyan", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground); Text("Онлайн · Уровень 8", fontSize = 14.sp, color = Color(0xFF757575)) } }

@Composable
private fun FollowersRow(followers: Int) { Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { CounterBlock("Подписчики", followers.toString()); Spacer(Modifier.weight(1f)); CounterBlock("Подписки", "42") } }

@Composable
private fun CounterBlock(label: String, value: String) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary); Text(label, fontSize = 12.sp, color = Color(0xFF757575)) } }

@Composable
private fun SubscribeButtons(isSubscribed: Boolean, onToggleSubscribe: () -> Unit, onOpenMessage: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = onToggleSubscribe, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = if (isSubscribed) Color(0xFF9E9E9E) else MaterialTheme.colorScheme.primary)) { Text(if (isSubscribed) "Отписаться" else "Подписаться", fontSize = 15.sp, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onOpenMessage, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp), shape = RoundedCornerShape(14.dp)) { Text("Сообщение", fontSize = 15.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun MessagesFeed(messages: List<String>) {
    if (messages.isEmpty()) return
    Column(modifier = Modifier.fillMaxWidth().heightIn(max = 180.dp).background(Color(0xFFF4F1FB), RoundedCornerShape(14.dp)).padding(12.dp)) {
        Text("📨 Отправленные сообщения", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5E35B1)); Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) { items(messages) { msg -> Box(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(10.dp)).padding(10.dp)) { Text(text = msg, fontSize = 13.sp, color = Color(0xFF1A1A2E)) } } }
    }
}

@Composable
private fun BottomBar() { Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) { BottomIcon("🏠"); BottomIcon("👥"); BottomIcon("⚙️") } }

@Composable
private fun BottomIcon(emoji: String) { Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFF4F1FB)), contentAlignment = Alignment.Center) { Text(emoji, fontSize = 22.sp) } }

@Composable
private fun MessageDialog(onDismiss: () -> Unit, onSend: (String) -> Unit) {
    val context = LocalContext.current
    var messageText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Сообщение для Kostyan") },
        text = {
            // Здесь разрешен ввод любых символов (кириллица работает)
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Введите текст...") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (messageText.isNotBlank()) {
                    onSend(messageText.trim())
                    Toast.makeText(context, "Сообщение отправлено", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Введите сообщение", Toast.LENGTH_SHORT).show()
                }
                onDismiss()
            }) { Text("Отправить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720, name = "Narrow")
@Composable
fun GameProfileNarrowPreview() { KostyaOnlineGamesTheme { GameProfileScreen() } }

@Preview(showBackground = true, widthDp = 800, heightDp = 600, name = "Wide")
@Composable
fun GameProfileWidePreview() { KostyaOnlineGamesTheme { GameProfileScreen() } }