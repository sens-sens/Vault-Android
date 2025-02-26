package com.androsmith.vault.ui.screens.vault_list.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.androsmith.vault.R
import com.androsmith.vault.data.model.VaultContact

@Composable
fun EditContactDialog(
    contact: VaultContact,
    onDismiss: () -> Unit,
    onSave: (VaultContact) -> Unit
) {
    var editedName by remember { mutableStateOf(contact.name) }
    var editedCategory by remember { mutableStateOf(contact.category ?: "") }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(8.dp),
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Edit Contact")
                IconButton(
                    onClick = onDismiss


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
            Column {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Name") })

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editedCategory,
                    onValueChange = { editedCategory = it },
                    label = { Text("Category") })
            }
        },
        confirmButton = {
            Button(
                shape = RoundedCornerShape(8.dp),
                onClick = {
                val updatedContact = contact.copy(name = editedName, category = editedCategory)
                onSave(updatedContact)
            }) { Text("Save") }
        },

    )
}