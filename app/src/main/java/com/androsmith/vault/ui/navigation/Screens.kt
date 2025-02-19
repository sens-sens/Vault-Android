package com.androsmith.vault.ui.navigation


import kotlinx.serialization.Serializable

sealed interface Screens {

    @Serializable
    object VaultList: Screens

    @Serializable
    object AddContacts: Screens

    @Serializable
    object Welcome: Screens
}