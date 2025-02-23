package com.androsmith.vault.ui.screens.add_contacts.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androsmith.vault.domain.model.Contact

@Composable
fun ContactTile(
    contact: Contact, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onCheckedChange(!isChecked) }
        .padding(
            vertical = 14.dp,
            horizontal = 16.dp
        ), verticalAlignment = Alignment.CenterVertically) {
        CircularCheckbox(
            checked = isChecked, onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = contact.name, style = TextStyle(fontSize = 18.sp)
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            Text(
                text = contact.phoneNumber, style = TextStyle(
                    fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5F)
                )
            )
        }
    }
}


@Composable
fun CircularCheckbox(
    modifier: Modifier = Modifier, checked: Boolean, onCheckedChange: (Boolean) -> Unit
) {

    val borderColor by animateColorAsState(
        if (checked) Color.Transparent else MaterialTheme.colorScheme.onBackground.copy(
            alpha = 0.2F
        )
    )
    val iconColor by animateColorAsState(
        if (checked) Color.White else Color.Transparent
    )
    val boxColor by animateColorAsState(if (checked) MaterialTheme.colorScheme.primary else Color.Transparent)
    Box(
        modifier = modifier
            .size(24.dp)
            .background(
                color = boxColor,
                shape = CircleShape
            )
            .border(
                shape = CircleShape,
                color = borderColor,
                width = 1.dp,
            )
    ) {

            IconButton(
                onClick =  { onCheckedChange(!checked) }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }

    }
}