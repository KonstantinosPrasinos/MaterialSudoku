package com.example.multiplayersudoku.views.SudokuView

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.multiplayersudoku.R
import com.example.multiplayersudoku.components.UserIcon
import com.example.multiplayersudoku.ui.theme.extendedColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun MultiplayerProgressBar(
    player1PhotoUrl: String? = null,
    player2PhotoUrl: String? = null,
    player1Percentage: Float = 1.0f,
    player2Percentage: Float = 0.0f
) {
    val density = LocalDensity.current

    val p1Offset = remember { Animatable(0f) }
    val shapeScale = remember { Animatable(1f) }
    val crownOffset = remember { Animatable(0f) }

    val defaultSpatial = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    val fastSpatial = MaterialTheme.motionScheme.fastSpatialSpec<Float>()

    LaunchedEffect(player1Percentage, player2Percentage) {
        if (player1Percentage < 1.0f) return@LaunchedEffect
        val targetCrownOffset = with(density) { (-24).dp.toPx() }
        val targetP1Offset = with(density) { (38).dp.toPx() }

        launch {
            p1Offset.animateTo(targetP1Offset, animationSpec = defaultSpatial)
        }
        launch {
            Log.d("MultiplayerProgressBar", "Scaling up")
            shapeScale.animateTo(0f, animationSpec = defaultSpatial)
        }

        delay(100)

        launch {
            crownOffset.animateTo(targetCrownOffset, animationSpec = defaultSpatial)
        }
    }

    return Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .weight(1f)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .widthIn(min = 40.dp)
                    .fillMaxWidth(player1Percentage)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(100))
            ) {}
            UserIcon(
                photoUrl = player1PhotoUrl,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterEnd)
                    .offset {
                        IntOffset(x = p1Offset.value.roundToInt(), y = 0)
                    }
            )
        }
        Box(
            modifier = Modifier
                .size(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = MaterialShapes.Pentagon.toShape(),
                    )
                    .size(32.dp * shapeScale.value)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_chess_queen),
                contentDescription = "Crown",
                tint = MaterialTheme.extendedColors.win,
                modifier = Modifier
                    .size(20.dp)
                    .offset {
                        IntOffset(x = 0, y = crownOffset.value.roundToInt())
                    }
            )
        }
        Box(
            Modifier
                .weight(1f)
                .clip(RoundedCornerShape(100))
        ) {
            Surface(
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .fillMaxWidth(player2Percentage)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(100))
                    .align(Alignment.CenterEnd)
            ) {
                Box() {
                    UserIcon(
                        photoUrl = player2PhotoUrl,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.CenterStart)
                    )
                }
            }
        }
    }
}