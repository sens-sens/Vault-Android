package com.androsmith.vault.ui.screens.vault_list

import com.androsmith.vault.data.model.VaultContact

data class VaultUiState(
    val contacts: List<VaultContact> = emptyList(),
    val searchQuery: String = "",
)
