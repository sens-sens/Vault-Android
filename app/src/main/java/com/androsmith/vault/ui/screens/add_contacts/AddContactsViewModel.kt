package com.androsmith.vault.ui.screens.add_contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androsmith.vault.data.model.VaultContact
import com.androsmith.vault.data.repository.ContactRepository
import com.androsmith.vault.domain.model.Contact
import com.androsmith.vault.domain.model.toVaultContacts
import com.androsmith.vault.domain.utils.PhoneNumberUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddContactsViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactUiState())
    private val _allContacts = MutableStateFlow<List<Contact>>(emptyList()) // Store the original list

    val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()

    init {
        loadContacts()
    }

    fun loadContacts() {
        viewModelScope.launch {  // Use viewModelScope for coroutines
            val contacts = contactRepository.getSystemContacts()
            _allContacts.value = contacts // Store all contacts
            _uiState.update { it.copy(contacts = contacts) }
        }
    }

    fun toggleSearchVisibility() {

        _uiState.update { currentState ->

            currentState.copy(
                searchVisibility = !currentState.searchVisibility
            )
        }
    }

    fun addContactsToVault() {

        val vaultContacts: List<VaultContact> = _uiState.value.selectedContacts.toVaultContacts(
        category = "Default",  // Or get the category from the UI
        isHidden = false       // Or get the isHidden value from the UI
    )
        viewModelScope.launch {
            vaultContacts.forEach { contact ->
                contactRepository.addContactToVault(contact)
            }
        }
    }


    fun onContactCheckedChange(contact: Contact, isChecked: Boolean) {
        _uiState.update { currentState ->
            val updatedSelectedContacts = currentState.selectedContacts.toMutableSet()

            if (isChecked) {
                updatedSelectedContacts.add(contact)
            } else {
                updatedSelectedContacts.remove(contact)
            }

            currentState.copy(selectedContacts = updatedSelectedContacts)
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
                        (PhoneNumberUtils.normalizePhoneNumber(contact.phoneNumber)?.contains(query) ?: false)
            }
        }
        _uiState.update { it.copy(contacts = filteredContacts) }
    }

}
