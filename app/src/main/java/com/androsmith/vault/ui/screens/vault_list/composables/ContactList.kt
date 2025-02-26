package com.androsmith.vault.ui.screens.vault_list.composables

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.androsmith.vault.data.model.VaultContact
import kotlin.collections.component1
import kotlin.collections.component2
import com.androsmith.vault.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.androsmith.vault.ui.theme.actionBlue
import com.androsmith.vault.ui.theme.actionGreen
import com.androsmith.vault.ui.theme.actionRed


@Composable
fun ContactList(
    contacts: List<VaultContact>,
    onContactDelete: (VaultContact) -> Unit,
    onContactEdit: (VaultContact) -> Unit,
    modifier: Modifier = Modifier
) {


    val context = LocalContext.current

    val groupedContacts = contacts.groupBy { it.name.first().uppercaseChar() }
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
    ) {
        groupedContacts.forEach { (letter, contactsForLetter) ->
            item {
                LetterHeader(letter)
            }
            items(contactsForLetter,
                key = { it.number }) { contact ->  // Use key for better performance

                SwipeableItemWithActions(
                    actions = {
                        ActionIcon(
                            onClick = {onContactEdit(contact)},
                            backgroundColor = actionBlue.copy(alpha = .3F),
                            icon = painterResource(R.drawable.edit),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = .7f)
                        )
                        ActionIcon(
                            onClick = {
                              try {
                                  val uri = Uri.parse("tel:${contact.number}")
                                  val intent = Intent(Intent.ACTION_DIAL, uri)
                                  context.startActivity(intent)
                              } catch (e: Exception){
                                  Log.e("Call", e.toString())
                              }

                            },
                            backgroundColor = actionGreen.copy(alpha = .3F),
                            icon = painterResource(R.drawable.phone),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = .7f)
                        )
                        ActionIcon(
                            onClick = {
                                onContactDelete(contact)
                            },
                            backgroundColor = actionRed.copy(alpha = .3F),
                            icon = painterResource(R.drawable.delete),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = .7f)
                        )
                    }
                ) {
                    VaultContactTile(
                        contact = contact,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    )
                }


            }
        }
    }
}

@Composable
fun LetterHeader(
    character: Char,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.width(20.dp)
        )
        Spacer(
            modifier = Modifier.width(10.dp)
        )
        Text(
            "$character",
            style = TextStyle(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6F)
            )
//            modifier = Modifier.weight(1F)
        )
        Spacer(
            modifier = Modifier.width(10.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1F)
        )
    }
}

