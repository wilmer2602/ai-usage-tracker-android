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

    Canvas(
        modifier = modifier
            .size(300.dp)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rotate ->
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
            // Head silhouette (oval)
            drawOval(
                color = Color(0xFFEEEBD0), // skin tone
                topLeft = Offset(0f, 0f),
                size = Size(headRadius * 2, headRadius * 2)
            )

            // Brain outline (no fill)
            val brainLeft = headRadius * 0.3f
            val brainTop = headRadius * 0.1f
            val brainWidth = headRadius * 1.4f
            val brainHeight = headRadius * 1.2f
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(brainLeft, brainTop),
                size = Size(brainWidth, brainHeight),
                cornerRadius = CornerRadius(headRadius * 0.2f)
            )

            // Helper to draw a region marker (small filled circle)
            fun drawRegionMarker(centerX: Float, centerY: Float, score: Int) {
                // Color from green (120) to cyan (180)
                val hue = 120 + (score / 100f) * 60
                val color = Color.hsv(hue, 0.8f, 0.9f)
                // Size from 12 to 20
                val radius = 12f + (score / 100f) * 8f
                drawCircle(
                    color = color,
                    radius = radius,
                    center = Offset(centerX, centerY)
                )
            }

            // Coordinates (relative to head bounding box)
            val cx = headRadius // center x of head
            val cy = headRadius // center y

            // Visual (eyes): slightly above center
            val eyeY = cy - headRadius * 0.1f
            val eyeOffsetX = headRadius * 0.25f
            drawRegionMarker(cx - eyeOffsetX, eyeY, visualScore)
            drawRegionMarker(cx + eyeOffsetX, eyeY, visualScore)

            // Audio (ears): sides at ear height
            val earY = cy
            val earXOffset = headRadius * 0.1f
            drawRegionMarker(earXOffset, earY, audioScore)
            drawRegionMarker(headRadius * 2 - earXOffset, earY, audioScore)

            // Language (mouth): below center
            val mouthY = cy + headRadius * 0.35f
            drawRegionMarker(cx, mouthY, languageScore)

            // Motor (hand): below mouth, maybe one hand
            val handY = cy + headRadius * 0.65f
            drawRegionMarker(cx, handY, motorScore)
        }
    }
}
