package com.example.oho.gesture

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HandleOverlay(
    isLeft: Boolean,
    onGestureDetected: (SwipeDirection) -> Unit
) {
    val gestureEngine = remember { GestureEngine() }
    var startX by remember { mutableFloatStateOf(0f) }
    var startY by remember { mutableFloatStateOf(0f) }
    var startTime by remember { mutableLongStateOf(0L) }

    // UI Feedback state
    var isSwiping by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(40.dp) // Leggermente più largo per facilitare il tocco
            .fillMaxHeight(0.6f) // Non copre tutto lo schermo in altezza
            .background(Color.Red.copy(alpha = if (isSwiping) 0.5f else 0.1f)) // Rosso per debug, trasparente/invisibile in prod
            .pointerInteropFilter { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = event.rawX
                        startY = event.rawY
                        startTime = System.currentTimeMillis()
                        isSwiping = true
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // Aggiorna l'UI di feedback qui se desiderato
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        val endX = event.rawX
                        val endY = event.rawY
                        val holdDuration = System.currentTimeMillis() - startTime
                        isSwiping = false

                        val direction = gestureEngine.calculateGesture(
                            startX, startY, endX, endY, isLeft, holdDuration
                        )

                        if (direction != SwipeDirection.NONE) {
                            onGestureDetected(direction)
                        }
                        true
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        isSwiping = false
                        true
                    }
                    else -> false
                }
            }
    )
}
