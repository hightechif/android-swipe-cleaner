package com.hightechif.swipecleaner.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hightechif.swipecleaner.R
import com.hightechif.swipecleaner.domain.model.KeptPhoto
import com.hightechif.swipecleaner.ui.feature.kept.KeptAlbum

enum class KeptViewMode { ALL_PHOTOS, ALBUMS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeptTabContentComp(
    keptPhotos: List<KeptPhoto>,
    keptAlbums: List<KeptAlbum>,
    onPhotoClick: (String) -> Unit,
    onPhotoLongClick: (String) -> Unit,
    onResetAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var keptViewMode by remember { mutableStateOf(KeptViewMode.ALL_PHOTOS) }
    var selectedAlbumId by remember { mutableStateOf<String?>(null) }
    var showViewModeDropdown by remember { mutableStateOf(false) }

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
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF252538))
                            .clickable { showViewModeDropdown = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (keptViewMode == KeptViewMode.ALL_PHOTOS) stringResource(R.string.kept_view_all) else stringResource(R.string.kept_view_albums),
                            color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = stringResource(R.string.kept_cd_select_view_mode),
                            tint = Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showViewModeDropdown,
                        onDismissRequest = { showViewModeDropdown = false },
                        modifier = Modifier.background(Color(0xFF252538))
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(R.string.kept_view_all),
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            },
                            onClick = {
                                keptViewMode = KeptViewMode.ALL_PHOTOS; selectedAlbumId =
                                null; showViewModeDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(R.string.kept_view_albums),
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            },
                            onClick = {
                                keptViewMode = KeptViewMode.ALBUMS; selectedAlbumId =
                                null; showViewModeDropdown = false
                            }
                        )
                    }
                }
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
            Box(modifier = Modifier
                .fillMaxSize()
                .weight(1f), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.kept_empty_short), color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            when (keptViewMode) {
                KeptViewMode.ALL_PHOTOS -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(keptPhotos, key = { it.uri }) { photo ->
                            KeptPhotoGridItemComp(
                                uri = photo.uri,
                                onClick = { onPhotoClick(photo.uri) },
                                onLongClick = { onPhotoLongClick(photo.uri) })
                        }
                    }
                }

                KeptViewMode.ALBUMS -> {
                    if (selectedAlbumId == null) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentPadding = PaddingValues(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(keptAlbums, key = { it.id }) { album ->
                                KeptAlbumCardComp(
                                    album = album,
                                    onClick = { selectedAlbumId = album.id })
                            }
                        }
                    } else {
                        val activeAlbum = keptAlbums.find { it.id == selectedAlbumId }
                        if (activeAlbum != null) {
                            Column(modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedAlbumId = null }
                                        .padding(horizontal = 24.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = stringResource(R.string.action_back),
                                        tint = Color(0xFF6C63FF),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.kept_back_to_albums, activeAlbum.name),
                                        color = Color(0xFF6C63FF),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(activeAlbum.photos, key = { it.uri }) { photo ->
                                        KeptPhotoGridItemComp(
                                            uri = photo.uri,
                                            onClick = { onPhotoClick(photo.uri) },
                                            onLongClick = { onPhotoLongClick(photo.uri) })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
