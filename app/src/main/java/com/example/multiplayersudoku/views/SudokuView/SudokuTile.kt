package com.example.multiplayersudoku.views.SudokuView

import android.os.Debug
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.multiplayersudoku.classes.SudokuTileData
import com.example.multiplayersudoku.utils.bottomBorder
import com.example.multiplayersudoku.utils.leftBorder
import com.example.multiplayersudoku.utils.rightBorder
import com.example.multiplayersudoku.utils.topBorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                        textColor = textColor.copy(alpha = 0.6f)
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpandingBox(isVisible: Boolean) {
    // 1. Create the animatable scale value
    val scale = remember { Animatable(0f) }

    // 2. Watch for the isVisible toggle
    LaunchedEffect(isVisible) {
        if (isVisible) {
            // Expand with a bounce
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
            )
            // Wait a moment (optional)
            delay(1000)
            // Collapse back down
            scale.animateTo(targetValue = 0f)
        }
    }

    Box(
        modifier = Modifier
            .graphicsLayer(scaleX = scale.value, scaleY = scale.value)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondaryContainer, MaterialShapes.SoftBurst.toShape()),
        contentAlignment = Alignment.Center,
        content = {}
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SudokuTile(
    tileData: SudokuTileData,
    modifier: Modifier = Modifier,
    selectedTileIndices: List<Int?>,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    selectedNumber: Int?,
    isPaused: Boolean = false,
    isCompleted: Boolean = true,
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

    val textColor = when {
        tileData.value != null && tileData.isMistake -> MaterialTheme.colorScheme.error
        isSelected || numberSelected -> MaterialTheme.colorScheme.onPrimary
        tileData.isEditable && tileData.value != null -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
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
        ExpandingBox(isVisible = tileData.isCompleted)
        if (!isPaused && tileData.value != null) Text(
            text = tileData.value.toString(),
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize
        )
    }
}