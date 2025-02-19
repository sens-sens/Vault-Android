package com.androsmith.vault.domain.model

import com.androsmith.vault.data.model.VaultContact
import com.androsmith.vault.domain.utils.PhoneNumberUtils

fun Contact.toVaultContact(category: String? = null, isHidden: Boolean = true): VaultContact {
    return VaultContact(
        number = normalizePhoneNumber(phoneNumber) ?: phoneNumber, // Use original if normalization fails
        name = name,
        category = category,
        isHidden = isHidden
    )
}

fun Set<Contact>.toVaultContacts(category: String? = null, isHidden: Boolean = true): List<VaultContact> {
    return map { it.toVaultContact(category, isHidden) }
}


private fun normalizePhoneNumber(phoneNumber: String): String? { // Return nullable String
    return PhoneNumberUtils.normalizePhoneNumber(phoneNumber)
}