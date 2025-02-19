package com.androsmith.vault.data.repository


import com.androsmith.vault.data.datasource.LocalContactDataSource
import com.androsmith.vault.data.datasource.SystemContactDataSource
import com.androsmith.vault.data.model.VaultContact
import com.androsmith.vault.domain.model.Contact
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val localContactDataSource: LocalContactDataSource,
    private val systemContactDataSource: SystemContactDataSource
) : ContactRepository {
    override suspend fun addContactToVault(contact: VaultContact) {
        localContactDataSource.insertVaultContact(contact)
    }

    override suspend fun getVaultContacts(): List<VaultContact> {
        return localContactDataSource.getAllVaultContacts()
    }

    override suspend fun getVaultContactByNumber(number: String): VaultContact? {
        return localContactDataSource.getVaultContactByNumber(number)
    }

    override suspend fun updateVaultContact(contact: VaultContact) {
        localContactDataSource.updateVaultContact(contact)
    }

    override suspend fun deleteVaultContact(contact: VaultContact) {
        localContactDataSource.deleteVaultContact(contact)
    }

    override suspend fun getSystemContacts(): List<Contact> {
        return systemContactDataSource.getContactsFromContentProvider()
    }
}