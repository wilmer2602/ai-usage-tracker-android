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
import androidx.compose.ui.graphics.drawscope.*
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

    // Choose image resource based on angle
    val imageRes = when (angle) {
        FaceAngle.FRONT -> if (aiLevel > 0.5f) R.drawable.avatar_mech_front else R.drawable.avatar_real_front
        FaceAngle.SIDE -> if (aiLevel > 0.5f) R.drawable.avatar_mech_side else R.drawable.avatar_real_side
    }

    // Painter for the base image
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

        withTransform({
            translate(center.x, center.y)
            rotate(rotation)
            scale(scale, scale)
            translate(-imgSize / 2, -imgSize / 2)
        }) {
            // Draw base image
            drawImage(
                image = painter.image,
                srcOffset = androidx.compose.ui.geometry.IntOffset(0, 0),
                srcSize = androidx.compose.ui.geometry.IntSize(painter.intrinsicSize.width.toInt(), painter.intrinsicSize.height.toInt()),
                dstOffset = androidx.compose.ui.geometry.IntOffset.Zero,
                dstSize = androidx.compose.ui.geometry.IntSize(imgSize.toInt(), imgSize.toInt()),
                alpha = 1f
            )
        }

        // Overlay brain effect (semi-transparent)
        val brainAlpha = (aiLevel * 0.7f).coerceIn(0f, 0.7f)
        if (brainAlpha > 0.1f) {
            withTransform({
                translate(center.x, center.y)
                rotate(rotation)
                scale(scale, scale)
                translate(-imgSize / 2, -imgSize / 2)
            }) {
                // Brain region (upper back)
                val brainRect = Rect(imgSize * 0.1f, imgSize * 0.1f, imgSize * 0.8f, imgSize * 0.6f)
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF00FFFF).copy(alpha = brainAlpha * 0.6f),
                            Color(0xFF00FFFF).copy(alpha = brainAlpha * 0.2f)
                        )
                    ),
                    topLeft = Offset(brainRect.left, brainRect.top),
                    size = Size(brainRect.width, brainRect.height),
                    cornerRadius = CornerRadius(imgSize * 0.15f)
                )

                // Circuit lines if AI high
                if (aiLevel > 0.5f) {
                    val lineColor = if (aiLevel > 0.8f) Color.Magenta.copy(alpha = brainAlpha) else Color.Cyan.copy(alpha = brainAlpha)
                    val steps = 5
                    for (i in 1..steps) {
                        val y = brainRect.top + brainRect.height * i / (steps + 1)
                        drawLine(
                            color = lineColor,
                            start = Offset(brainRect.left + brainRect.width * 0.15f, y),
                            end = Offset(brainRect.left + brainRect.width * 0.85f, y),
                            strokeWidth = 2f
                        )
                    }
                }

                // Processor block
                if (aiLevel > 0.85f) {
                    val procSize = imgSize * 0.2f
                    val procLeft = imgSize * 0.4f
                    val procTop = imgSize * 0.3f
                    drawRoundRect(
                        color = Color(0xFFFFD700).copy(alpha = brainAlpha),
                        topLeft = Offset(procLeft, procTop),
                        size = Size(procSize, procSize),
                        cornerRadius = CornerRadius(6f)
                    )
                }
            }
        }
    }
}
