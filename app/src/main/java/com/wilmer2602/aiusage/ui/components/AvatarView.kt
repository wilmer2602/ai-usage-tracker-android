package com.wilmer2602.aiusage.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
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

    // Choose image based on angle and AI level
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
    }
}
