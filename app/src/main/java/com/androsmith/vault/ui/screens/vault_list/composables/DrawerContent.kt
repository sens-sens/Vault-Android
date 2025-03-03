package com.androsmith.vault.ui.screens.vault_list.composables

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androsmith.vault.R

@Composable
fun DrawerContent(
    performExport: (Uri) -> Unit,
    performImport: (Uri) -> Unit,
    saveCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val exportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
            uri?.let { performExport(it) }
        }

    // Launcher for importing
    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let { performImport(it) }
        }

    var showDialog by remember { mutableStateOf(false) }

    var defaultCategory by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(8.dp),
            onDismissRequest = { showDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Category")
                    IconButton(
                        onClick = { showDialog = false }


                    ) {
                        Icon(
                            painterResource(R.drawable.close),
                            contentDescription = "Close",
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            },
            text = {


                OutlinedTextField(
                    value = defaultCategory,
                    onValueChange = { defaultCategory = it },
                    label = { Text("") })

            },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        saveCategory(defaultCategory)
                        showDialog = false
                    }) { Text("Save") }
            },

            )

    }

    Column(
//        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(250.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(Modifier.height(80.dp))
        Image(
            painter = painterResource(R.drawable.ic_launcher_playstore),
            modifier = Modifier.size(64.dp)
                .clip(RoundedCornerShape(15.dp)),
            contentDescription = "App Icon"
        )
        Spacer(Modifier.height(40.dp))
        DrawerTile(
            text = "Export Contacts",
            icon = R.drawable.export_2_svgrepo_com,
            onPressed = { exportLauncher.launch("vault_contacts.json") }
        )
        DrawerTile(
            text = "Import Contacts",
            icon = R.drawable.import_1_svgrepo_com,
            onPressed = { importLauncher.launch(arrayOf("application/json")) }
        )
        DrawerTile(
            text = "Set Category",
            icon = R.drawable.config_svgrepo_com,
            onPressed = { showDialog = true }
        )
Spacer(Modifier.weight(1F))
        Text("v 1.0.0",
            style = TextStyle(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6F),
                fontSize = 14.sp
            )
            )
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
fun DrawerTile(
    text: String,
    onPressed: () -> Unit,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = true, onClick = onPressed)
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)

    ) {
        Row(
        ) {

//            Spacer(Modifier.width(8.dp))
            Icon(
                painterResource(icon),
                modifier = Modifier.size(20.dp),
                contentDescription = null
            )
            Spacer(Modifier.width(20.dp))
            Text(
                text
            )
        }
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05F)
        )
    }
}