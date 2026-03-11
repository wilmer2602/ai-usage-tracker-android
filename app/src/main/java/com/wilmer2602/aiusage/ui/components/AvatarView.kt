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
        val r = min(size.width, size.height) / 2 * 0.8f

        withTransform({
            translate(center.x, center.y)
            rotate(rotation)
            scale(scale, scale)
            translate(-r, -r)
        }) {
            // Simple head circle
            drawOval(color = Color(0xFFEEEBD0), topLeft = Offset(0f, 0f), size = Size(r*2, r*2))
            // Eyes
            val eyeY = r - r*0.1f
            val eyeXL = r - r*0.25f
            val eyeXR = r + r*0.25f
            val eyeR = r * 0.08f
            drawCircle(Color.White, eyeR, Offset(eyeXL, eyeY))
            drawCircle(Color.White, eyeR, Offset(eyeXR, eyeY))
            val irisColor = if (aiLevel > 0.3f) Color.Cyan else Color(0xFF4A2C00)
            drawCircle(irisColor, eyeR*0.5f, Offset(eyeXL, eyeY))
            drawCircle(irisColor, eyeR*0.5f, Offset(eyeXR, eyeY))
            drawCircle(Color.Black, eyeR*0.25f, Offset(eyeXL, eyeY))
            drawCircle(Color.Black, eyeR*0.25f, Offset(eyeXR, eyeY))
            // Mouth
            drawOval(color = Color(0xFF8B5A2B), topLeft = Offset(r - r*0.2f, r + r*0.25f), size = Size(r*0.4f, r*0.08f))
        }
    }
}
