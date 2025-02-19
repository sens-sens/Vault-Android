package com.androsmith.vault.ui.screens.vault_list.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androsmith.vault.data.model.VaultContact
import com.androsmith.vault.domain.model.Contact


@Composable
fun VaultContactTile(
    contact: VaultContact,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 4.dp,
                vertical = 12.dp,
            ),
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
}
