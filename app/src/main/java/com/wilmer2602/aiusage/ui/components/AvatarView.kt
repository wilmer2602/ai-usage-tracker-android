package com.wilmer2602.aiusage.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.*

private enum class FaceAngle { FRONT, SIDE }

@Composable
fun AvatarView(
    visualScore: Int,
    audioScore: Int,
    languageScore: Int,
    motorScore: Int,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var angle by remember { mutableStateOf(FaceAngle.FRONT) }

    val aiLevel = maxOf(visualScore, languageScore, motorScore) / 100f

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
        val headRadius = min(size.width, size.height) / 2 * 0.8f

        withTransform({
            translate(center.x, center.y)
            rotate(rotation)
            scale(scale, scale)
            translate(-headRadius, -headRadius)
        }) {
            when (angle) {
                FaceAngle.FRONT -> drawFront(aiLevel, headRadius)
                FaceAngle.SIDE -> drawSide(aiLevel, headRadius)
            }
        }
    }
}

private fun DrawScope.drawFront(aiLevel: Float, r: Float) {
    // Head
    drawOval(color = Color(0xFFEEEBD0), topLeft = Offset(0f, 0f), size = Size(r*2, r*2))
    // Hair
    drawOval(color = Color(0xFF4A3728), topLeft = Offset(0f, -r*0.2f), size = Size(r*2, r*0.6f))
    // Eyes
    val eyeY = r - r*0.1f
    val eyeXL = r - r*0.3f
    val eyeXR = r + r*0.3f
    val eyeR = r * 0.08f
    drawCircle(Color.White, eyeR, Offset(eyeXL, eyeY))
    drawCircle(Color.White, eyeR, Offset(eyeXR, eyeY))
    val irisColor = if (aiLevel > 0.3f) Color.Cyan else Color(0xFF4A2C00)
    drawCircle(irisColor, eyeR*0.5f, Offset(eyeXL, eyeY))
    drawCircle(irisColor, eyeR*0.5f, Offset(eyeXR, eyeY))
    drawCircle(Color.Black, eyeR*0.25f, Offset(eyeXL, eyeY))
    drawCircle(Color.Black, eyeR*0.25f, Offset(eyeXR, eyeY))
    if (aiLevel > 0.6f) {
        drawCircle(Color.Cyan.copy(alpha=0.3f), eyeR*1.2f, Offset(eyeXL, eyeY))
        drawCircle(Color.Cyan.copy(alpha=0.3f), eyeR*1.2f, Offset(eyeXR, eyeY))
    }
    // Mouth
    drawOval(color = Color(0xFF8B5A2B), topLeft = Offset(r - r*0.2f, r + r*0.25f), size = Size(r*0.4f, r*0.08f))
    // Hands
    val handY = r + r*0.4f
    val handSize = r * 0.15f
    drawHand(Offset(r - r*0.25f, handY), handSize, aiLevel)
    drawHand(Offset(r + r*0.25f, handY), handSize, aiLevel)
    // Brain overlay
    val alpha = (aiLevel * 0.7f).coerceIn(0f, 0.7f)
    drawRoundRect(Color.Cyan.copy(alpha=alpha), Offset(0f, r*0.1f), Size(r*2, r*1.6f), CornerRadius(r*0.2f))
    if (aiLevel > 0.4f) {
        val lineColor = if (aiLevel > 0.7f) Color.Magenta.copy(alpha=alpha) else Color.Cyan.copy(alpha=alpha)
        for (i in 1..5) {
            val y = r*0.1f + r*1.6f * i / 6f
            drawLine(lineColor, Offset(r*0.2f, y), Offset(r*1.8f, y), 1f)
        }
    }
    if (aiLevel > 0.85f) {
        val procSize = r * 0.3f
        drawRoundRect(Color.Yellow.copy(alpha=alpha), Offset(r - procSize/2, r - procSize/2), Size(procSize, procSize), CornerRadius(6f))
    }
}

private fun DrawScope.drawSide(aiLevel: Float, r: Float) {
    // Profile
    val path = Path().apply {
        moveTo(r*0.3f, r*1.6f)
        lineTo(r*0.2f, r*1.2f)
        addArc(Rect(r*0.15f, r*0.2f, r*1.7f, r*1.4f), 90f, 180f)
        lineTo(r*0.3f, r*1.6f)
        close()
    }
    drawPath(path, color = Color(0xFFEEEBD0))
    // Hair
    val hair = Path().apply {
        moveTo(r*0.15f, r*0.4f)
        lineTo(r*0.15f, r*0.2f)
        lineTo(r*1.7f, r*0.3f)
        lineTo(r*1.9f, r*0.5f)
        lineTo(r*1.7f, r*0.9f)
        lineTo(r*0.6f, r*1.0f)
        close()
    }
    drawPath(hair, color = Color(0xFF4A3728))
    // Eye
    val eyeX = r*0.7f
    val eyeY = r*0.7f
    val eyeR = r*0.08f
    drawCircle(Color.White, eyeR, Offset(eyeX, eyeY))
    val irisColor = if (aiLevel > 0.3f) Color.Cyan else Color(0xFF4A2C00)
    drawCircle(irisColor, eyeR*0.5f, Offset(eyeX, eyeY))
    drawCircle(Color.Black, eyeR*0.25f, Offset(eyeX, eyeY))
    if (aiLevel > 0.6f) {
        drawCircle(Color.Cyan.copy(alpha=0.3f), eyeR*1.2f, Offset(eyeX, eyeY))
    }
    // Mouth
    drawOval(color = Color(0xFF8B5A2B), topLeft = Offset(r*0.65f, r*1.3f), size = Size(r*0.2f, r*0.08f))
    // Hand
    drawHand(Offset(r*0.56f, r*1.5f), r*0.1f, aiLevel)
    // Brain overlay
    val alpha = (aiLevel * 0.7f).coerceIn(0f, 0.7f)
    drawRoundRect(Color.Cyan.copy(alpha=alpha), Offset(r*0.3f, r*0.2f), Size(r*1.4f, r*1.4f), CornerRadius(r*0.15f))
    if (aiLevel > 0.85f) {
        val procSize = r*0.2f
        drawRoundRect(Color.Yellow.copy(alpha=alpha), Offset(r - procSize/2 - r*0.2f, r - procSize/2), Size(procSize, procSize), CornerRadius(4f))
    }
}

private fun DrawScope.drawHand(center: Offset, size: Float, aiLevel: Float) {
    val skin = if (aiLevel > 0.5f) Color(0xFF6A5ACD) else Color(0xFFEEEBD0)
    drawOval(color = skin, topLeft = Offset(center.x - size/2, center.y - size*0.6f), size = Size(size, size*1.2f))
    // Fingers
    val fingerWidth = size * 0.1f
    for (i in 0..3) {
        val x = center.x - size*0.2f + i*fingerWidth*1.2f
        drawLine(color = skin, start = Offset(x, center.y - size*0.2f), end = Offset(x, center.y - size*0.9f), strokeWidth = fingerWidth, cap = StrokeCap.Round)
    }
    // Thumb
    drawLine(color = skin, start = Offset(center.x + size*0.1f, center.y - size*0.2f), end = Offset(center.x + size*0.2f, center.y - size*0.5f), strokeWidth = fingerWidth, cap = StrokeCap.Round)
}
