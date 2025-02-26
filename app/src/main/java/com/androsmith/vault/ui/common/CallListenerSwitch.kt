package com.androsmith.vault.ui.common

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.androsmith.vault.domain.utils.PhoneStateForegroundService

@Composable
fun CallListenerSwitch() {
    val context = LocalContext.current
    val isListening = remember { mutableStateOf(false) }
    Switch(
        checked = isListening.value,
        onCheckedChange = {
            isListening.value = it
            val serviceIntent = Intent(context, PhoneStateForegroundService::class.java)
            if (it) {
                ContextCompat.startForegroundService(context, serviceIntent) // Use this to start service
            } else {
                context.stopService(serviceIntent)
            }
        },
        colors = SwitchDefaults.colors(
            uncheckedThumbColor = MaterialTheme.colorScheme.background,
            uncheckedTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .12F),
            uncheckedBorderColor = Color.Transparent,
        )
    )

}