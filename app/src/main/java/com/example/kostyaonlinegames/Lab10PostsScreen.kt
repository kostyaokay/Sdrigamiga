package com.example.kostyaonlinegames

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kostyaonlinegames.data.PostsUiState
import com.example.kostyaonlinegames.data.PostsViewModel
import com.example.kostyaonlinegames.data.Post

@Composable
fun Lab10PostsScreen(onBack: () -> Unit, vm: PostsViewModel = viewModel()) {
    var selectedPost by remember { mutableStateOf<Post?>(null) }

    if (selectedPost == null) {
        val state by vm.state.collectAsStateWithLifecycle()
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F1FB)).padding(16.dp)) {
            AnimatedActionButton("← На главную", Color(0xFF5E35B1), Modifier.fillMaxWidth(), onBack)
            Spacer(Modifier.height(16.dp))
            Text("События (Сеть)", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
            Spacer(Modifier.height(16.dp))

            when (val s = state) {
                is PostsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is PostsUiState.Error -> {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text("📡 Ошибка!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        Spacer(Modifier.height(8.dp))
                        Text(s.message, color = Color.Gray)
                        Spacer(Modifier.height(24.dp))
                        AnimatedActionButton("Повторить", Color(0xFF5E35B1), Modifier.width(200.dp), vm::load)
                    }
                }
                is PostsUiState.Success -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(s.posts) { post ->
                            // Добавляем AnimatedVisibility для плавного появления карточек
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + expandVertically()
                            ) {
                                PostCard(post, onDetailsClick = { selectedPost = post })
                            }
                        }
                    }
                }
            }
        }
    } else {
        PostDetailScreen(post = selectedPost!!, onBack = { selectedPost = null })
    }
}

@Composable
fun PostCard(post: Post, onDetailsClick: () -> Unit) {
    // Применяем наш модификатор для пружинящего нажатия на всю карточку
    Card(
        modifier = Modifier.fillMaxWidth().animatedClickableCard { onDetailsClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(post.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A2E))
            Spacer(Modifier.height(8.dp))
            Text(
                text = post.body,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(12.dp))

            // Кнопка Подробнее тоже получает анимацию
            TextButton(
                onClick = onDetailsClick,
                modifier = Modifier
                    .align(Alignment.End)
                    .animatedClickableCard { onDetailsClick() }
            ) {
                Text("Подробнее →", color = Color(0xFF5E35B1), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PostDetailScreen(post: Post, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F1FB)).padding(16.dp)) {
        AnimatedActionButton("← Назад к списку", Color(0xFF5E35B1), Modifier.fillMaxWidth(), onBack)
        Spacer(Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth().animatedClickableCard { }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(post.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                Spacer(Modifier.height(16.dp))
                Text(post.body, fontSize = 16.sp, color = Color.DarkGray, lineHeight = 24.sp)
            }
        }
    }
}