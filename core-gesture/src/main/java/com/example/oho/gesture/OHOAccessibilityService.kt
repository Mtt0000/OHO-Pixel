package com.example.oho.gesture

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.oho.root.SuExecutor

/**
 * Il servizio Accessibility principale per l'app.
 * Oltre a gestire i click globali, inizializza anche il Root in background se necessario.
 */
class OHOAccessibilityService : AccessibilityService() {

    private val TAG = "OHOAccessibilityService"

    // Per consentire agli overlay o ad altre parti dell'app di inviare comandi al service
    companion object {
        var instance: OHOAccessibilityService? = null
        private set
    }

    lateinit var gestureExecutor: GestureExecutor

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "AccessibilityService connesso")
        instance = this
        gestureExecutor = GestureExecutor(this)

        // Prova ad avviare la shell root in background se non è già attiva
        Thread {
            SuExecutor.init()
            if (SuExecutor.isRootAvailable()) {
                Log.d(TAG, "Root disponibile e shell libsu inizializzata.")
            } else {
                Log.w(TAG, "Root non disponibile. Si userà solo Accessibility API.")
            }
        }.start()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Possiamo intercettare typeWindowContentChanged per rilevare l'app in primo piano
        // Questo è utile per le funzioni come 'Kill App' o per i 'Blacklist' per app (es. disabilitare negli giochi)
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()
            Log.v(TAG, "Finestra in primo piano cambiata: \$packageName")
            // Salvare packageName corrente in uno stateflow globale
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "AccessibilityService interrotto")
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}
