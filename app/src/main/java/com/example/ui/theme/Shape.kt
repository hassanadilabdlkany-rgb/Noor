package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val IslamicShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// Distinctive Islamic-inspired geometry shapes
val IslamicArchShape = RoundedCornerShape(
    topStart = 28.dp,
    topEnd = 28.dp,
    bottomStart = 14.dp,
    bottomEnd = 14.dp
)

val IslamicPillShape = RoundedCornerShape(50.dp)
val IslamicCardShape = RoundedCornerShape(20.dp)
val IslamicDialogShape = RoundedCornerShape(26.dp)
