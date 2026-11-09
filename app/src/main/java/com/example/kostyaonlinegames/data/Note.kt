package com.example.kostyaonlinegames.data

import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: Long = System.currentTimeMillis(),
    val title: String = "",
    val body: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)