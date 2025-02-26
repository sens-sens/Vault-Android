package com.androsmith.vault.ui.screens.add_contacts.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import com.androsmith.vault.R
@Composable
fun AddToVaultButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            painterResource(R.drawable.tick),
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
    }
}