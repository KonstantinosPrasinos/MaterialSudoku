package com.example.multiplayersudoku.views.SudokuView

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.multiplayersudoku.classes.SudokuTileData
import com.example.multiplayersudoku.utils.bottomBorder
import com.example.multiplayersudoku.utils.leftBorder
import com.example.multiplayersudoku.utils.rightBorder
import com.example.multiplayersudoku.utils.topBorder

@Composable
fun Fixed3x3Grid(notedNumbers: MutableList<Int>, textColor: Color) {
    val items = (1..9).toList() // 9 items

    Column {
        items.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Each row fills the width
                    .weight(1f),    // Each row takes 1/3 of the vertical space
                horizontalArrangement = Arrangement.SpaceAround // Distribute cells horizontally
            ) {
                rowItems.forEach { item ->
                    GridCell(
                        item = item,
                        isNoted = notedNumbers.contains(item),
                        textColor = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.GridCell(item: Int, isNoted: Boolean = false, textColor: Color) {
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isNoted) textColor else Color.Transparent
        )
    }
}

@Composable
fun SudokuTile(
    tileData: SudokuTileData,
    modifier: Modifier = Modifier,
    selectedTileIndices: List<Int?>,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    selectedNumber: Int?,
    isPaused: Boolean = false,
) {
    // Configure the background color
    val isSelected =
        (selectedTileIndices[0] == tileData.rowIndex && selectedTileIndices[1] == tileData.colIndex)
    val groupSelected =
        (selectedTileIndices[0] == tileData.rowIndex || selectedTileIndices[1] == tileData.colIndex)
    val numberSelected = selectedNumber != null && selectedNumber == tileData.value;

    val color = when {
        isSelected || numberSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        groupSelected -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    // Configure the text color
    val textColor = when {
        tileData.value != null && tileData.isMistake -> MaterialTheme.colorScheme.error
        isSelected || numberSelected -> MaterialTheme.colorScheme.onPrimary
        groupSelected -> MaterialTheme.colorScheme.onSecondary
        tileData.isEditable && tileData.value != null -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val fontWeight = when {
        tileData.isEditable -> FontWeight.Normal
        else -> FontWeight.SemiBold
    }

    // Add the borders
    val thinBorderWidth = 0.5.dp;
    val thickBorderWidth = 1.dp;
    val thinBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.37f)
    val thickBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.67f)

    var borderModifier = if (tileData.rowIndex != 0) {
        if (tileData.rowIndex?.rem(3) == 0) {
            Modifier.topBorder(thickBorderWidth, thickBorderColor)
        } else {
            Modifier.topBorder(thinBorderWidth, thinBorderColor)
        }
    } else {
        Modifier
    }

    if (tileData.rowIndex != 8) {
        borderModifier = if (tileData.rowIndex?.rem(3) == 2) {
            borderModifier.bottomBorder(
                thickBorderWidth,
                thickBorderColor
            )
        } else {
            borderModifier.bottomBorder(
                thinBorderWidth,
                thinBorderColor
            )
        }
    }

    if (tileData.colIndex != 0) {
        borderModifier = if (tileData.colIndex?.rem(3) == 0) {
            borderModifier.leftBorder(
                thickBorderWidth,
                thickBorderColor,
            )
        } else {
            borderModifier.leftBorder(
                thinBorderWidth,
                thinBorderColor,
            )
        }
    }

    if (tileData.colIndex != 8) {
        borderModifier = if (tileData.colIndex?.rem(3) == 2) {
            borderModifier.rightBorder(thickBorderWidth, thickBorderColor)
        } else {
            borderModifier.rightBorder(thinBorderWidth, thinBorderColor)
        }
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .background(color)
            .then(borderModifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),


        contentAlignment = Alignment.Center
    ) {
        if (tileData.value == null) Fixed3x3Grid(
            notedNumbers = tileData.notes,
            textColor = textColor
        )
        if (!isPaused && tileData.value != null) Text(
            text = tileData.value.toString(),
            color = textColor,
            fontWeight = fontWeight,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize
        )
    }
}