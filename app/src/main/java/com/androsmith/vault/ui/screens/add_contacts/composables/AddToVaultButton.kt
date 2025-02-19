package com.androsmith.vault.ui.screens.add_contacts.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AddToVaultButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick
    ) {
        Icon(
            Icons.Default.Check,
            contentDescription = null
        )
    }
}