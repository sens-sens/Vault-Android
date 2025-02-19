package com.androsmith.vault.ui.screens.vault_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.androsmith.vault.ui.screens.vault_list.composables.VaultAppBar
import com.androsmith.vault.ui.screens.vault_list.composables.VaultContactTile

@Composable
fun VaultListScreen(
    onAddContactsClicked: () -> Unit, modifier: Modifier = Modifier
) {

    val viewModel: VaultListViewModel = hiltViewModel<VaultListViewModel>()
    val uiState = viewModel.uiState.collectAsState().value
    VaultListContent(
        uiState = uiState,
        onAddContactsClicked = onAddContactsClicked,
        onSearchQueryChange = viewModel::onSearchQueryChange,
    )
}

@Composable
fun VaultListContent(
    uiState: VaultUiState,
    onAddContactsClicked: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddContactsClicked
            ) {
                Icon(
                    Icons.Default.Add, contentDescription = null
                )
            }
        },
        topBar = { VaultAppBar() },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange, // Call the function to update the query
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search contacts...") },
            )
            LazyColumn(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            ) {
                items(uiState.contacts) { contact ->
                    VaultContactTile(contact)
                }
            }
        }
    }
}