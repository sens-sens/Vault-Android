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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.androsmith.vault.R // Replace with your actual package name/resources
import com.androsmith.vault.data.VaultDatabase
import com.androsmith.vault.data.model.VaultContact
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HeadsUpOverlayService : Service(),
    LifecycleOwner,
    SavedStateRegistryOwner
{

    private var windowManager: WindowManager? = null


    private val _lifecycleRegistry = LifecycleRegistry(this)
    private val _savedStateRegistryController: SavedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry = _savedStateRegistryController.savedStateRegistry
    override val lifecycle: Lifecycle = _lifecycleRegistry

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

        _savedStateRegistryController.performAttach()
        _savedStateRegistryController.performRestore(null)
        _lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
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
            removeOverlayView()
        }

        _lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        _lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        overlayView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@HeadsUpOverlayService)
            setViewTreeSavedStateRegistryOwner(this@HeadsUpOverlayService)
            setContent {
                // Your Compose UI for the overlay
                OverlayContent(contact = contact)
            }
        }

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.CENTER
        layoutParams.x = 0
        layoutParams.y = 0

        try {
            windowManager?.addView(overlayView, layoutParams)
        } catch (e: Exception) {
            Log.e("HeadsUpOverlayService", "Error adding view: ${e.message}", e)
            CoroutineScope(Dispatchers.Main).launch {
                stopSelf()
            }
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


            _lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            _lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    }

    override fun onDestroy() {
        removeOverlayView()
        windowManager = null
        super.onDestroy()
        _lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}
@Composable
fun OverlayContent(contact: VaultContact) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = contact.name,
            color = Color.White,
            fontSize = 16.sp
        )
        Text(
            text = contact.category ?: "Unknown Category",
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}
