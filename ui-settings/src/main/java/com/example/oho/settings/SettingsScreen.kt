package com.example.oho.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    var width by remember { mutableFloatStateOf(40f) }
    var transparency by remember { mutableFloatStateOf(0.1f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Impostazioni Maniglie", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Spessore Maniglia: \${width.toInt()} dp")
        Slider(
            value = width,
            onValueChange = { width = it },
            valueRange = 10f..100f
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Trasparenza (Alpha): \${(transparency * 100).toInt()}%")
        Slider(
            value = transparency,
            onValueChange = { transparency = it },
            valueRange = 0f..1f
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text("Configurazione Azioni (Placeholder)", style = MaterialTheme.typography.titleMedium)
        Text("- Swipe Orizzontale: Indietro", style = MaterialTheme.typography.bodyMedium)
        Text("- Swipe Diagonale Su: Recenti", style = MaterialTheme.typography.bodyMedium)
        Text("- Swipe Diagonale Giù: Notifiche", style = MaterialTheme.typography.bodyMedium)
    }
}
