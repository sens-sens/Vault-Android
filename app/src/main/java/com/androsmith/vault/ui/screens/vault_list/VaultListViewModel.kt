package com.androsmith.vault.ui.screens.vault_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androsmith.vault.data.model.VaultContact
import com.androsmith.vault.data.repository.ContactRepository
import com.androsmith.vault.domain.utils.PhoneNumberUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VaultListViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VaultUiState())
    private val _allContacts = MutableStateFlow<List<VaultContact>>(emptyList()) // Store the original list

    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

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
