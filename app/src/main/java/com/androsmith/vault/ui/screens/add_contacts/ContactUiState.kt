package com.androsmith.vault.ui.screens.add_contacts

import com.androsmith.vault.domain.model.Contact

data class ContactUiState(
    val contacts: List<Contact> = emptyList(),
    val selectedContacts: Set<Contact> = emptySet(),
    val searchVisibility: Boolean = false,
    val searchQuery: String = "",
)
