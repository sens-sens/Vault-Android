package com.androsmith.vault.domain.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.androsmith.vault.MainActivity
import com.androsmith.vault.data.VaultDatabase
import com.androsmith.vault.data.model.VaultContact
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PhoneStateReceiver : BroadcastReceiver() {

    @Inject
    lateinit var database: VaultDatabase



    // Helper function to check if overlay permission is granted
    private fun isOverlayPermissionGranted(context: Context): Boolean {
        // Get a reference to the MainActivity to check the permission flag
        val mainActivity = context as? MainActivity // Assumes PhoneStateReceiver is in the same package as MainActivity. Can also pass Context of MainActivity

        return mainActivity?.isOverlayPermissionGranted() ?: false
    }



    override fun onReceive(context: Context, intent: Intent) {
        val tag = "PhoneStateReceiver"

        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                var phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

                if (phoneNumber.isNullOrEmpty()) {
                    // Try to get the number from call log (if permission granted)
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                        phoneNumber = getLastIncomingCall(context) // Implement this function (see below)
                        Log.d(tag, "Got number from call log: $phoneNumber")
                    } else {
                        Log.w(tag, "READ_CALL_LOG permission denied.")
                        // Optionally, prompt the user to grant the permission
                    }
                }

                if (!phoneNumber.isNullOrEmpty()) {
                    Log.d(tag, "Incoming number: $phoneNumber")
                    CoroutineScope(Dispatchers.IO).launch {
                        checkAndShowNotification(context, phoneNumber)
                    }
                } else {
                    Log.e(tag, "Could not retrieve incoming number.")
                }
            } else if (state == TelephonyManager.EXTRA_STATE_IDLE || state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                // Call ended, stop the overlay
                val serviceIntent = Intent(context, HeadsUpOverlayService::class.java)
                context.stopService(serviceIntent)  // Explicitly stop the service
            }
        }

    }
    // Function to retrieve the last incoming call number from call log
    private fun getLastIncomingCall(context: Context): String? {
        // Same implementation as in the previous response.
        // Requires READ_CALL_LOG permission
        return try {
            val uri = android.provider.CallLog.Calls.CONTENT_URI
            val projection = arrayOf(android.provider.CallLog.Calls.NUMBER)
            val sortOrder = android.provider.CallLog.Calls.DATE + " DESC LIMIT 1" // Get only the last call
            val cursor = context.contentResolver.query(uri, projection, null, null, sortOrder)

            cursor?.use {
                if (it.moveToFirst()) {
                    val number = it.getString(it.getColumnIndexOrThrow(android.provider.CallLog.Calls.NUMBER))
                    return number
                }
            }
            null
        } catch (e: SecurityException) {
            Log.e("PhoneStateReceiver", "SecurityException accessing call log: ${e.message}")
            null // Handle the case where READ_CALL_LOG permission is revoked during runtime
        } catch (e: Exception) {
            Log.e("PhoneStateReceiver", "Error reading call log: ${e.message}")
            null // Generic error handling
        }
    }


    private suspend fun checkAndShowNotification(context: Context, phoneNumber: String) {
        val tag = "PhoneStateReceiver"
        val normalizedPhoneNumber = PhoneNumberUtils.normalizePhoneNumber(phoneNumber) ?: phoneNumber
        try {
            val contact = database.vaultContactDao().getVaultContactByNumber(normalizedPhoneNumber)

            if (contact != null) {
                val serviceIntent = Intent(context, HeadsUpOverlayService::class.java)
                serviceIntent.putExtra(HeadsUpOverlayService.EXTRA_PHONE_NUMBER, phoneNumber) // Pass the number
                context.startService(serviceIntent)
            } else {
                Log.d(tag, "Unsaved Contact")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error checking and showing notification.", e)
        }
    }

    private fun showHeadsUpNotification(context: Context, contact: VaultContact) {
        val tag = "PhoneStateReceiver"
        val channelId = "vault_call_notification"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Vault Call Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for incoming calls from contacts in your Vault."
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create the "normal" notification
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Vault Contact Calling")
            .setContentText("${contact.name} (${contact.category ?: "Unknown"})")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(true)
            .setFullScreenIntent(null, true)

        // Create the public version (visible on lock screen)
        val publicNotification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Incoming Call")  // Generic title
            .setContentText("Vault Contact") // Generic text
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .build()

        // Set the public version
        notificationBuilder.setPublicVersion(publicNotification)

        val notification = notificationBuilder.build()


        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(contact.number.hashCode(), notification)
            } else {
                Log.w(tag, "Notification permission not granted.")
            }
        }
    }
}
