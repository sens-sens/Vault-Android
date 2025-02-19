package com.androsmith.vault.data.repository

import com.androsmith.vault.data.model.VaultContact
import com.androsmith.vault.domain.model.Contact


interface ContactRepository {
    suspend fun addContactToVault(contact: VaultContact)
    suspend fun getVaultContacts(): List<VaultContact>
    suspend fun getVaultContactByNumber(number: String): VaultContact?
    suspend fun updateVaultContact(contact: VaultContact)
    suspend fun deleteVaultContact(contact: VaultContact)
    suspend fun getSystemContacts(): List<Contact>

}