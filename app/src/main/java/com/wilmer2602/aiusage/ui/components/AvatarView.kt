package com.wilmer2602.aiusage.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
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

    val painter = painterResource(id = imageRes)

    Canvas(
        modifier = modifier
            .size(300.dp)
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, rotate ->
                    scale *= zoom
                    rotation += rotate
                }
            }
            .clipToBounds()
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val imgSize = min(size.width, size.height)

        // Draw image
        withTransform({
            translate(center.x, center.y)
            rotate(rotation)
            scale(scale, scale)
            translate(-imgSize / 2, -imgSize / 2)
        }) {
            drawPicture(
                picture = painter.picture,
                srcOffset = androidx.compose.ui.geometry.IntOffset(0, 0),
                srcSize = androidx.compose.ui.geometry.IntSize(painter.intrinsicSize.width.toInt(), painter.intrinsicSize.height.toInt()),
                dstOffset = androidx.compose.ui.geometry.IntOffset.Zero,
                dstSize = androidx.compose.ui.geometry.IntSize(imgSize.toInt(), imgSize.toInt())
            )
        }

        // Brain overlay (semi-transparent)
        val alpha = (aiLevel * 0.7f).coerceIn(0f, 0.7f)
        if (alpha > 0.05f) {
            withTransform({
                translate(center.x, center.y)
                rotate(rotation)
                scale(scale, scale)
                translate(-imgSize / 2, -imgSize / 2)
            }) {
                // Upper back area
                val left = imgSize * 0.15f
                val top = imgSize * 0.1f
                val width = imgSize * 0.7f
                val height = imgSize * 0.5f
                drawRect(
                    color = Color(0xFF00FFFF).copy(alpha = alpha * 0.5f),
                    topLeft = Offset(left, top),
                    size = Size(width, height)
                )
                // Circuit lines if AI > 0.5
                if (aiLevel > 0.5f) {
                    val lineColor = if (aiLevel > 0.8f) Color.Magenta.copy(alpha = alpha) else Color.Cyan.copy(alpha = alpha)
                    val steps = 4
                    for (i in 1..steps) {
                        val y = top + height * i / (steps + 1)
                        drawLine(
                            color = lineColor,
                            start = Offset(left + width * 0.1f, y),
                            end = Offset(left + width * 0.9f, y),
                            strokeWidth = 2f
                        )
                    }
                }
                // Processor block if AI > 0.85
                if (aiLevel > 0.85f) {
                    val procSize = imgSize * 0.15f
                    drawRoundRect(
                        color = Color(0xFFFFD700).copy(alpha = alpha),
                        topLeft = Offset(imgSize * 0.425f, imgSize * 0.35f),
                        size = Size(procSize, procSize),
                        cornerRadius = CornerRadius(4f)
                    )
                }
            }
        }
    }
}
