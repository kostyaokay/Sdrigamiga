package com.example.kostyaonlinegames

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kostyaonlinegames.data.*

@Composable
fun Lab11NotesScreen(onBack: () -> Unit, vm: NotesViewModel) {
    val context = LocalContext.current
    val state by vm.state.collectAsStateWithLifecycle()

    var editingNote by remember { mutableStateOf<Note?>(null) }
    var noteToDelete by remember { mutableStateOf<Long?>(null) }

    // Экспорт
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            val currentNotes = (state as? NotesUiState.Success)?.notes ?: emptyList()
            context.contentResolver.openOutputStream(it)?.bufferedWriter()?.use { writer ->
                writer.write(NotesJson.encode(currentNotes))
            }
        }
    }

    // Импорт
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val text = context.contentResolver.openInputStream(it)?.bufferedReader()?.use { it.readText() }
            if (text != null) {
                try {
                    val importedNotes = NotesJson.decode(text)
                    importedNotes.forEach { note -> vm.saveNote(note.copy(id = System.currentTimeMillis())) }
                } catch (e: Exception) {
                    // Ошибка парсинга
                }
            }
        }
    }

    if (editingNote != null) {
        NoteEditScreen(
            note = editingNote!!,
            onSave = { note ->
                vm.saveNote(note)
                editingNote = null
            },
            onBack = { editingNote = null }
        )
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { editingNote = Note(title = "Новая запись") },
                    containerColor = Color(0xFF5E35B1)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить запись", tint = Color.White)
                }
            },
            containerColor = Color(0xFFF4F1FB)
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
                AnimatedActionButton("← На главную", Color(0xFF5E35B1), Modifier.fillMaxWidth(), onBack)
                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Дневник приключений", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Row {
                        IconButton(onClick = { importLauncher.launch(arrayOf("application/json")) }) {
                            Text("📥", fontSize = 20.sp)
                        }
                        IconButton(onClick = { exportLauncher.launch("adventure-log.json") }) {
                            Text("📤", fontSize = 20.sp)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                when (val s = state) {
                    is NotesUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    is NotesUiState.Error -> {
                        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text("Ошибка!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                            Text(s.message, color = Color.Gray)
                            Spacer(Modifier.height(16.dp))
                            AnimatedActionButton("Повторить", Color(0xFF5E35B1), Modifier.width(200.dp), vm::load)
                        }
                    }
                    is NotesUiState.Success -> {
                        if (s.notes.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Дневник пуст. Нажмите + чтобы записать подвиг.", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(s.notes, key = { it.id }) { note ->
                                    NoteCard(
                                        note = note,
                                        onClick = { editingNote = note },
                                        onDeleteClick = { noteToDelete = note.id }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("Удалить запись?") },
            text = { Text("Вы уверены, что хотите стереть этот подвиг из истории?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteNote(noteToDelete!!)
                    noteToDelete = null
                }) { Text("Удалить", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) { Text("Отмена") }
            }
        )
    }
}

@Composable
fun NoteCard(note: Note, onClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().animatedClickableCard { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(Color(0xFFEDE7F6), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("📜", fontSize = 20.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    note.title.ifBlank { "Запись без названия" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A2E)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    note.body,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = Color.Red)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(note: Note, onSave: (Note) -> Unit, onBack: () -> Unit) {
    var title by remember { mutableStateOf(note.title) }
    var body by remember { mutableStateOf(note.body) }

    DisposableEffect(Unit) {
        onDispose {
            if (title.isNotBlank() || body.isNotBlank()) {
                onSave(note.copy(title = title, body = body, updatedAt = System.currentTimeMillis()))
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F1FB)).padding(16.dp)) {
        AnimatedActionButton("← Сохранить и выйти", Color(0xFF5E35B1), Modifier.fillMaxWidth(), onBack)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Название приключения") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            label = { Text("Описание подвига") },
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
    }
}