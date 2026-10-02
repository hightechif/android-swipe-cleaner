package com.hightechif.swipecleaner.ui.component

import android.app.Activity
import android.content.IntentSender
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.hightechif.swipecleaner.domain.model.PendingSystemAction
import kotlinx.coroutines.flow.Flow

@Composable
fun TrashRequestEffectComp(
    trashEvents: Flow<PendingSystemAction>,
    onTrashCompleted: () -> Unit
) {
    val trashLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) onTrashCompleted()
    }

    LaunchedEffect(trashEvents) {
        trashEvents.collect { action ->
            (action.handle as? IntentSender)?.let { intentSender ->
                trashLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
        }
    }
}
