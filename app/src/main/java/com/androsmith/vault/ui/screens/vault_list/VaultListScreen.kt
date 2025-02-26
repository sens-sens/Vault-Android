package com.androsmith.vault.ui.screens.vault_list

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.androsmith.vault.data.model.VaultContact
import com.androsmith.vault.ui.common.composables.CustomSearchBar
import com.androsmith.vault.ui.screens.vault_list.composables.ContactList
import com.androsmith.vault.ui.screens.vault_list.composables.EditContactDialog
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
        onContactDelete = viewModel::onContactDelete,
        onContactEdit = viewModel::onContactEdit,
        onSearchQueryChange = viewModel::onSearchQueryChange,
    )
}

@Composable
fun VaultListContent(
    uiState: VaultUiState,
    onAddContactsClicked: () -> Unit,
    onContactDelete: (VaultContact) -> Unit,
    onContactEdit: (VaultContact) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = onAddContactsClicked
            ) {
                Icon(

                    Icons.Rounded.Add,
                    contentDescription = null
                )
            }
        },
        topBar = { VaultAppBar() },
    ) { innerPadding ->


        val focusManager = LocalFocusManager.current


        var showEditDialog by remember { mutableStateOf(false) }
        var selectedContact by remember { mutableStateOf(VaultContact(number = "", name = "", isHidden = false, category = "")) }

        if (showEditDialog) {
            EditContactDialog(
                contact = selectedContact,
                onDismiss = { showEditDialog = false },
                onSave = { updatedContact ->
                    onContactEdit(updatedContact)
                    showEditDialog = false
                }
            )
        }


        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 8.dp)
        ) {
            CustomSearchBar(
            value = uiState.searchQuery,
            focusManager = focusManager,
            onValueChanged = onSearchQueryChange,
            onFocusChanged = {}
        )

            ContactList(
                contacts = uiState.contacts,
                onContactEdit = {
                    selectedContact = it
                                showEditDialog = true
                                },
                onContactDelete = onContactDelete,
            )

        }
    }
}