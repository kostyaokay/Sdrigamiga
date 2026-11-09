package com.example.kostyaonlinegames.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class NotesFileRepository(
    private val filesDir: File,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val file = File(filesDir, "notes.json")

    // Предзаполненные записи для первого запуска
    private fun getInitialNotes(): List<Note> {
        val now = System.currentTimeMillis()
        return listOf(
            Note(
                id = 1,
                title = "Победа над Древним Драконом",
                body = "Тактика: использовал ледяные стрелы и зелья сопротивления огню. Дракон повержен, лут собран: Чешуя дракона (x5), Коготь (x2). Опыт: +5000.",
                updatedAt = now - 86400000 // Вчера
            ),
            Note(
                id = 2,
                title = "Секретный квест в Тёмном Лесу",
                body = "Нашел скрытую тропу за водопадом. Внутри пещеры — сундук с легендарным мечом. Осторожно: ловушки с ядом!",
                updatedAt = now - 172800000 // Позавчера
            ),
            Note(
                id = 3,
                title = "Список покупок в магазине",
                body = "1. Зелье лечения (x10) - 500 золота\n2. Свиток телепортации - 1000 золота\n3. Новая броня для рейда - 3000 золота",
                updatedAt = now - 259200000 // 3 дня назад
            )
        )
    }

    suspend fun loadNotes(): Result<List<Note>> = withContext(ioDispatcher) {
        runCatching {
            if (file.exists()) {
                NotesJson.decode(file.readText())
            } else {
                // Если файла нет, создаем предзаполненные заметки и возвращаем их
                val initial = getInitialNotes()
                // Сохраняем их в файл сразу
                val tmp = File(filesDir, "notes.json.tmp")
                tmp.writeText(NotesJson.encode(initial))
                if (file.exists()) file.delete()
                tmp.renameTo(file)

                initial
            }
        }
    }

    suspend fun saveNotes(notes: List<Note>): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            // Атомарная запись
            val tmp = File(filesDir, "notes.json.tmp")
            tmp.writeText(NotesJson.encode(notes))
            if (file.exists()) file.delete()
            tmp.renameTo(file)
            Unit
        }
    }
}