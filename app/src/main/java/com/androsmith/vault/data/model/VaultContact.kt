package com.androsmith.vault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("vault_contacts")
data class VaultContact(
    @PrimaryKey
    val number: String,
    val name: String,
    val category: String?,
    val isHidden: Boolean,
)
