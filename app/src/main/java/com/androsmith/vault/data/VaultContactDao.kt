package com.androsmith.vault.data

import androidx.room.*
import com.androsmith.vault.data.model.VaultContact
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultContactDao {
    @Query("SELECT * FROM vault_contacts")
    fun getAllVaultContacts(): Flow<List<VaultContact>>

    @Query("SELECT * FROM vault_contacts WHERE number = :number")
    fun getVaultContactByNumber(number: String): VaultContact?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertVaultContact(contact: VaultContact)

    @Update
    suspend fun updateVaultContact(contact: VaultContact)

    @Delete
    suspend fun deleteVaultContact(contact: VaultContact)

}