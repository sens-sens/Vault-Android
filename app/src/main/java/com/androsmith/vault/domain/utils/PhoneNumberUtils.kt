package com.androsmith.vault.domain.utils

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat
import java.util.Locale
import android.util.Log

object PhoneNumberUtils {

    private val phoneNumberUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()

    fun normalizePhoneNumber(phoneNumber: String, defaultRegion: String? = null): String? {
        try {
            val number: PhoneNumber = phoneNumberUtil.parse(phoneNumber, defaultRegion ?: getCurrentCountryCode())
            if (!phoneNumberUtil.isValidNumber(number)) {
                Log.w("PhoneNumberUtils", "Invalid phone number: $phoneNumber")
                return null // Or handle invalid numbers as needed
            }
            return phoneNumberUtil.format(number, PhoneNumberFormat.E164) // E.164 format is recommended
        } catch (e: Exception) {
            Log.e("PhoneNumberUtils", "Error parsing phone number: $phoneNumber", e)
            return null // Or handle parsing errors as needed
        }
    }

    private fun getCurrentCountryCode(): String {
        // Get current system country code.
        return Locale.getDefault().country
    }

}
