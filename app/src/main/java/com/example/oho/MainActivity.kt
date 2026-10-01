package com.example.oho

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oho.gesture.OverlayService
import com.example.oho.settings.SettingsScreen

class MainActivity : ComponentActivity() {

    private val quickToolsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.example.oho.ACTION_SHOW_QUICK_TOOLS" && context != null) {
                QuickToolsOverlayManager.showQuickTools(context)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Registra ricevitore per mostrare il Quick Tools Panel
        val filter = IntentFilter("com.example.oho.ACTION_SHOW_QUICK_TOOLS")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(quickToolsReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(quickToolsReceiver, filter)
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        onStartOverlay = { startOverlayService() },
                        onRequestPermissions = { requestPermissions() }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(quickToolsReceiver)
    }

    private fun requestPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

        val accessibilityIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(accessibilityIntent)
    }

    private fun startOverlayService() {
        if (Settings.canDrawOverlays(this)) {
            startService(Intent(this, OverlayService::class.java))
        }
    }
}

@Composable
fun MainScreen(
    onStartOverlay: () -> Unit,
    onRequestPermissions: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("One Hand Operation Clone", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onRequestPermissions) {
            Text("Richiedi Permessi (Overlay & Accessibilità)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onStartOverlay) {
            Text("Avvia Overlay Handles")
        }

        Spacer(modifier = Modifier.height(32.dp))

        SettingsScreen()
    }
}
