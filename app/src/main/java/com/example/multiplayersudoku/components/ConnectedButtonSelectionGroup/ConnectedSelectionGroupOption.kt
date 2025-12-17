package com.example.multiplayersudoku.components.ConnectedButtonSelectionGroup

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConnectedSelectionGroupOption(
    isSelected: Boolean,
    onClick: () -> Unit,
    shape: ToggleButtonShapes,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ToggleButton(
        colors = ToggleButtonDefaults.tonalToggleButtonColors(),
        checked = isSelected,
        onCheckedChange = { onClick() },
        modifier = modifier.semantics { role = Role.RadioButton },
        shapes = shape,
    ) {
        content()
    }
}