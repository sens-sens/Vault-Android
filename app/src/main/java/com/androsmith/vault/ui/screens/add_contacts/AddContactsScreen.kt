package com.androsmith.vault.ui.screens.add_contacts

import android.Manifest
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.androsmith.vault.domain.model.Contact
import com.androsmith.vault.ui.common.composables.CustomSearchBar
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
       ContactsPermissionRequester(permission = Manifest.permission.READ_CONTACTS,
        onPermissionGranted = { /* Permission Granted Logic */ },
        onPermissionDenied = { /* Permission Denied Logic */ }) {

           val viewModel: AddContactsViewModel = hiltViewModel<AddContactsViewModel>()
           val uiState = viewModel.uiState.collectAsState().value


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

    val focusManager = LocalFocusManager.current

    Scaffold(floatingActionButton = {
        AddToVaultButton(onClick = {
            onSubmitted()
            Log.d("AddContactsScreen", "Selected contacts: ${uiState.selectedContacts}")
        })
    }, topBar = {
        AddContactsAppBar(
            text = if(uiState.selectedContacts.isEmpty()) "Add Contacts"
            else "${uiState.selectedContacts.size} selected"
            ,
            onNavigateBack = onNavigateBack,
            toggleSearchVisibility = toggleSearchVisibility
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 8.dp)
        ) {
            AnimatedVisibility(
                visible = uiState.searchVisibility
            ) {
                CustomSearchBar(
                    value = uiState.searchQuery,
                    focusManager = focusManager,
                    onValueChanged = onSearchQueryChange,
                    onFocusChanged = {}
                )
//                OutlinedTextField(
//                    value = uiState.searchQuery,
//                    onValueChange = onSearchQueryChange, // Call the function to update the query
//                    shape = CircleShape,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 8.dp)
//                        .padding(bottom = 8.dp),
//                    placeholder = { Text("") },
//                )
            }

            SelectableContactList(
                contacts = uiState.contacts,
                isChecked = { uiState.selectedContacts.contains(it)},
                onContactCheckedChanged = onContactCheckedChange
            )

        }
    }
}

@Composable
fun SelectableContactList(
    contacts: List<Contact>,
    isChecked: (Contact) -> Boolean,
    onContactCheckedChanged: (Contact, Boolean) -> Unit,
    modifier: Modifier = Modifier) {
    val groupedContacts = contacts.groupBy { it.name.first().uppercaseChar() }
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
    ) {
        groupedContacts.forEach { (letter, contactsForLetter) ->
            item{
                LetterHeader(letter)
            }
            items(contactsForLetter,
                key = { it.id }) { contact ->  // Use key for better performance
                ContactTile(contact = contact,
                    isChecked = isChecked(contact),
                    onCheckedChange = { isChecked ->
                        onContactCheckedChanged(contact, isChecked)
                    })
            }
        }
    }
}

@Composable
fun LetterHeader(
    character: Char,
    modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.width(20.dp)
        )
        Spacer(
            modifier = Modifier.width(10.dp)
        )
        Text("$character",
            style = TextStyle(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6F)
            )
//            modifier = Modifier.weight(1F)
            )
        Spacer(
            modifier = Modifier.width(10.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1F)
        )
    }
}

