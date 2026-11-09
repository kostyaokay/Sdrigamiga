package com.example.kostyaonlinegames.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PostsUiState {
    data object Loading : PostsUiState
    data class Success(val posts: List<Post>) : PostsUiState
    data class Error(val message: String) : PostsUiState
}

class PostsViewModel : ViewModel() {
    private val repository = PostsRepository(RetrofitClient.api)

    private val _state = MutableStateFlow<PostsUiState>(PostsUiState.Loading)
    val state: StateFlow<PostsUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = PostsUiState.Loading
        viewModelScope.launch {
            repository.getPosts()
                .onSuccess { _state.value = PostsUiState.Success(it) }
                .onFailure { _state.value = PostsUiState.Error(it.message ?: "Неизвестная ошибка сети") }
        }
    }
}