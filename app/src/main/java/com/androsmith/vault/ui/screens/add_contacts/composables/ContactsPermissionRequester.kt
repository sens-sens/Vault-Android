package com.androsmith.vault.ui.screens.add_contacts.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactsPermissionRequester(
    permission: String,  // Make it more generic
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    content: @Composable () -> Unit // Content to show after permission is granted
) {
    val permissionState = rememberPermissionState(permission)

    if (permissionState.status.isGranted) {
        onPermissionGranted()
        content() // Show content only if permission is granted
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) { // Or any other layout you prefer
            if (permissionState.status.shouldShowRationale) {
                Text("Permission is needed.")
            }
            Button(onClick = { permissionState.launchPermissionRequest() }) {
                Text("Request Permission")
            }
            if (!permissionState.status.shouldShowRationale && !permissionState.status.isGranted) {
                onPermissionDenied()
                Text("Permission denied.")
            }
        }
    }
}
