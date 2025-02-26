package com.androsmith.vault.data.repository

import com.androsmith.vault.data.model.VaultContact
import com.androsmith.vault.domain.model.Contact
import kotlinx.coroutines.flow.Flow


interface ContactRepository {
    suspend fun addContactToVault(contact: VaultContact)
    suspend fun getVaultContacts(): Flow<List<VaultContact>>
    suspend fun getVaultContactByNumber(number: String): VaultContact?
    suspend fun updateVaultContact(contact: VaultContact)
    suspend fun deleteVaultContact(contact: VaultContact)
    suspend fun getSystemContacts(): List<Contact>

}