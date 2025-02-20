package com.androsmith.vault.ui.screens.add_contacts.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactsAppBar(
    onNavigateBack: () -> Unit,
    toggleSearchVisibility: () -> Unit,
    modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = { Text("Add Contacts")},
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(
                onClick = toggleSearchVisibility
            ) {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = null
                )
            }
        }
    )
}