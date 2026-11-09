package com.example.kostyaonlinegames

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kostyaonlinegames.data.Note
import com.example.kostyaonlinegames.data.NotesViewModel

data class ShopItem(val id: String, val title: String, val emoji: String, val price: Int)

@Composable
fun GameShopScreen(onBack: () -> Unit, notesViewModel: NotesViewModel) {
    val items = listOf(
        ShopItem("1", "Меч дракона", "🛡️", 1500), ShopItem("2", "Зелье силы", "🧪", 300),
        ShopItem("3", "Кольцо маны", "💍", 2200), ShopItem("4", "Свиток огня", "📜", 800),
        ShopItem("5", "Лук ветра", "🏹", 1200), ShopItem("6", "Щит титана", "🛡", 1900)
    )

    // Состояние корзины: список выбранных товаров
    val cartItems = remember { mutableStateListOf<ShopItem>() }
    val totalSum = cartItems.sumOf { it.price }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
        Text("🛒 Магазин", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxSize().weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Левая колонка (Товары)
            LazyColumn(modifier = Modifier.fillMaxHeight().weight(0.7f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(items) { item ->
                    ShopItemCard(item, onAddToCart = {
                        // Добавляем в корзину (если уже есть - не дублируем)
                        if (!cartItems.contains(item)) {
                            cartItems.add(item)
                        }
                    })
                }
            }

            // Правая колонка (Корзина)
            Column(modifier = Modifier.fillMaxHeight().weight(0.35f).background(Color(0xFFF4F1FB), RoundedCornerShape(14.dp)).padding(12.dp)) {
                Text("Корзина", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                Spacer(Modifier.height(8.dp))

                // Список выбранных товаров
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(cartItems) { cartItem ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(cartItem.title, fontSize = 12.sp, color = Color(0xFF1A1A2E))
                            Text("${cartItem.price}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }

                Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                Text("Итого: $totalSum ⭐", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                Spacer(Modifier.height(8.dp))

                // Кнопка оформления заказа
                Button(
                    onClick = {
                        if (cartItems.isNotEmpty()) {
                            // Формируем текст для Дневника
                            val itemsList = cartItems.joinToString(", ") { "${it.emoji} ${it.title}" }
                            notesViewModel.saveNote(
                                Note(
                                    title = "Заказ в магазине оформлен",
                                    body = "Вы приобрели следующие предметы: $itemsList.\nОбщая сумма: $totalSum золота."
                                )
                            )
                            cartItems.clear() // Очищаем корзину
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = cartItems.isNotEmpty()
                ) {
                    Text("Оформить заказ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("← Назад в лобби", fontSize = 14.sp, textAlign = TextAlign.Center) }
    }
}

@Composable
private fun ShopItemCard(item: ShopItem, onAddToCart: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).background(Color(0xFFEDE7F6), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Text(item.emoji, fontSize = 28.sp) }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                Text("${item.price} ⭐", fontSize = 13.sp, color = Color(0xFF757575))
            }
            Button(
                onClick = onAddToCart,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("В корзину", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}