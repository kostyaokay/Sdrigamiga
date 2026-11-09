package com.example.kostyaonlinegames.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object NotesJson {
    val format = Json { prettyPrint = true; ignoreUnknownKeys = true }

    fun encode(notes: List<Note>): String = format.encodeToString(notes)
    fun decode(text: String): List<Note> = format.decodeFromString(text)
}