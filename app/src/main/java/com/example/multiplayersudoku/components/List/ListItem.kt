package com.example.multiplayersudoku.components.List

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class ListItemOrder {
    FIRST,
    MIDDLE,
    LAST
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ListItem(order: ListItemOrder, content: @Composable () -> Unit, onClick: (() -> Unit)? = null) {
    Surface(
        shape = RoundedCornerShape(
            topStart = if (order == ListItemOrder.FIRST) 16.dp else 0.dp,
            topEnd = if (order == ListItemOrder.FIRST) 16.dp else 0.dp,
            bottomStart = if (order == ListItemOrder.LAST) 16.dp else 0.dp,
            bottomEnd = if (order == ListItemOrder.LAST) 16.dp else 0.dp
        ),
        tonalElevation = 2.dp
    ) {
        Box(modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier) {
            Box(
                modifier = Modifier.padding(12.dp)
            ) {
                content()
            }
        }
    }
}