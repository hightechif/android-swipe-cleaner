package com.hightechif.swipecleaner.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hightechif.swipecleaner.R

@Composable
fun MilestoneDialogComp(
    swipeCount: Int,
    trashCount: Int,
    onReviewTrash: () -> Unit,
    onEmptyTrash: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF252538),
        title = {
            Text(
                text = stringResource(R.string.milestone_title),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(R.string.milestone_desc, swipeCount),
                color = Color.LightGray,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                onClick = onReviewTrash
            ) { Text(stringResource(R.string.milestone_review_trash, trashCount), color = Color.White) }
        },
        dismissButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (trashCount > 0) {
                    TextButton(onClick = onEmptyTrash) {
                        Text(stringResource(R.string.action_empty_trash), color = Color(0xFFE91E63))
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.action_keep_swiping), color = Color.Gray)
                }
            }
        }
    )
}
