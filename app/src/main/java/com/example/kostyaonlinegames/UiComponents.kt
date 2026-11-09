package com.example.kostyaonlinegames

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. Анимированная кнопка
@Composable
fun AnimatedActionButton(
    text: String,
    baseColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedColor by animateColorAsState(
        targetValue = if (isPressed) baseColor.copy(alpha = 0.7f) else baseColor,
        animationSpec = tween(150), label = "BtnColor"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "BtnScale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(50.dp)
            .graphicsLayer(scaleX = animatedScale, scaleY = animatedScale)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = animatedColor),
        contentPadding = PaddingValues(0.dp),
        interactionSource = interactionSource
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

// 2. Модификатор для пружинящего нажатия на карточки
@Composable
fun Modifier.animatedClickableCard(
    isPressedColor: Color = Color(0xFFE0E0E0),
    onClick: () -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "CardScale"
    )

    return this
        .graphicsLayer(scaleX = animatedScale, scaleY = animatedScale)
        .animateContentSize()
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
}