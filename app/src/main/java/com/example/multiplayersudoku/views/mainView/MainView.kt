package com.example.multiplayersudoku.views.mainView

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.multiplayersudoku.R
import com.example.multiplayersudoku.classes.GameSettings
import com.example.multiplayersudoku.classes.StatisticsUiState
import com.example.multiplayersudoku.components.GameSettingsBottomSheet
import com.example.multiplayersudoku.components.SignInModal
import com.example.multiplayersudoku.components.UserIcon
import com.example.multiplayersudoku.ui.theme.FredokaFamily
import com.example.multiplayersudoku.ui.theme.extendedColors
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

@Composable
fun MainView(
    onNavigateToSudoku: (gameSettings: GameSettings) -> Unit,
    onNavigateToJoinRoom: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: MainViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()
    val statisticsUiState by viewModel.statisticsUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.init(onNavigateToJoinRoom)
    }

    MainViewContent(
        user = user,
        statisticsUiState = statisticsUiState,
        showLoginModal = viewModel.showLoginModal,
        onNavigateToSudoku = onNavigateToSudoku,
        onNavigateToJoinRoom = viewModel::navigateToJoinRoom,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToStatistics = onNavigateToStatistics,
        onCloseLoginModal = viewModel::closeLoginModal,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainViewContent(
    user: FirebaseUser?,
    statisticsUiState: StatisticsUiState,
    showLoginModal: Boolean,
    onNavigateToSudoku: (gameSettings: GameSettings) -> Unit,
    onNavigateToJoinRoom: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onCloseLoginModal: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val layoutDirection = LocalLayoutDirection.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val loginSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showPlaySoloBottomSheet by remember { mutableStateOf(false) }

    var selectedDifficulty by remember { mutableStateOf(GameSettings.defaultDifficulty) }

    val mistakesOptions = (1..GameSettings.maxMistakes).map { it.toString() }
    var selectedMistakesOption by remember { mutableStateOf(mistakesOptions[1]) }

    val hintsOptions = (1..GameSettings.maxHints).map { it.toString() }
    var selectedHintsOption by remember { mutableStateOf(hintsOptions[0]) }

    val completionPercentage = remember(statisticsUiState) {
        if (statisticsUiState.totalGames > 0) {
            String.format("%.1f%%", (statisticsUiState.completedGames.toFloat() / statisticsUiState.totalGames * 100))
        } else {
            "0.0%"
        }
    }

    fun formatDuration(seconds: Long?): String {
        if (seconds == null || seconds <= 0) return "--:--"
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) {
            String.format("%02d:%02d:%02d", h, m, s)
        } else {
            String.format("%02d:%02d", m, s)
        }
    }

    fun startSoloGame() {
        val gameSettings = GameSettings()
        gameSettings.mistakes = selectedMistakesOption.toInt()
        gameSettings.hints = selectedHintsOption.toInt()
        gameSettings.difficulty = selectedDifficulty

        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                showPlaySoloBottomSheet = false
                onNavigateToSudoku(gameSettings)
            }
        }
    }

    fun closeLoginBottomSheet() {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onCloseLoginModal()
            }
        }
    }

    with(sharedTransitionScope) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text(
                            "Logic Arena",
                            style = TextStyle(
                                fontFamily = FredokaFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            ),
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    },
                    actions = {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 52.dp,
                                bottomStart = 52.dp
                            ),
                            tonalElevation = 3.dp
                        ) {
                            Box(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                UserIcon(
                                    photoUrl = if (user != null) user.photoUrl.toString() else null,
                                    size = 36.dp,
                                    onClick = { onNavigateToProfile() },
                                    modifier = Modifier
                                        .sharedElement(
                                            rememberSharedContentState(key = "account-circle"),
                                            animatedVisibilityScope = animatedVisibilityScope
                                        )
                                )
                            }
                        }
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        start = innerPadding.calculateStartPadding(layoutDirection),
                        end = innerPadding.calculateEndPadding(layoutDirection)
                    )
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Welcome back,",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Text(
                            text = if (user != null) user.displayName ?: "Player" else "Player",
                            style = TextStyle(
                                fontFamily = FredokaFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.headlineMedium.fontSize
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        // 1. Get the current screen density
                        val density = LocalDensity.current

                        // 2. Fetch the typography font size (which is in sp)
                        val baseFontSize = MaterialTheme.typography.titleMedium.fontSize

                        // 3. Multiply by 20 and convert it to Dp
                        val twentyEmDp = with(density) {
                            (baseFontSize * 10).toDp()
                        }
                        StreakSurface(
                            surfaceColor = MaterialTheme.extendedColors.redBg,
                            surfaceVariantColor = MaterialTheme.extendedColors.redIconBg,
                            onSurfaceColor = MaterialTheme.extendedColors.redText,
                            title = "Win streak",
                            value = statisticsUiState.winStreak.toString(),
                            onClick = { onNavigateToStatistics() },
                            modifier = Modifier
                                .width(twentyEmDp),
                            iconDescription = "Streak icon",
                            icon = Icons.Outlined.LocalFireDepartment,
                        )
                        StreakSurface(
                            surfaceColor = MaterialTheme.extendedColors.purpleBg,
                            surfaceVariantColor = MaterialTheme.extendedColors.purpleIconBg,
                            onSurfaceColor = MaterialTheme.extendedColors.purpleText,
                            title = "Completion",
                            value = completionPercentage,
                            onClick = { onNavigateToStatistics() },
                            modifier = Modifier
                                .width(twentyEmDp),
                            iconDescription = "Completion percentage icon",
                            painter = painterResource(id = R.drawable.ic_chess_queen),
                        )
                        StreakSurface(
                            surfaceColor = MaterialTheme.extendedColors.tealBg,
                            surfaceVariantColor = MaterialTheme.extendedColors.tealIconBg,
                            onSurfaceColor = MaterialTheme.extendedColors.tealText,
                            title = "Best time",
                            value = formatDuration(statisticsUiState.bestTime),
                            onClick = { onNavigateToStatistics() },
                            modifier = Modifier
                                .width(twentyEmDp),
                            iconDescription = "Timer icon",
                            icon = Icons.Outlined.Timer,
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.width(IntrinsicSize.Max),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showPlaySoloBottomSheet = true },
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = ButtonDefaults.MediumContainerHeight),
                        contentPadding = ButtonDefaults.MediumContentPadding
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Solo icon"
                            )
                            Text(
                                "Play Solo",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = onNavigateToJoinRoom,
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier
                            .heightIn(min = ButtonDefaults.MediumContainerHeight)
                            .padding(bottom = innerPadding.calculateBottomPadding()),
                        contentPadding = ButtonDefaults.MediumContentPadding
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = "Versus icon"
                            )
                            Text(
                                "Play versus",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(0.5f))
                if (showPlaySoloBottomSheet) {
                    GameSettingsBottomSheet(
                        sheetState = sheetState,
                        toggleVisibility = { showPlaySoloBottomSheet = false },
                        setDifficulty = { difficulty -> selectedDifficulty = difficulty },
                        confirmAction = { startSoloGame() },
                        selectedDifficulty = selectedDifficulty,
                        selectedMistakesOption = selectedMistakesOption,
                        setSelectedMistakesOption = { selectedMistakesOption = it.toString() },
                        selectedHintsOption = selectedHintsOption,
                        setSelectedHintsOption = { selectedHintsOption = it.toString() },
                        confirmButtonText = "Play solo",
                    )
                }

                if (showLoginModal) {
                    SignInModal(onDismissRequest = ::closeLoginBottomSheet, loginSheetState, snackbarHostState)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StreakSurface(
    surfaceColor: Color,
    surfaceVariantColor: Color,
    onSurfaceColor: Color,
    title: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconDescription: String,
    icon: ImageVector = Icons.Outlined.LocalFireDepartment,
    painter: Painter? = null
) {
    Surface(
        onClick = onClick,
        color = surfaceColor,
        shape = RoundedCornerShape(100),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Surface(
                color = surfaceVariantColor,
                shape = RoundedCornerShape(100),
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .padding(4.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (painter == null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = iconDescription,
                            tint = onSurfaceColor
                        )
                    } else {
                        Icon(
                            painter,
                            contentDescription = iconDescription,
                            tint = onSurfaceColor
                        )
                    }

                }
            }
            Column(
                modifier = Modifier.padding(end = 12.dp, top = 4.dp, bottom = 4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                        .copy(color = onSurfaceColor.copy(alpha = 0.67f))
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMediumEmphasized
                        .copy(color = onSurfaceColor)
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    MaterialTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                MainViewContent(
                    user = null,
                    statisticsUiState = StatisticsUiState(
                        averageDuration = 120,
                        bestTime = 95,
                        totalGames = 10,
                        completedGames = 8,
                        totalDuration = 1200,
                        winStreak = 3,
                        isLoading = false
                    ),
                    showLoginModal = false,
                    onNavigateToSudoku = {},
                    onNavigateToJoinRoom = {},
                    onNavigateToProfile = {},
                    onNavigateToStatistics = {},
                    onCloseLoginModal = {},
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedVisibility
                )
            }
        }
    }
}