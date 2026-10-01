# One Hand Operation Clone

Questo è un clone open-source e potenziato dell'app "One Hand Operation +" per dispositivi Android, sviluppato interamente in Kotlin utilizzando Jetpack Compose, Coroutines e integrando funzionalità avanzate tramite permessi Root (sfruttando `libsu` di topjohnwu).

## Funzionalità Principali
- **Maniglie Edge Trasparenti**: Due aree laterali invisibili (destra e sinistra) per intercettare i tocchi.
- **Motore Gesture**: Rilevamento preciso di swipe dritti, diagonali (su e giù), e swipe lunghi (hold).
- **Integrazione Root (Opzionale)**: Supporto per `libsu` / KernelSU per eseguire input a basso livello e comandi di sistema istantanei, bypassando le limitazioni dell'AccessibilityService.
- **Azioni Avanzate**: Include navigazione base (Indietro, Home, Recenti), spegnimento schermo, controllo torcia, kill rapido delle app, e un Quick Tools Panel fluttuante.
- **Interfaccia Moderna**: UI di configurazione e overlay disegnati interamente con Jetpack Compose.

## Struttura Modulare del Progetto
Il progetto Gradle è diviso in quattro moduli principali:
1. `:app`: Modulo principale contenente la `MainActivity`, il `QuickToolsOverlay` e la logica di inizializzazione.
2. `:core-gesture`: Cuore del rilevamento tocchi, contiene l'`AccessibilityService`, l'`OverlayService` (`WindowManager`), il `GestureEngine` e il motore di esecuzione.
3. `:core-root`: Modulo isolato per la gestione dell'accesso Root, astrazione su `libsu` e l'esecuzione di comandi shell asincroni.
4. `:ui-settings`: Modulo che contiene la schermata di configurazione principale (`SettingsScreen`) scritta in Compose.

## Requisiti
- **Android SDK**: Minimo Android 10 (API 29), Target Android 14 (API 34).
- **Root (Raccomandato)**: Magisk, KernelSU, o simili per sbloccare le feature avanzate senza latenza.
- **Permessi**: Richiede `SYSTEM_ALERT_WINDOW` (Disegno su altre app) e `BIND_ACCESSIBILITY_SERVICE` (Servizio di Accessibilità).

## Istruzioni per la Compilazione

1. Assicurati di avere il JDK 17 installato nel tuo sistema.
2. Clona il repository.
3. Apri un terminale nella radice del progetto e avvia la build con Gradle:
   ```bash
   ./gradlew :app:assembleDebug
   ```
4. L'APK generato si troverà nel percorso: `app/build/outputs/apk/debug/app-debug.apk`.

## Installazione e Configurazione

1. Installa l'APK sul tuo dispositivo Android.
2. Apri l'app e clicca su **"Richiedi Permessi"**. Verrai reindirizzato alle impostazioni di Android per concedere il permesso di **"Visualizzazione sopra altre app"** e attivare il **Servizio di Accessibilità** ("One Hand Operation Gestures").
3. Torna all'app e clicca su **"Avvia Overlay Handles"**. Questo avvierà il `WindowManager` posizionando due maniglie ai lati dello schermo (temporaneamente visibili in rosso semitrasparente per il debug, ma invisibili in produzione).
4. (Se hai il root): Il demone `su` verrà inizializzato in background automaticamente.
