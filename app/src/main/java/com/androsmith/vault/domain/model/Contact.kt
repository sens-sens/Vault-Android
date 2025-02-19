package com.androsmith.vault.domain.model

import com.androsmith.vault.domain.utils.PhoneNumberUtils

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String
) {
    // Override equals and hashCode to handle duplicate phone number formats
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Contact

        return normalizePhoneNumber(phoneNumber) == normalizePhoneNumber(other.phoneNumber)
    }

    override fun hashCode(): Int {
        return normalizePhoneNumber(phoneNumber)?.hashCode() ?: 0 // Handle null case
    }

    // Helper function to normalize phone numbers for comparison
    private fun normalizePhoneNumber(phoneNumber: String): String? { // Return nullable String
        return PhoneNumberUtils.normalizePhoneNumber(phoneNumber)
    }
}