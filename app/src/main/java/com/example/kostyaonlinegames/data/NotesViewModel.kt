package com.example.kostyaonlinegames.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed interface NotesUiState {
    data object Loading : NotesUiState
    data class Success(val notes: List<Note>) : NotesUiState
    data class Error(val message: String) : NotesUiState
}

class NotesViewModel(private val repository: NotesFileRepository) : ViewModel() {
    private val _state = MutableStateFlow<NotesUiState>(NotesUiState.Loading)
    val state: StateFlow<NotesUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = NotesUiState.Loading
            repository.loadNotes()
                .onSuccess { _state.value = NotesUiState.Success(it) }
                .onFailure { _state.value = NotesUiState.Error(it.message ?: "Ошибка чтения") }
        }
    }

    fun saveNote(note: Note) {
        viewModelScope.launch {
            val currentList = (_state.value as? NotesUiState.Success)?.notes ?: emptyList()
            // Если заметка с таким ID есть - заменяем, иначе добавляем
            val newList = currentList.toMutableList()
            val index = newList.indexOfFirst { it.id == note.id }
            if (index != -1) newList[index] = note else newList.add(note)

            // Сортируем по дате (свежие сверху)
            val sortedList = newList.sortedByDescending { it.updatedAt }

            repository.saveNotes(sortedList).onSuccess {
                _state.value = NotesUiState.Success(sortedList)
            }
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            val currentList = (_state.value as? NotesUiState.Success)?.notes ?: return@launch
            val newList = currentList.filterNot { it.id == id }
            repository.saveNotes(newList).onSuccess {
                _state.value = NotesUiState.Success(newList)
            }
        }
    }
}