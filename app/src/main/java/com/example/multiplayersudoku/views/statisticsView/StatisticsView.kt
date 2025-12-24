package com.example.multiplayersudoku.views.statisticsView

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.multiplayersudoku.classes.Difficulty
import com.example.multiplayersudoku.components.List.List
import com.example.multiplayersudoku.components.List.ListItem
import com.example.multiplayersudoku.components.List.ListItemOrder
import com.example.multiplayersudoku.utils.capitalizeFirstLetter
import com.example.multiplayersudoku.views.SudokuView.formatTime
import kotlinx.coroutines.launch

enum class StatisticsDestination {
    ALL, EASY, MEDIUM, HARD
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StatisticsView(onBack: () -> Unit) {
    // Observe the state from the ViewModel
    val viewModel: StatisticsViewModel = hiltViewModel()

    val scope = rememberCoroutineScope()
    val startDestination = StatisticsDestination.ALL
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
    val pagerState = rememberPagerState(pageCount = { StatisticsDestination.entries.size })

    fun setPage(index: Int) {
        selectedDestination = index

        val difficulty = when (selectedDestination) {
            StatisticsDestination.EASY.ordinal -> Difficulty.EASY
            StatisticsDestination.MEDIUM.ordinal -> Difficulty.MEDIUM
            StatisticsDestination.HARD.ordinal -> Difficulty.HARD
            else -> null
        }
        viewModel.setDifficulty(difficulty)
    }

    LaunchedEffect(pagerState.currentPage) {
        setPage(pagerState.currentPage)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    TooltipBox(
                        positionProvider =
                            TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Above
                            ),
                        tooltip = { PlainTooltip { Text("Menu") } },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(onClick = { onBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            HorizontalFloatingToolbar(
                expanded = true,
                expandedShadowElevation = 2.dp
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatisticsDestination.entries.forEachIndexed { index, destination ->
                        Surface(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            shape = RoundedCornerShape(100.dp),
                            color = if (index == selectedDestination) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                        ) {
                            Text(
                                capitalizeFirstLetter(destination.name),
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) {
                StatisticsColumn(
                    averageDuration = uiState.averageDuration,
                    bestTime = uiState.bestTime,
                    totalGames = uiState.totalGames,
                    completedGames = uiState.completedGames,
                    totalDuration = uiState.totalDuration
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StatisticsColumn(
    averageDuration: Long?,
    bestTime: Long?,
    totalGames: Int,
    completedGames: Int,
    totalDuration: Long?
) {
    val completionPercentage = if (totalGames > 0) (completedGames.toFloat() / totalGames.toFloat()) * 100 else 0f
    // 5. Display the data
    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Activity", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 12.dp))
            List {
                ListItem(
                    order = ListItemOrder.FIRST,
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Games started", style = MaterialTheme.typography.bodyLargeEmphasized)
                            Text(totalGames.toString(), style = MaterialTheme.typography.bodyLargeEmphasized)
                        }
                    }
                )
                ListItem(
                    order = ListItemOrder.MIDDLE,
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Games completed", style = MaterialTheme.typography.bodyLargeEmphasized)
                            Text(completedGames.toString(), style = MaterialTheme.typography.bodyLargeEmphasized)
                        }
                    }
                )
                ListItem(
                    order = ListItemOrder.LAST,
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Completion percentage", style = MaterialTheme.typography.bodyLargeEmphasized)
                            Text(
                                "%.1f%%".format(completionPercentage),
                                style = MaterialTheme.typography.bodyLargeEmphasized
                            )
                        }
                    }
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Time", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 12.dp))
            List {
                ListItem(
                    order = ListItemOrder.FIRST,
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Best time", style = MaterialTheme.typography.bodyLargeEmphasized)
                            Text(
                                formatTime(bestTime?.toInt() ?: 0),
                                style = MaterialTheme.typography.bodyLargeEmphasized
                            )
                        }
                    }
                )
                ListItem(
                    order = ListItemOrder.MIDDLE,
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Average time", style = MaterialTheme.typography.bodyLargeEmphasized)
                            Text(
                                formatTime(averageDuration?.toInt() ?: 0),
                                style = MaterialTheme.typography.bodyLargeEmphasized
                            )
                        }
                    }
                )
                ListItem(
                    order = ListItemOrder.LAST,
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total time played", style = MaterialTheme.typography.bodyLargeEmphasized)
                            Text(
                                formatTime(totalDuration?.toInt() ?: 0),
                                style = MaterialTheme.typography.bodyLargeEmphasized
                            )
                        }
                    }
                )
            }
        }
    }
}