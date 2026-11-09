package com.example.kostyaonlinegames

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

// Модель данных друга
data class Friend(
    val id: Int,
    val nickname: String,
    val level: Int,
    val status: String,
    val statusColor: Long
)

// Состояния экрана
sealed interface FriendsUiState {
    data object Loading : FriendsUiState
    data class Success(val friends: List<Friend>) : FriendsUiState
    data class Error(val message: String, val errorType: String) : FriendsUiState
    data class Refreshing(val friends: List<Friend>) : FriendsUiState
}

class Lab7ViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<FriendsUiState>(FriendsUiState.Loading)
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    private var isRequestActive = false
    private var cachedFriends: List<Friend> = emptyList()

    private val nicknames = listOf(
        "Kostyan", "DragonSlayer", "ShadowKnight", "CyberWolf",
        "MageMaster", "DarkPhoenix", "SniperPro", "IronFist",
        "GhostRider", "ThunderGod"
    )

    init {
        loadFriends()
    }

    fun loadFriends(isRefresh: Boolean = false) {
        if (isRequestActive) return
        isRequestActive = true

        viewModelScope.launch {
            if (isRefresh && cachedFriends.isNotEmpty()) {
                _uiState.value = FriendsUiState.Refreshing(cachedFriends)
            } else {
                _uiState.value = FriendsUiState.Loading
            }

            delay(1000) // Имитация сети

            val random = Random.nextInt(100)
            if (random < 20) { // 20% шанс ошибки
                val errorMsg = when (Random.nextInt(3)) {
                    0 -> "Нет подключения к интернету."
                    1 -> "Сервер не отвечает."
                    else -> "Ошибка синхронизации друзей (500)."
                }
                val errorType = if (random < 10) "network" else "server"
                _uiState.value = FriendsUiState.Error(errorMsg, errorType)
            } else {
                val newFriends = (1..10).map { index ->
                    val name = nicknames.shuffled().first() + " #$index"
                    val level = Random.nextInt(5, 60)

                    // ИСПОЛЬЗУЕМ Pair ВМЕСТО Triple (проще и надежнее)
                    val statuses: List<Pair<String, Long>> = listOf(
                        "В главном меню" to 0xFF4CAF50,       // Зеленый
                        "В игре: Битва драконов" to 0xFF2196F3, // Синий
                        "Не в сети" to 0xFF9E9E9E,            // Серый
                        "Нет на месте" to 0xFFFF9800          // Оранжевый
                    )
                    val randomStatus = statuses.random()

                    Friend(
                        id = index,
                        nickname = name,
                        level = level,
                        status = randomStatus.first,
                        statusColor = randomStatus.second
                    )
                }
                cachedFriends = newFriends
                _uiState.value = FriendsUiState.Success(newFriends)
            }
            isRequestActive = false
        }
    }

    fun retry() = loadFriends(isRefresh = false)
    fun refresh() = loadFriends(isRefresh = true)
}