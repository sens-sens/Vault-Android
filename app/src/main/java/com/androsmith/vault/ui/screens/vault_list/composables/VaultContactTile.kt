package com.androsmith.vault.ui.screens.vault_list.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue

import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androsmith.vault.data.model.VaultContact


@Composable
fun VaultContactTile(
    contact: VaultContact,
    modifier: Modifier = Modifier,
) {


    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 4.dp,
                vertical = 12.dp,
            ),
    ) {
        Column(

        ) {
            Text(
                text = contact.name,
                style = TextStyle(
                    fontSize = 18.sp
                )
            )
            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )
            Text(
                text = contact.number,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme
                        .colorScheme
                        .onSurface
                        .copy(alpha = 0.5F)
                )
            )
        }

        FilterChip(
            label = {Text(contact.category?:"Default")},
            selected = true,
            onClick = {}
        )
    }
}
