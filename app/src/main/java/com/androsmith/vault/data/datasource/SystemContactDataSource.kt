package com.androsmith.vault.data.datasource


import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.androsmith.vault.domain.model.Contact
import com.androsmith.vault.domain.utils.PhoneNumberUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SystemContactDataSource @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun getContactsFromContentProvider(): List<Contact> {
        return withContext(Dispatchers.IO) {
            val contactsList = mutableSetOf<Contact>() // Use a Set to avoid duplicates
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )

            cursor?.use {
                while (it.moveToNext()) {
                    val idColumnIndex =
                        it.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)
                    val nameColumnIndex =
                        it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val phoneNumberColumnIndex =
                        it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                    if (idColumnIndex != -1 && nameColumnIndex != -1 && phoneNumberColumnIndex != -1) {
                        val id = it.getString(idColumnIndex)
                        val name = it.getString(nameColumnIndex)
                        val phoneNumber = it.getString(phoneNumberColumnIndex)

                        val normalizedPhoneNumber =
                            PhoneNumberUtils.normalizePhoneNumber(phoneNumber)

                        if (normalizedPhoneNumber != null) {
                            val contact = Contact(id, name, phoneNumber)
                            contactsList.add(contact) // Set automatically handles duplicates
                        } else {
                            // Handle invalid phone number
                            Log.w("ContactLoading", "Skipping invalid phone number: $phoneNumber")
                        }

                    }
                }
            }
            cursor?.close()
            contactsList.toList() // Convert to list when returning
        }
    }

}