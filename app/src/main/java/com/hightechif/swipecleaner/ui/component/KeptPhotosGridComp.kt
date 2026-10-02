package com.hightechif.swipecleaner.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

private const val GRID_COLUMNS = 3
private val AccentColor = Color(0xFF6C63FF)

@Composable
fun KeptPhotosGridComp(
    keptPhotos: List<KeptPhoto>,
    keptAlbums: List<KeptAlbum>,
    viewMode: KeptViewMode,
    selectedAlbumId: String?,
    onAlbumSelected: (String?) -> Unit,
    onPhotoClick: (String) -> Unit,
    onPhotoLongClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeAlbum = keptAlbums.find { it.id == selectedAlbumId }

    when {
        viewMode == KeptViewMode.ALL_PHOTOS -> PhotoGrid(
            uris = keptPhotos.map { it.uri },
            onPhotoClick = onPhotoClick,
            onPhotoLongClick = onPhotoLongClick,
            modifier = modifier
        )

        selectedAlbumId == null -> LazyVerticalGrid(
            columns = GridCells.Fixed(GRID_COLUMNS),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(keptAlbums, key = { it.id }) { album ->
                KeptAlbumCardComp(album = album, onClick = { onAlbumSelected(album.id) })
            }
        }

        activeAlbum != null -> Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAlbumSelected(null) }
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.action_back),
                    tint = AccentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.kept_back_to_albums, activeAlbum.name),
                    color = AccentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            PhotoGrid(
                uris = activeAlbum.photos.map { it.uri },
                onPhotoClick = onPhotoClick,
                onPhotoLongClick = onPhotoLongClick
            )
        }
    }
}

@Composable
private fun PhotoGrid(
    uris: List<String>,
    onPhotoClick: (String) -> Unit,
    onPhotoLongClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMNS),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(uris, key = { it }) { uri ->
            KeptPhotoGridItemComp(
                uri = uri,
                onClick = { onPhotoClick(uri) },
                onLongClick = { onPhotoLongClick(uri) }
            )
        }
    }
}
