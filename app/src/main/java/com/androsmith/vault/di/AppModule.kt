package com.androsmith.vault.di

import android.content.Context
import androidx.room.Room
import com.androsmith.vault.data.VaultContactDao
import com.androsmith.vault.data.VaultDatabase
import com.androsmith.vault.data.datasource.LocalContactDataSource
import com.androsmith.vault.data.datasource.SystemContactDataSource
import com.androsmith.vault.data.repository.ContactRepository
import com.androsmith.vault.data.repository.ContactRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideVaultDatabase(@ApplicationContext context: Context): VaultDatabase {
        return Room.databaseBuilder(
            context,
            VaultDatabase::class.java,
            "vault_database"
        ).build()
    }

    @Provides
    fun provideVaultContactDao(database: VaultDatabase): VaultContactDao {
        return database.vaultContactDao()
    }

    @Provides
    @Singleton
    fun provideContactRepository(
        localContactDataSource: LocalContactDataSource,
        systemContactDataSource: SystemContactDataSource
    ): ContactRepository {
        return ContactRepositoryImpl(localContactDataSource, systemContactDataSource)
    }
}