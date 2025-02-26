package com.androsmith.vault.data.datasource

import com.androsmith.vault.data.VaultContactDao
import com.androsmith.vault.data.model.VaultContact
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalContactDataSource @Inject constructor(private val vaultContactDao: VaultContactDao) {
    suspend fun insertVaultContact(contact: VaultContact) {
        vaultContactDao.insertVaultContact(contact)
    }

    suspend fun getAllVaultContacts(): Flow<List<VaultContact>> {
        return vaultContactDao.getAllVaultContacts()
    }
    suspend fun getVaultContactByNumber(number: String): VaultContact? {
        return vaultContactDao.getVaultContactByNumber(number)
    }
    suspend fun updateVaultContact(contact: VaultContact) {
        vaultContactDao.updateVaultContact(contact)
    }

    suspend fun deleteVaultContact(contact: VaultContact) {
        vaultContactDao.deleteVaultContact(contact)
    }
}