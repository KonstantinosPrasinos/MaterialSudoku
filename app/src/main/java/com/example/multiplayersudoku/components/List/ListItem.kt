package com.example.multiplayersudoku.components.List

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
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
fun ListItem(order: ListItemOrder, content: @Composable () -> Unit, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(
            topStart = if (order == ListItemOrder.FIRST) 16.dp else 0.dp,
            topEnd = if (order == ListItemOrder.FIRST) 16.dp else 0.dp,
            bottomStart = if (order == ListItemOrder.LAST) 16.dp else 0.dp,
            bottomEnd = if (order == ListItemOrder.LAST) 16.dp else 0.dp
        ),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Box(
            modifier = Modifier.padding(10.dp)
        ) {
            content()
        }
    }
}