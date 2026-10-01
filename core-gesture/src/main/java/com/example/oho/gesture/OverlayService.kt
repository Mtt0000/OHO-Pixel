package com.example.oho.gesture

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

/**
 * Servizio che crea gli overlay di WindowManager per le maniglie laterali.
 */
class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var leftHandleView: ComposeView? = null
    private var rightHandleView: ComposeView? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        setupOverlays()
    }

    private fun setupOverlays() {
        leftHandleView = createHandleView(isLeft = true)
        rightHandleView = createHandleView(isLeft = false)

        windowManager.addView(leftHandleView, getHandleLayoutParams(isLeft = true))
        windowManager.addView(rightHandleView, getHandleLayoutParams(isLeft = false))
    }

    private fun createHandleView(isLeft: Boolean): ComposeView {
        val composeView = ComposeView(this).apply {
            // È necessario un LifecycleOwner per far funzionare Compose fuori da un'Activity
            val lifecycleOwner = MyLifecycleOwner()
            lifecycleOwner.performRestore(null)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)

            setContent {
                HandleOverlay(
                    isLeft = isLeft,
                    onGestureDetected = { direction ->
                        Log.d("OverlayService", "Gesture \$direction detected")
                        // In una build reale, mapperemmo le direction in Action tramite i Settings
                        // Qui usiamo un'azione di default per test
                        val action = if (direction == SwipeDirection.STRAIGHT) GestureAction.BACK else GestureAction.NONE
                        OHOAccessibilityService.instance?.gestureExecutor?.execute(action)
                    }
                )
            }

            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
        return composeView
    }

    private fun getHandleLayoutParams(isLeft: Boolean): WindowManager.LayoutParams {
        val params = WindowManager.LayoutParams(
            40, // width iniziale, verrà controllata dalle impostazioni
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = if (isLeft) Gravity.START or Gravity.CENTER_VERTICAL else Gravity.END or Gravity.CENTER_VERTICAL
        return params
    }

    override fun onDestroy() {
        super.onDestroy()
        leftHandleView?.let { windowManager.removeView(it) }
        rightHandleView?.let { windowManager.removeView(it) }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

// Necessario per usare Compose all'interno di un WindowManager senza Activity
private class MyLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }

    fun performRestore(savedState: android.os.Bundle?) {
        savedStateRegistryController.performRestore(savedState)
    }
}
