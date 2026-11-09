package com.example.kostyaonlinegames

import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val gameClasses = listOf("Маг", "Рыцарь", "Целитель", "Броненосец", "Убийца")
val gameWeapons = listOf("Посох огня", "Двуручный меч", "Святой жезл", "Щит и булава", "Кинжалы тьмы")

@Composable
fun Lab8Screen(currentClass: String, currentWeapon: String, onSaveProfile: (String, String) -> Unit, onBack: () -> Unit) {
    var themeMode by rememberSaveable { mutableStateOf("SYSTEM") }
    val isSystemDark = isSystemInDarkTheme()
    val useDarkTheme = when (themeMode) { "DARK" -> true; "LIGHT" -> false; else -> isSystemDark }

    MaterialTheme(colorScheme = if (useDarkTheme) darkColorScheme() else lightColorScheme()) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                AnimatedActionButton("← В меню", Color(0xFF5E35B1), Modifier.fillMaxWidth(), onBack)
                Spacer(Modifier.height(16.dp))
                CharacterScreenCompact(currentClass, currentWeapon, onSaveProfile, themeMode, { themeMode = it })
            }
        }
    }
}

@Composable
fun CharacterScreenCompact(initialClass: String, initialWeapon: String, onSaveProfile: (String, String) -> Unit, themeMode: String, onThemeChange: (String) -> Unit) {
    var editClass by remember { mutableStateOf(initialClass) }
    var editWeapon by remember { mutableStateOf(initialWeapon) }
    var isLoading by remember { mutableStateOf(false) }
    var showSavedMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val avatarColor = when(editClass) {
        "Маг" -> Color(0xFF2196F3); "Рыцарь" -> Color(0xFFF44336); "Целитель" -> Color(0xFF4CAF50); "Броненосец" -> Color(0xFF795548); "Убийца" -> Color(0xFF9C27B0); else -> MaterialTheme.colorScheme.primary
    }

    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Персонаж", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.fillMaxWidth())
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Тема: ", modifier = Modifier.weight(1f))
            FilterChip(selected = themeMode == "SYSTEM", onClick = { onThemeChange("SYSTEM") }, label = { Text("Система") })
            Spacer(Modifier.width(4.dp))
            FilterChip(selected = themeMode == "DARK", onClick = { onThemeChange("DARK") }, label = { Text("Тёмная") })
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Класс:", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                gameClasses.forEach { c -> FilterChip(selected = editClass == c, onClick = { editClass = c }, label = { Text(c) }) }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Оружие:", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                gameWeapons.forEach { w -> FilterChip(selected = editWeapon == w, onClick = { editWeapon = w }, label = { Text(w) }) }
            }
        }

        Card(modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {}.animatedClickableCard { }, shape = MaterialTheme.shapes.medium) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(56.dp).background(avatarColor.copy(alpha = 0.2f), RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
                    Text("K", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = avatarColor)
                }
                Spacer(Modifier.width(16.dp))
                Column { Text("Kostyan", style = MaterialTheme.typography.titleLarge); Text("$editClass · $editWeapon", style = MaterialTheme.typography.bodyMedium) }
            }
        }

        AnimatedActionButton("Сохранить изменения", Color(0xFF5E35B1), Modifier.fillMaxWidth(), {
            isLoading = true
            scope.launch {
                delay(1000); isLoading = false; onSaveProfile(editClass, editWeapon); showSavedMessage = true; delay(3000); showSavedMessage = false
            }
        })

        if (showSavedMessage) Text("Класс и оружие сохранены!", color = MaterialTheme.colorScheme.primary, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive })
    }
}