package com.hightechif.swipecleaner.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hightechif.swipecleaner.R

@Composable
fun ConfirmDialogComp(
    title: String,
    message: String,
    confirmLabel: String,
    confirmColor: Color,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF252538),
        title = {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(text = message, color = Color.LightGray, fontSize = 14.sp)
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = confirmColor),
                onClick = onConfirm
            ) { Text(confirmLabel, color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel), color = Color.Gray)
            }
        }
    )
}
