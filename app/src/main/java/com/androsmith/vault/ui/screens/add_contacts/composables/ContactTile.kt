package com.androsmith.vault.ui.screens.add_contacts.composables
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androsmith.vault.domain.model.Contact

@Composable
fun ContactTile(
    contact: Contact,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(
                vertical = 12.dp,
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
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
                text = contact.phoneNumber,
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
}


//@Composable
//fun CircularCheckbox(
//    modifier: Modifier = Modifier, checked: Boolean, onCheckedChange: (Boolean) -> Unit
//) {
//    Box(
//        modifier = modifier
//            .size(24.dp)
//            .background(
//                color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
//                    alpha = 0.6f
//                ), shape = CircleShape
//            )
//            .clickable { onCheckedChange(!checked) }, contentAlignment = Alignment.Center
//    ) {
//        if (checked) {
//            Icon(
//                imageVector = Icons.Default.Check,
//                contentDescription = null,
//                tint = Color.White,
//                modifier = Modifier.size(16.dp)
//            )
//        }
//    }
//}