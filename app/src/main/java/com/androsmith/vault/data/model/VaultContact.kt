package com.androsmith.vault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Entity("vault_contacts")
@Serializable
data class VaultContact(
    @PrimaryKey
    val number: String,
    val name: String,
    val category: String?,
    val isHidden: Boolean,
)
