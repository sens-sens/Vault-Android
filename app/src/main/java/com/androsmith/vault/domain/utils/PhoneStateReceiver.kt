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

    override fun onReceive(context: Context, intent: Intent) {
        val tag = "PhoneStateReceiver"

        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                @Suppress("DEPRECATION")
                val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                if (phoneNumber != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        checkAndShowNotification(context, phoneNumber)
                    }
                } else {
                    Log.e(tag, "Incoming number is null.")
                }
            }
        }
    }

    private suspend fun checkAndShowNotification(context: Context, phoneNumber: String) {
        val tag = "PhoneStateReceiver"
        try {
            val contact = database.vaultContactDao().getVaultContactByNumber(phoneNumber)

            if (contact != null) {
                showHeadsUpNotification(context, contact)
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
