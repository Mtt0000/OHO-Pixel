package com.example.oho.gesture

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.oho.root.SuExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Gestisce l'esecuzione effettiva delle azioni tramite AccessibilityService e comandi Root.
 */
class GestureExecutor(private val service: AccessibilityService) {

    private val scope = CoroutineScope(Dispatchers.Default)

    fun execute(action: GestureAction) {
        Log.d("GestureExecutor", "Esecuzione azione: \$action")
        when (action) {
            GestureAction.NONE -> {}
            GestureAction.BACK -> service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            GestureAction.HOME -> service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
            GestureAction.RECENTS -> service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
            GestureAction.NOTIFICATIONS -> service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
            GestureAction.QUICK_SETTINGS -> service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS)
            GestureAction.SCREENSHOT -> service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
            GestureAction.SCREEN_OFF -> service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
            GestureAction.ONE_HAND_MODE -> {
                Log.d("GestureExecutor", "Azione One-Hand Mode richiesta")
                // Il fallback root per OHO/WM scaling:
                // SuExecutor.executeCommand("wm overscan 0,0,0,400")
            }
            GestureAction.PREVIOUS_APP -> performPreviousApp()
            GestureAction.KILL_APP -> killForegroundApp()
            GestureAction.FLASHLIGHT -> toggleFlashlight()
            GestureAction.QUICK_TOOLS_PANEL -> {
                // Inviare broadcast o usare un singleton manager per triggerare il pannello Compose in :app
                Log.d("GestureExecutor", "Richiesto Quick Tools Panel")
                val intent = Intent("com.example.oho.ACTION_SHOW_QUICK_TOOLS")
                service.sendBroadcast(intent)
            }
        }
    }

    private fun performPreviousApp() {
        scope.launch {
            if (SuExecutor.isRootAvailable()) {
                SuExecutor.injectInput("keyevent 187") // KEYCODE_APP_SWITCH
                kotlinx.coroutines.delay(100)
                SuExecutor.injectInput("keyevent 187")
            }
        }
    }

    private fun killForegroundApp() {
        Log.d("GestureExecutor", "Kill app (richiede root e tracking del package)")
    }

    private fun toggleFlashlight() {
        Log.d("GestureExecutor", "Toggle flashlight")
    }
}
