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
    var angle by remember { mutableStateOf(FaceAngle.FRONT) } // toggle for demo

    // Overall AI level (0..1) based on max of four scores
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
        val headWidth = min(size.width, size.height) * 0.7f
        val headHeight = headWidth * 1.2f

        withTransform({
            translate(center.x, center.y)
            rotate(rotation)
            scale(scale, scale)
            translate(-headWidth / 2, -headHeight / 2)
        }) {
            // Draw based on angle
            when (angle) {
                FaceAngle.FRONT -> drawFrontHead(aiLevel, headWidth, headHeight)
                FaceAngle.SIDE -> drawSideHead(aiLevel, headWidth, headHeight)
            }
        }
    }
}

private fun DrawScope.drawFrontHead(aiLevel: Float, w: Float, h: Float) {
    val cx = w / 2
    val cy = h / 2
    val rx = w / 2
    val ry = h / 2

    // Head base (skin)
    drawOval(
        color = Color(0xFFEEEBD0),
        topLeft = Offset(0f, 0f),
        size = Size(w, h)
    )

    // Hair (top)
    val hairColor = Color(0xFF4A3728)
    drawOval(
        color = hairColor,
        topLeft = Offset(0f, -h * 0.15f),
        size = Size(w, h * 0.4f)
    )

    // Eyes
    val eyeY = cy - h * 0.05f
    val eyeXLeft = cx - w * 0.25f
    val eyeXRight = cx + w * 0.25f
    val eyeRadius = w * 0.08f
    // Sclera
    drawCircle(Color.White, eyeRadius, Offset(eyeXLeft, eyeY))
    drawCircle(Color.White, eyeRadius, Offset(eyeXRight, eyeY))
    // Iris/pupil
    val irisColor = if (aiLevel > 0.3f) Color.Cyan else Color(0xFF4A2C00)
    drawCircle(irisColor, eyeRadius * 0.5f, Offset(eyeXLeft, eyeY))
    drawCircle(irisColor, eyeRadius * 0.5f, Offset(eyeXRight, eyeY))
    // Pupil
    drawCircle(Color.Black, eyeRadius * 0.25f, Offset(eyeXLeft, eyeY))
    drawCircle(Color.Black, eyeRadius * 0.25f, Offset(eyeXRight, eyeY))
    // Mechanical glow if high AI
    if (aiLevel > 0.6f) {
        drawCircle(
            color = Color.Cyan.copy(alpha = 0.3f),
            radius = eyeRadius * 1.2f,
            center = Offset(eyeXLeft, eyeY)
        )
        drawCircle(
            color = Color.Cyan.copy(alpha = 0.3f),
            radius = eyeRadius * 1.2f,
            center = Offset(eyeXRight, eyeY)
        )
    }

    // Mouth
    val mouthY = cy + h * 0.25f
    val mouthW = w * 0.3f
    val mouthH = h * 0.06f
    // Simple line or ellipse
    drawOval(
        color = Color(0xFF8B5A2B),
        topLeft = Offset(cx - mouthW / 2, mouthY - mouthH / 2),
        size = Size(mouthW, mouthH)
    )

    // Hands cupping face (small, at lower part of face)
    val handY = cy + h * 0.4f
    val handSize = w * 0.15f
    val handOffsetX = w * 0.25f
    // Left hand
    drawHand(Offset(cx - handOffsetX, handY), handSize, aiLevel)
    // Right hand
    drawHand(Offset(cx + handOffsetX, handY), handSize, aiLevel)

    // Back of head overlay (semi-transparent brain/circuit)
    val brainLeft = 0f
    val brainTop = h * 0.1f
    val brainW = w
    val brainH = h * 0.8f
    val brainAlpha = (aiLevel * 0.8f).coerceIn(0f, 0.8f)

    // Save layer for transparency
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            this.color = Color(0xFF00FFFF).copy(alpha = brainAlpha).toArgb()
            style = PaintingStyle.Stroke
            strokeWidth = 2f
        }
        // Brain outline
        canvas.drawRoundRect(
            Rect(brainLeft, brainTop, brainW, brainH),
            CornerRadius(w * 0.2f).toRoundRect().cornerRadius,
            paint
        )

        if (aiLevel > 0.4f) {
            // Draw circuit lines inside
            paint.strokeWidth = 1f
            val steps = 5
            for (i in 1..steps) {
                val y = brainTop + brainH * i / (steps + 1)
                paint.color = if (aiLevel > 0.7f) Color.Magenta.copy(alpha = brainAlpha).toArgb() else Color.Cyan.copy(alpha = brainAlpha).toArgb()
                canvas.drawLine(
                    Offset(brainLeft + brainW * 0.1f, y),
                    Offset(brainLeft + brainW * 0.9f, y),
                    paint
                )
                // Nodes
                val nodeX1 = brainLeft + brainW * 0.2f
                val nodeX2 = brainLeft + brainW * 0.8f
                paint.style = PaintingStyle.Fill
                canvas.drawCircle(nodeX1, y, 3f, paint)
                canvas.drawCircle(nodeX2, y, 3f, paint)
                paint.style = PaintingStyle.Stroke
            }
        }

        if (aiLevel > 0.8f) {
            // More mechanical: add processor block in center
            paint.color = Color.Yellow.copy(alpha = brainAlpha).toArgb()
            paint.strokeWidth = 2f
            val procSize = min(brainW, brainH) * 0.3f
            val procLeft = cx - procSize / 2
            val procTop = cy - procSize / 2
            canvas.drawRoundRect(
                Rect(procLeft, procTop, procSize, procSize),
                CornerRadius(8f).toRoundRect().cornerRadius,
                paint
            )
        }
    }
}

private fun DrawScope.drawSideHead(aiLevel: Float, w: Float, h: Float) {
    val cx = w / 2
    val cy = h / 2
    val rx = w / 2
    val ry = h / 2

    // Head silhouette (ellipse, but clipped side view)
    // We'll draw a profile shape using path
    val path = Path().apply {
        // Start at chin
        moveTo(w * 0.3f, h * 0.8f)
        // Jaw up to ear
        lineTo(w * 0.2f, h * 0.6f)
        // Back of head
        addArc(
            Rect(w * 0.15f, h * 0.1f, w * 0.85f, h * 0.7f),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 180f
        )
        // Forehead to chin
        lineTo(w * 0.3f, h * 0.8f)
        close()
    }
    drawPath(
        path = path,
        color = Color(0xFFEEEBD0),
        style = Fill
    )

    // Hair (side)
    val hairPath = Path().apply {
        moveTo(w * 0.15f, h * 0.2f)
        lineTo(w * 0.15f, h * 0.1f)
        lineTo(w * 0.85f, h * 0.15f)
        lineTo(w * 0.95f, h * 0.25f)
        lineTo(w * 0.85f, h * 0.45f)
        lineTo(w * 0.3f, h * 0.5f)
        close()
    }
    drawPath(hairPath, color = Color(0xFF4A3728), style = Fill)

    // Eye (one visible)
    val eyeX = w * 0.35f
    val eyeY = h * 0.35f
    val eyeRadius = w * 0.06f
    drawCircle(Color.White, eyeRadius, Offset(eyeX, eyeY))
    val irisColor = if (aiLevel > 0.3f) Color.Cyan else Color(0xFF4A2C00)
    drawCircle(irisColor, eyeRadius * 0.5f, Offset(eyeX, eyeY))
    drawCircle(Color.Black, eyeRadius * 0.25f, Offset(eyeX, eyeY))
    if (aiLevel > 0.6f) {
        drawCircle(Color.Cyan.copy(alpha = 0.3f), eyeRadius * 1.2f, Offset(eyeX, eyeY))
    }

    // Mouth (side)
    val mouthX = w * 0.4f
    val mouthY = h * 0.65f
    drawOval(
        color = Color(0xFF8B5A2B),
        topLeft = Offset(mouthX - w * 0.05f, mouthY - h * 0.03f),
        size = Size(w * 0.1f, h * 0.06f)
    )

    // Hand (side: one hand near chin)
    val handY = h * 0.75f
    val handX = w * 0.3f
    val handSize = w * 0.12f
    drawHand(Offset(handX, handY), handSize, aiLevel)

    // Back of head (transparent brain/circuit)
    val brainLeft = w * 0.15f
    val brainTop = h * 0.1f
    val brainW = w * 0.7f
    val brainH = h * 0.7f
    val brainAlpha = (aiLevel * 0.8f).coerceIn(0f, 0.8f)

    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            this.color = Color(0xFF00FFFF).copy(alpha = brainAlpha).toArgb()
            style = PaintingStyle.Stroke
            strokeWidth = 2f
        }
        // Brain outline
        canvas.drawRoundRect(
            Rect(brainLeft, brainTop, brainW, brainH),
            CornerRadius(w * 0.15f).toRoundRect().cornerRadius,
            paint
        )
        if (aiLevel > 0.4f) {
            paint.strokeWidth = 1f
            val steps = 5
            for (i in 1..steps) {
                val y = brainTop + brainH * i / (steps + 1)
                paint.color = if (aiLevel > 0.7f) Color.Magenta.copy(alpha = brainAlpha).toArgb() else Color.Cyan.copy(alpha = brainAlpha).toArgb()
                canvas.drawLine(
                    Offset(brainLeft + brainW * 0.15f, y),
                    Offset(brainLeft + brainW * 0.85f, y),
                    paint
                )
                val nodeX1 = brainLeft + brainW * 0.25f
                val nodeX2 = brainLeft + brainW * 0.75f
                paint.style = PaintingStyle.Fill
                canvas.drawCircle(nodeX1, y, 3f, paint)
                canvas.drawCircle(nodeX2, y, 3f, paint)
                paint.style = PaintingStyle.Stroke
            }
        }
        if (aiLevel > 0.8f) {
            paint.color = Color.Yellow.copy(alpha = brainAlpha).toArgb()
            paint.strokeWidth = 2f
            val procSize = min(brainW, brainH) * 0.25f
            val procLeft = cx - procSize / 2
            val procTop = cy - procSize / 2 - h * 0.1f
            canvas.drawRoundRect(
                Rect(procLeft, procTop, procSize, procSize),
                CornerRadius(6f).toRoundRect().cornerRadius,
                paint
            )
        }
    }
}

private fun DrawScope.drawHand(center: Offset, size: Float, aiLevel: Float) {
    // Simple hand shape (oval palm + fingers)
    val handPaint = Paint().apply {
        color = if (aiLevel > 0.5f) Color(0xFF6A5ACD).copy(alpha = 0.8f) else Color(0xFFEEEBD0)
        style = PaintingStyle.Fill
    }
    // Palm
    drawOval(
        paint = handPaint,
        topLeft = Offset(center.x - size / 2, center.y - size * 0.6f),
        size = Size(size, size * 1.2f)
    )
    // Fingers (simple lines/ovals)
    val fingerCount = 4
    val fingerSpacing = size * 0.1f
    val fingerStartX = center.x - size * 0.25f
    val fingerStartY = center.y - size * 0.8f
    val fingerLength = size * 0.8f
    val fingerWidth = size * 0.12f
    val fingerPaint = Paint().apply {
        color = if (aiLevel > 0.5f) Color(0xFF8A2BE2).copy(alpha = 0.9f) else Color(0xFFEEEBD0)
        style = PaintingStyle.Stroke
        strokeWidth = fingerWidth
        strokeCap = StrokeCap.Round
    }
    for (i in 0 until fingerCount) {
        val x = fingerStartX + i * fingerSpacing
        drawLine(
            p1 = Offset(x, fingerStartY),
            p2 = Offset(x, fingerStartY - fingerLength),
            paint = fingerPaint
        )
    }
    // Thumb
    val thumbStartX = center.x + size * 0.1f
    val thumbStartY = center.y - size * 0.4f
    drawLine(
        p1 = Offset(thumbStartX, thumbStartY),
        p2 = Offset(thumbStartX + size * 0.3f, thumbStartY - size * 0.4f),
        paint = fingerPaint
    )
}
