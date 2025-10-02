package com.example.multiplayersudoku.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.multiplayersudoku.classes.SudokuTileData
import com.example.multiplayersudoku.utils.bottomBorder
import com.example.multiplayersudoku.utils.leftBorder
import com.example.multiplayersudoku.utils.rightBorder
import com.example.multiplayersudoku.utils.topBorder

@Composable
fun Fixed3x3Grid() {
    val items = (1..9).toList() // 9 items

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Divide the list of 9 items into 3 rows
        items.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Each row takes equal vertical space
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display the 3 items for the current row
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f) // Each cell takes equal horizontal space in the Row
                            .aspectRatio(1f) // Makes the cell a square
                            .padding(4.dp)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = item.toString())
                    }
                }
            }
        }
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
        isSelected || numberSelected -> MaterialTheme.colorScheme.onPrimary
        groupSelected -> MaterialTheme.colorScheme.onSecondary
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
        tileData.value?.let {
            Text(
                text = it.toString(),
                color = textColor,
                fontWeight = fontWeight,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize
            )
        }
        Fixed3x3Grid()
    }
}