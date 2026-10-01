package com.example.oho.gesture

import android.util.Log
import kotlin.math.atan2
import kotlin.math.hypot

enum class SwipeDirection {
    STRAIGHT,
    DIAGONAL_UP,
    DIAGONAL_DOWN,
    LONG_STRAIGHT,
    LONG_DIAGONAL_UP,
    LONG_DIAGONAL_DOWN,
    NONE
}

/**
 * Motore per il calcolo della gesture e l'identificazione della direzione.
 */
class GestureEngine {

    // Soglie (in pixel per il momento)
    private val thresholdDistance = 100f
    private val longThresholdDistance = 300f
    private val angleTolerance = 25.0 // Tolleranza in gradi

    fun calculateGesture(startX: Float, startY: Float, endX: Float, endY: Float, isLeftEdge: Boolean, holdDuration: Long): SwipeDirection {
        val dx = endX - startX
        val dy = endY - startY
        val distance = hypot(dx, dy)

        if (distance < thresholdDistance) {
            return SwipeDirection.NONE
        }

        // Angolo in radianti, poi convertito in gradi
        // atan2 restituisce angolo da -pi a pi
        var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble()))

        // Se è sul bordo destro, normalizza l'angolo come se partisse da sinistra (ribaltando l'asse X)
        if (!isLeftEdge) {
            if (angle > 0) angle = 180 - angle
            else angle = -180 - angle
        }

        val isLongSwipe = distance >= longThresholdDistance || holdDuration > 500L

        Log.v("GestureEngine", "Distance: \$distance, Angle: \$angle, IsLong: \$isLongSwipe")

        return when {
            // Straight
            angle >= -angleTolerance && angle <= angleTolerance -> {
                if (isLongSwipe) SwipeDirection.LONG_STRAIGHT else SwipeDirection.STRAIGHT
            }
            // Diagonal Down (angolo positivo verso il basso dello schermo)
            angle > angleTolerance && angle <= 90 + angleTolerance -> {
                if (isLongSwipe) SwipeDirection.LONG_DIAGONAL_DOWN else SwipeDirection.DIAGONAL_DOWN
            }
            // Diagonal Up (angolo negativo verso l'alto dello schermo)
            angle < -angleTolerance && angle >= -90 - angleTolerance -> {
                if (isLongSwipe) SwipeDirection.LONG_DIAGONAL_UP else SwipeDirection.DIAGONAL_UP
            }
            else -> SwipeDirection.NONE
        }
    }
}
