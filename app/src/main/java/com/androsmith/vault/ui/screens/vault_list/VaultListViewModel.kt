package com.androsmith.vault.ui.screens.vault_list

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androsmith.vault.data.datasource.LocalUserPreferenceDataSource
import com.androsmith.vault.data.model.VaultContact
import com.androsmith.vault.data.repository.ContactRepository
import com.androsmith.vault.domain.utils.PhoneNumberUtils
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import javax.inject.Inject


@HiltViewModel
class VaultListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contactRepository: ContactRepository,
    private val preferenceDataSource: LocalUserPreferenceDataSource,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VaultUiState())
    private val _allContacts = MutableStateFlow<List<VaultContact>>(emptyList()) // Store the original list

    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    private var _defaultCategory = "Default"



    private val _exportUri: MutableState<Uri?> = mutableStateOf(null)
    val exportUri: Uri? get() = _exportUri.value

    private val _importUri: MutableState<Uri?> = mutableStateOf(null)
    val importUri: Uri? get() = _importUri.value


    fun performExport(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val contacts = contactRepository.getVaultContacts().first()
                val jsonString = Json.encodeToString(contacts) // Use kotlinx-serialization
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(jsonString)
                    }
                }
                Log.d("VaultViewModel", "Contacts exported successfully to $uri")
            } catch (e: IOException) {
                Log.e("VaultViewModel", "Error exporting contacts", e)
                // Handle the exception (e.g., show a toast or snackbar)
            }
        }
    }

    fun changeCategory(category: String){
        viewModelScope.launch(Dispatchers.IO) {
            preferenceDataSource.setDefaultCategory(category)
        }
    }

    // Call this when you have the import URI
    fun performImport(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val json = StringBuilder()
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            json.append(line)
                        }
                    }
                }

                val contacts: List<VaultContact> = Json.decodeFromString(json.toString()) // Use kotlinx-serialization

                // Insert contacts into the database
                for (c in contacts) {
                    contactRepository.addContactToVault(c)
                }

                Log.d("VaultViewModel", "Contacts imported successfully from $uri")
            } catch (e: IOException) {
                Log.e("VaultViewModel", "Error importing contacts", e)
                // Handle the exception
            } catch (e: Exception) { // Use generic Exception for decoding errors
                Log.e("VaultViewModel", "Error decoding contacts", e)
                // Handle the exception
            }
        }
    }

    init {
        loadContacts()
    }



    fun loadContacts() {
        viewModelScope.launch(Dispatchers.IO) {  // Use viewModelScope for coroutines
            contactRepository.getVaultContacts().collect { contacts ->
                _allContacts.value = contacts // Store all contacts
                _uiState.update { it.copy(contacts = contacts) }
            }

        }
    }

    fun onContactEdit(contact: VaultContact){
        viewModelScope.launch(Dispatchers.IO){
            contactRepository.updateVaultContact(contact)
        }
    }
    fun onContactDelete(contact: VaultContact) {
        viewModelScope.launch(Dispatchers.IO) {
            contactRepository.deleteVaultContact(contact)
        }
    }
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterContacts(query)
    }

    private fun filterContacts(query: String) {
        val filteredContacts = if (query.isBlank()) {
            _allContacts.value // Show all contacts when the query is empty
        } else {
            _allContacts.value.filter { contact ->
                contact.name.contains(query, ignoreCase = true) ||
                        (PhoneNumberUtils.normalizePhoneNumber(contact.number)?.contains(query) == true)
            }
        }
        _uiState.update { it.copy(contacts = filteredContacts) }
    }

}
