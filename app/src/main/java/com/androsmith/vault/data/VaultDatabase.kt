package com.androsmith.vault.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.androsmith.vault.data.model.VaultContact

@Database(entities = [VaultContact::class], version = 1, exportSchema = false)
abstract class VaultDatabase : RoomDatabase() {
    abstract fun vaultContactDao(): VaultContactDao
}