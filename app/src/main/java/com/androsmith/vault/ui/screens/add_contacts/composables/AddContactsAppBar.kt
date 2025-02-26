package com.androsmith.vault.ui.screens.add_contacts.composables

import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.androsmith.vault.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactsAppBar(
    text: String,
    onNavigateBack: () -> Unit,
    toggleSearchVisibility: () -> Unit,
    modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = { Text(text)},
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        actions = {
            IconButton(
                onClick = toggleSearchVisibility
            ) {
                Icon(
                    painterResource(R.drawable.search),
                    contentDescription = "Search",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
}