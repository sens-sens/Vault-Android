package com.androsmith.vault.domain.utils

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.androsmith.vault.R // Replace with your actual package name/resources
import com.androsmith.vault.data.VaultDatabase
import com.androsmith.vault.data.model.VaultContact
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HeadsUpOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null

    @Inject
    lateinit var database: VaultDatabase  // Inject your database

    companion object {
        const val EXTRA_PHONE_NUMBER = "extra_phone_number"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val phoneNumber = intent?.getStringExtra(EXTRA_PHONE_NUMBER)
        if (phoneNumber != null) {
            showOverlay(phoneNumber)
        } else {
            stopSelf() // Stop service if no phone number is passed
        }
        return START_NOT_STICKY
    }
    private fun showOverlay(phoneNumber: String) {
        val normalizedPhoneNumber = PhoneNumberUtils.normalizePhoneNumber(phoneNumber) ?: phoneNumber

        CoroutineScope(Dispatchers.IO).launch { // Background thread for database operation
            try {
                val contact = database.vaultContactDao().getVaultContactByNumber(normalizedPhoneNumber)

                CoroutineScope(Dispatchers.Main).launch { // UI thread to create overlay
                    if (contact != null) {
                        createOverlayView(contact)
                    } else {
                        stopSelf() // No contact, stop the service
                    }
                }
            } catch (e: Exception) {
                Log.e("HeadsUpOverlayService", "Error fetching contact: ${e.message}", e)
                CoroutineScope(Dispatchers.Main).launch {
                    stopSelf() // Error, stop the service
                }
            }
        }
    }

    private fun createOverlayView(contact: VaultContact) {
        if (overlayView != null) {
            removeOverlayView() // Remove existing view first
        }

        val layoutInflater = LayoutInflater.from(this)
        overlayView = layoutInflater.inflate(R.layout.overlay_layout, null) // Create your overlay layout

        // Customize your overlay layout (overlay_layout.xml)
        val nameTextView = overlayView?.findViewById<TextView>(R.id.nameTextView)
        val categoryTextView = overlayView?.findViewById<TextView>(R.id.categoryTextView)

        nameTextView?.text = contact.name
        categoryTextView?.text = contact.category ?: "Unknown Category"

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY // Modern way
            } else {
                WindowManager.LayoutParams.TYPE_PHONE // Deprecated but necessary for older versions
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or  // Important: Allows touch-through
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or  // Show on lock screen
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or // Dismiss keyguard
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        layoutParams.x = 0
        layoutParams.y = 100 // Adjust position

        try {
            windowManager?.addView(overlayView, layoutParams)
        } catch (e: Exception) {
            Log.e("HeadsUpOverlayService", "Error adding view: ${e.message}", e)
            // Handle exception, e.g., if the user revoked the SYSTEM_ALERT_WINDOW permission *after* it was granted.
            stopSelf() // Stop the service if we can't add the view.
        }
    }

    private fun removeOverlayView() {
        if (overlayView != null) {
            try {
                windowManager?.removeView(overlayView)
            } catch (e: IllegalArgumentException) {
                // View not attached - that's fine
            }
            overlayView = null
        }
    }

    override fun onDestroy() {
        removeOverlayView()
        windowManager = null
        super.onDestroy()
    }
}