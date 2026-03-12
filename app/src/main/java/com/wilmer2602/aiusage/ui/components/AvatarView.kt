package com.wilmer2602.aiusage.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlin.math.*

internal enum class FaceAngle(val value: Int) { FRONT(0), SIDE(1) }

@Composable
fun AvatarView(
    visualScore: Int,
    audioScore: Int,
    languageScore: Int,
    motorScore: Int,
    modifier: Modifier = Modifier,
    faceAngle: Int = 0
) {
    val angle = when (faceAngle) {
        0 -> FaceAngle.FRONT
        1 -> FaceAngle.SIDE
        else -> FaceAngle.FRONT
    }
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }

    val aiLevel = maxOf(visualScore, languageScore, motorScore) / 100f

    // Choose image
    val imageRes = when (angle) {
        FaceAngle.FRONT -> if (aiLevel > 0.5f) R.drawable.avatar_mech_front else R.drawable.avatar_real_front
        FaceAngle.SIDE -> if (aiLevel > 0.5f) R.drawable.avatar_mech_side else R.drawable.avatar_real_side
    }

    Box(
        modifier = modifier
            .size(300.dp)
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, rotate ->
                    scale *= zoom
                    rotation += rotate
                }
            }
            .clipToBounds()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
            }
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Avatar",
            modifier = Modifier.matchParentSize()
        )

        // Brain overlay (semi-transparent)
        if (aiLevel > 0.05f) {
            val alpha = (aiLevel * 0.7f).coerceIn(0f, 0.7f)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        translationX = size.width * 0.15f
                        translationY = size.height * 0.1f
                    }
            ) {
                // Upper back area overlay
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.matchParentSize()
                ) {
                    val w = size.width * 0.7f
                    val h = size.height * 0.5f
                    drawRect(
                        color = Color(0xFF00FFFF).copy(alpha = alpha * 0.5f),
                        topLeft = Offset(0f, 0f),
                        size = android.compose.ui.geometry.Size(w, h)
                    )
                    if (aiLevel > 0.5f) {
                        val lineColor = if (aiLevel > 0.8f) Color.Magenta.copy(alpha = alpha) else Color.Cyan.copy(alpha = alpha)
                        val steps = 4
                        for (i in 1..steps) {
                            val y = h * i / (steps + 1)
                            drawLine(
                                color = lineColor,
                                start = Offset(w * 0.1f, y),
                                end = Offset(w * 0.9f, y),
                                strokeWidth = 2f
                            )
                        }
                    }
                    if (aiLevel > 0.85f) {
                        val procSize = min(w, h) * 0.15f
                        drawRoundRect(
                            color = Color(0xFFFFD700).copy(alpha = alpha),
                            topLeft = Offset(w * 0.375f, h * 0.35f),
                            size = android.compose.ui.geometry.Size(procSize, procSize),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
                        )
                    }
                }
            }
        }
    }
}
