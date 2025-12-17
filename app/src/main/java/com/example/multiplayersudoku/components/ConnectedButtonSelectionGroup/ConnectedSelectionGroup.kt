package com.example.multiplayersudoku.components.ConnectedButtonSelectionGroup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> ConnectedSelectionGroup(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit, // It's good practice to have the callback here
    modifier: Modifier = Modifier,
    // Update the lambda to provide the calculated shape to the caller
    optionContent: @Composable RowScope.(item: T, isSelected: Boolean, shape: ToggleButtonShapes) -> Unit
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        items.forEachIndexed { index, item ->
            val isSelected = item == selectedItem

            // 1. Shape logic is correctly located here.
            val shape: ToggleButtonShapes = when (index) {
                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                items.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
            }

            // 2. Pass the calculated `shape` to the `optionContent` lambda.
            optionContent(item, isSelected, shape)
        }
    }
}