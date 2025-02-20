package com.androsmith.vault.ui.screens.add_contacts

import android.Manifest
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            toggleSearchVisibility = viewModel::toggleSearchVisibility,
            onNavigateBack = onNavigateBack,
            onSearchQueryChange = viewModel::onSearchQueryChange
        )
    }
}

@Composable
fun AddContactsContent(
    uiState: ContactUiState,
    onNavigateBack: () -> Unit,
    onSubmitted: () -> Unit,
    toggleSearchVisibility: () -> Unit,
    onContactCheckedChange: (Contact, Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    Scaffold(floatingActionButton = {
        AddToVaultButton(onClick = {
            onSubmitted()
            Log.d("AddContactsScreen", "Selected contacts: ${uiState.selectedContacts}")
        })
    }, topBar = {
        AddContactsAppBar(
            onNavigateBack = onNavigateBack,
            toggleSearchVisibility = toggleSearchVisibility
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp)
        ) {
            AnimatedVisibility(
                visible = uiState.searchVisibility
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange, // Call the function to update the query
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 8.dp),
                    placeholder = { Text("Search contacts...") },
                )
            }
            LazyColumn(
                modifier = Modifier
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


