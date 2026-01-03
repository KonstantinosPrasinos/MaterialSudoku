package com.example.multiplayersudoku.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UserIcon(
    photoUrl: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    onClick: () -> Unit = {}
) {
    Surface(
        shadowElevation = if (photoUrl !== null) 4.dp else 0.dp,
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .aspectRatio(1f)
    ) {
        if (photoUrl !== null) {
            val highResPhotoUrl = photoUrl.replace("s96-c", "s400-c")
            AsyncImage(
                model = highResPhotoUrl,
                contentDescription = "Profile picture",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Account circle",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
