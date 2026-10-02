package com.hightechif.swipecleaner.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hightechif.swipecleaner.R
import com.hightechif.swipecleaner.domain.model.KeptPhoto
import com.hightechif.swipecleaner.ui.feature.kept.KeptAlbum

@Composable
fun KeptTabContentComp(
    keptPhotos: List<KeptPhoto>,
    keptAlbums: List<KeptAlbum>,
    onPhotoClick: (String) -> Unit,
    onPhotoLongClick: (String) -> Unit,
    onResetAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf(KeptViewMode.ALL_PHOTOS) }
    var selectedAlbumId by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(28.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.kept_title),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                KeptViewModeSelectorComp(
                    viewMode = viewMode,
                    onViewModeSelected = {
                        viewMode = it
                        selectedAlbumId = null
                    }
                )
            }
            if (keptPhotos.isNotEmpty()) {
                IconButton(onClick = onResetAll) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = stringResource(R.string.kept_cd_restore_all),
                        tint = Color(0xFFE91E63)
                    )
                }
            }
        }

        if (keptPhotos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.kept_empty_short),
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            KeptPhotosGridComp(
                keptPhotos = keptPhotos,
                keptAlbums = keptAlbums,
                viewMode = viewMode,
                selectedAlbumId = selectedAlbumId,
                onAlbumSelected = { selectedAlbumId = it },
                onPhotoClick = onPhotoClick,
                onPhotoLongClick = onPhotoLongClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
