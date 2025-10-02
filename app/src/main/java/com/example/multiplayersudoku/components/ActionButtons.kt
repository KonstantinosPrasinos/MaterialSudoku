package com.example.multiplayersudoku.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.multiplayersudoku.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ActionButtons(isEditing: Boolean, toggleEditing: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        FilledTonalIconButton(
            onClick = {},
            shapes = IconButtonDefaults.shapes(),
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Default.Undo, contentDescription = "")
        }
        FilledTonalIconButton(
            onClick = {},
            shapes = IconButtonDefaults.shapes(),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ink_eraser_24px),
                contentDescription = "",
            )
        }
        FilledTonalIconButton(
            onClick = {},
            shapes = IconButtonDefaults.shapes(),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = ""
            )
        }
        FilledTonalIconToggleButton(
            checked = isEditing,
            onCheckedChange = { toggleEditing() },
            shapes = IconButtonDefaults.toggleableShapes(),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = ""
            )
        }
    }
}