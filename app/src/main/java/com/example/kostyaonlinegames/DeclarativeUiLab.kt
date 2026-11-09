package com.example.kostyaonlinegames

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kostyaonlinegames.ui.theme.KostyaOnlineGamesTheme

/**
 * Лабораторная работа 2. Декларативный UI.
 *
 * Демонстрация базовых composable-функций:
 *  - Text
 *  - Column
 *  - Row
 *  - Box
 *
 * И модификаторов:
 *  - padding
 *  - fillMaxWidth
 *  - align
 *  - clickable
 */

// ---------- Требование: "Text, Column, Row, Box" ----------

/** 1. Простой Text */
@Composable
fun SimpleText() {
    Text(text = "Я — composable Text")
}

/** 2. Column — вертикальный стек */
@Composable
fun SimpleColumn() {
    Column {
        Text("Верх")
        Text("Центр")
        Text("Низ")
    }
}

/** 3. Row — горизонтальный стек */
@Composable
fun SimpleRow() {
    Row {
        Text("Слева")
        Spacer(Modifier.width(8.dp))
        Text("Центр")
        Spacer(Modifier.width(8.dp))
        Text("Справа")
    }
}

/** 4. Box — контейнер с наложением (можно центрировать содержимое) */
@Composable
fun SimpleBox() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color(0xFFEDE7F6), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("Box", fontWeight = FontWeight.Bold)
    }
}

// ---------- Требование: "экран с заголовком, подзаголовком и кнопкой" ----------

@Composable
fun DeclarativeUiLabScreen() {
    // Состояние — счётчик нажатий
    var clickCount by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 🟣 Заголовок
        Text(
            text = "Декларативный UI",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        // ⚪ Подзаголовок
        Text(
            text = "UI = функция от состояния (State → UI)",
            fontSize = 15.sp,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(28.dp))

        // 🔲 Демонстрация Box
        SimpleBox()

        Spacer(Modifier.height(24.dp))

        // 🧩 Демонстрация Row с clickable
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            DemoChip("Text", onTap = { clickCount++ })
            Spacer(Modifier.width(10.dp))
            DemoChip("Column", onTap = { clickCount++ })
            Spacer(Modifier.width(10.dp))
            DemoChip("Row", onTap = { clickCount++ })
        }

        Spacer(Modifier.height(28.dp))

        // 🔘 Кнопка — основной элемент экрана
        Button(
            onClick = { clickCount++ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Нажато: $clickCount",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(Modifier.height(40.dp))

        // 📝 Подпись
        Text(
            text = "Made in KZ BY Kostyan 😎",
            fontSize = 13.sp,
            color = Color(0xFF9E9E9E),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Маленькая «плашка» с clickable — демонстрация модификатора clickable.
 */
@Composable
private fun DemoChip(text: String, onTap: () -> Unit) {
    Box(
        modifier = Modifier
            .background(Color(0xFFF4F1FB), RoundedCornerShape(10.dp))
            .clickable { onTap() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ---------- Preview для Android Studio ----------

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun DeclarativeUiLabPreview() {
    KostyaOnlineGamesTheme {
        DeclarativeUiLabScreen()
    }
}