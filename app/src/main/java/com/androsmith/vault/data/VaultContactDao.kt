package com.androsmith.vault.data

import androidx.room.*
import com.androsmith.vault.data.model.VaultContact

@Dao
interface VaultContactDao {
    @Query("SELECT * FROM vault_contacts")
    fun getAllVaultContacts(): List<VaultContact>

    @Query("SELECT * FROM vault_contacts WHERE number = :number")
    fun getVaultContactByNumber(number: String): VaultContact?

    @Insert
    suspend fun insertVaultContact(contact: VaultContact)

    @Update
    suspend fun updateVaultContact(contact: VaultContact)

    @Delete
    suspend fun deleteVaultContact(contact: VaultContact)

}