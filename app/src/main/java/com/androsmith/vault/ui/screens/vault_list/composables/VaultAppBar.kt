package com.androsmith.vault.ui.screens.vault_list.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androsmith.vault.ui.common.CallListenerSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultAppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = { Text("Vault")},
        actions = {
            CallListenerSwitch()
            Spacer(Modifier.width(12.dp))
        }
    )
}