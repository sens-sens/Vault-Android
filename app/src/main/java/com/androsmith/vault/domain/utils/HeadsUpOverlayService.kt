package com.androsmith.vault.domain.utils

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.androsmith.vault.data.VaultDatabase
import com.androsmith.vault.data.model.VaultContact
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.androsmith.vault.R
import com.androsmith.vault.ui.theme.VaultTheme
import com.androsmith.vault.ui.theme.green

@AndroidEntryPoint
class HeadsUpOverlayService : Service(), LifecycleOwner, SavedStateRegistryOwner {

    private var windowManager: WindowManager? = null


    private val _lifecycleRegistry = LifecycleRegistry(this)
    private val _savedStateRegistryController: SavedStateRegistryController =
        SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry =
        _savedStateRegistryController.savedStateRegistry
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
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

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
        val normalizedPhoneNumber =
            PhoneNumberUtils.normalizePhoneNumber(phoneNumber) ?: phoneNumber

        CoroutineScope(Dispatchers.IO).launch { // Background thread for database operation
            try {
                val contact =
                    database.vaultContactDao().getVaultContactByNumber(normalizedPhoneNumber)

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
                VaultCallNotificationCard(contact = contact,
onClose = { CoroutineScope(Dispatchers.Main).launch {
    stopSelf() // Error, stop the service
} }
                    )
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
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
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


@PreviewLightDark
@Composable
private fun VaultCallNotificationCardPreview() {
    val contact = VaultContact(
        name = "John",
        number = "+91 98765 43211",
        category = "Work",
        isHidden = false,
    )
    VaultTheme {

        VaultCallNotificationCard(contact, {})
    }
}

@Composable
fun VaultCallNotificationCard(contact: VaultContact, onClose: () -> Unit) {
  VaultTheme{  Card(
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.background
        )
        ,
        border = BorderStroke(1.dp,

            MaterialTheme.colorScheme.primary
            ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 80.dp)
            , elevation = CardDefaults.cardElevation(
            0.dp,
        ), shape = RoundedCornerShape(12.dp)
    ) {
        Column(
        ) {
            Row(modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.inversePrimary
                )
                .fillMaxWidth()
                .padding(

                    start = 20.dp,
                ),
//                .padding(top = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        painterResource(R.drawable.phone_calling_svgrepo_com),
                        contentDescription = "Call Icon",
                        tint =
                        MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Vault Contact", style = TextStyle(
                            fontSize = 20.sp,
                            color =
                                    MaterialTheme.colorScheme.onBackground,
                        )
                    )
                }
                IconButton(
                    onClick = onClose
                ){
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Close",
                        tint =
                                MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            HorizontalDivider(
                color =MaterialTheme.colorScheme.primary
            )
            Column(

                modifier = Modifier.padding( horizontal = 20.dp)
            ) {

                Spacer(modifier = Modifier.height(2.dp))



                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start ,modifier = Modifier.fillMaxWidth()) {


                    Text(
                    text = contact.name, style = TextStyle(
                        fontSize = 21.sp, //fontWeight = FontWeight.Medium
                    )
                )
                    Spacer(Modifier.width(16.dp))
                    contact.category?.let {
                        SuggestionChip(
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            onClick = { /* View Contact */ }, label =
                        {                            Text(it
                        , style = TextStyle
                                (
                                        color = MaterialTheme.colorScheme.primary
                                        )
                        )})

                }

            }
                Spacer(Modifier.height(2.dp))
                Text(text = "(${contact.number})", style =
                TextStyle(
                    fontSize = 16.sp,
    //                fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7F)
                )
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

        }
    }}
}

@Composable
fun OverlayContent(contact: VaultContact) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = contact.name, color = Color.White, fontSize = 16.sp
        )
        Text(
            text = contact.category ?: "Unknown Category", color = Color.Gray, fontSize = 12.sp
        )
    }
}
