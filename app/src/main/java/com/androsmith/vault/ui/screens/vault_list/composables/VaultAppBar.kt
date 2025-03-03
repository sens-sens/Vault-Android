package com.androsmith.vault.ui.screens.vault_list.composables

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.androsmith.vault.ui.common.CallListenerSwitch
import com.androsmith.vault.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultAppBar(
    onDrawerClicked: () -> Unit,
    modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(
                onClick = onDrawerClicked
            ) {
                Icon(
                    painter = painterResource(R.drawable.menu_alt_03_svgrepo_com),
                    contentDescription = "Drawer",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = { Text("Vault")},
        actions = {
            CallListenerSwitch()
            Spacer(Modifier.width(12.dp))
        }
    )
}