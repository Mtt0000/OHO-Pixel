package com.example.oho.root

import android.util.Log
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Gestore dell'esecuzione dei comandi root usando libsu di topjohnwu.
 */
object SuExecutor {

    private const val TAG = "SuExecutor"

    // Inizializza la shell root in background. Dovrebbe essere chiamata all'avvio dell'app.
    fun init() {
        // Imposta i parametri per libsu
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        )
    }

    /**
     * Verifica se il dispositivo ha accesso root e se la shell su è pronta.
     */
    fun isRootAvailable(): Boolean {
        return Shell.getShell().isRoot
    }

    /**
     * Esegue un comando con i privilegi di root in modo sincrono o asincrono.
     * @param command Il comando shell da eseguire (es. "input keyevent 26")
     * @return Una coppia contenente il codice di uscita e l'output del comando (lista di stringhe).
     */
    suspend fun executeCommand(command: String): Pair<Int, List<String>> = withContext(Dispatchers.IO) {
        if (!isRootAvailable()) {
            Log.e(TAG, "Impossibile eseguire '$command'. Permessi di root non disponibili.")
            return@withContext Pair(-1, listOf("Root non disponibile"))
        }

        Log.d(TAG, "Esecuzione comando root: $command")
        val result = Shell.cmd(command).exec()
        val exitCode = result.code
        val out = result.out

        if (!result.isSuccess) {
            Log.e(TAG, "Comando '$command' fallito con codice $exitCode")
        }

        Pair(exitCode, out)
    }

    /**
     * Termina un'app specificata tramite root (am force-stop).
     * @param packageName Il package name dell'app da terminare.
     */
    suspend fun killApp(packageName: String): Boolean {
        val (exitCode, _) = executeCommand("am force-stop $packageName")
        return exitCode == 0
    }

    /**
     * Simula un evento touch o una gesture tramite input shell (utile come fallback a basso livello).
     * @param action Comando di input come "tap x y" o "keyevent 26"
     */
    suspend fun injectInput(action: String): Boolean {
        val (exitCode, _) = executeCommand("input $action")
        return exitCode == 0
    }
}
