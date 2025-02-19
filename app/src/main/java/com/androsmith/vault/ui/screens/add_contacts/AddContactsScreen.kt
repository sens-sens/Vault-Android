package com.androsmith.vault.ui.screens.add_contacts

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androsmith.vault.domain.model.Contact
import com.androsmith.vault.ui.screens.add_contacts.composables.AddContactsAppBar
import com.androsmith.vault.ui.screens.add_contacts.composables.AddToVaultButton
import com.androsmith.vault.ui.screens.add_contacts.composables.ContactTile
import com.androsmith.vault.ui.screens.add_contacts.composables.ContactsPermissionRequester
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddContactsScreen(
    onContactsSaved: () -> Unit,
    modifier: Modifier = Modifier) {
    val viewModel: AddContactsViewModel = hiltViewModel<AddContactsViewModel>()
    val uiState = viewModel.uiState.collectAsState().value

    ContactsPermissionRequester(permission = Manifest.permission.READ_CONTACTS,
        onPermissionGranted = { /* Permission Granted Logic */ },
        onPermissionDenied = { /* Permission Denied Logic */ }) {
        AddContactsContent(
            uiState = uiState,
            onSubmitted = {
                viewModel.addContactsToVault()
                onContactsSaved()
            },
            onContactCheckedChange = viewModel::onContactCheckedChange,
            onSearchQueryChange = viewModel::onSearchQueryChange
        )
    }
}

@Composable
fun AddContactsContent(
    uiState: ContactUiState,
    onSubmitted: () -> Unit,
    onContactCheckedChange: (Contact, Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    Scaffold(floatingActionButton = {
        AddToVaultButton(onClick = {
            onSubmitted()
            Log.d("AddContactsScreen", "Selected contacts: ${uiState.selectedContacts}")
        })
    }, topBar = {
        AddContactsAppBar()
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange, // Call the function to update the query
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = { Text("Search contacts...") },
            )
            LazyColumn(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            ) {
                items(uiState.contacts,
                    key = { it.id }) { contact ->  // Use key for better performance
                    ContactTile(contact = contact,
                        isChecked = uiState.selectedContacts.contains(contact),
                        onCheckedChange = { isChecked ->
                            onContactCheckedChange(contact, isChecked)
                        })
                }
            }
        }
    }
}


