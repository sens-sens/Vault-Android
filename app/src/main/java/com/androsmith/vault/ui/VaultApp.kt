package com.androsmith.vault.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.androsmith.vault.ui.navigation.Screens
import com.androsmith.vault.ui.screens.add_contacts.AddContactsScreen
import com.androsmith.vault.ui.screens.vault_list.VaultListScreen

@Composable
fun VaultApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController, startDestination = Screens.VaultList
    ) {
        composable<Screens.VaultList> {
            VaultListScreen(
                onAddContactsClicked = {
                    navController.navigate(Screens.AddContacts)
                }
            )
        }
        composable<Screens.AddContacts> {
            AddContactsScreen(
                onNavigateBack = { navController.popBackStack()},
                onContactsSaved = { navController.navigate(Screens.VaultList) }
            )
        }
    }
}