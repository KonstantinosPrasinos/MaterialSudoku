package com.example.multiplayersudoku.views.mainView

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.multiplayersudoku.classes.GameSettings
import com.example.multiplayersudoku.components.ConnectedButtonSelectionGroup.ConnectedSelectionGroup
import com.example.multiplayersudoku.components.ConnectedButtonSelectionGroup.ConnectedSelectionGroupOption
import com.example.multiplayersudoku.components.DifficultyPicker
import com.example.multiplayersudoku.components.UserIcon
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    onNavigateToSudoku: (gameSettings: GameSettings) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val viewModel: MainViewModel = hiltViewModel()
    val layoutDirection = LocalLayoutDirection.current

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showPlaySoloBottomSheet by remember { mutableStateOf(false) }

    var selectedDifficulty by remember { mutableStateOf(GameSettings.defaultDifficulty) }

    val mistakesOptions = (1..GameSettings.maxMistakes).map { it.toString() }
    var selectedMistakesOption by remember { mutableStateOf(mistakesOptions[1]) }

    val hintsOptions = (1..GameSettings.maxHints).map { it.toString() }
    var selectedHintsOption by remember { mutableStateOf(hintsOptions[0]) }

    val user by viewModel.currentUser.collectAsState()

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

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {},
                actions = {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 52.dp,
                            bottomStart = 52.dp
                        ),
                        tonalElevation = 3.dp
                    ) {
                        UserIcon(
                            photoUrl = if (user != null) user?.photoUrl.toString() else null,
                            size = 36.dp,
                            onClick = { onNavigateToProfile() },
                            modifier = Modifier.padding(8.dp)
                        )
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
            Text(
                "Material Sudoku",
                style = MaterialTheme.typography.displayMediumEmphasized,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showPlaySoloBottomSheet = true },
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = ButtonDefaults.MediumContainerHeight),
                        contentPadding = ButtonDefaults.MediumContentPadding
                    ) {
                        Text(
                            "Play Solo",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        enabled = false,
                        onClick = {},
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier
                            .weight(1.5f)
                            .heightIn(min = ButtonDefaults.MediumContainerHeight)
                            .padding(bottom = innerPadding.calculateBottomPadding()),
                        contentPadding = ButtonDefaults.MediumContentPadding
                    ) {
                        Text(
                            "Play versus (coming soon)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            if (showPlaySoloBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showPlaySoloBottomSheet = false
                    },
                    sheetState = sheetState,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(
                            "Difficulty:",
                            style = MaterialTheme.typography.titleLargeEmphasized,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        DifficultyPicker(
                            onDifficultySelected = { difficulty -> selectedDifficulty = difficulty },
                            selectedDifficulty = selectedDifficulty
                        )
                        Text(
                            "Max mistakes:",
                            style = MaterialTheme.typography.titleLargeEmphasized,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        ConnectedSelectionGroup(
                            items = mistakesOptions,
                            selectedItem = selectedMistakesOption,
                            onItemSelected = { selectedMistakesOption = it }, // State is updated here
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) { item, isSelected, shape -> // The shape is now provided here!

                            // The parent no longer knows or cares how the shape is made.
                            ConnectedSelectionGroupOption(
                                isSelected = isSelected,
                                onClick = { selectedMistakesOption = item },
                                shape = shape, // Simply use the provided shape
                                modifier = Modifier.weight(1f),
                            ) {
//                                Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                                Text(
                                    item,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant

                                )
                            }
                        }
                        Text(
                            "Max hints:",
                            style = MaterialTheme.typography.titleLargeEmphasized,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        ConnectedSelectionGroup(
                            items = hintsOptions,
                            selectedItem = selectedHintsOption,
                            onItemSelected = { selectedHintsOption = it }, // State is updated here
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) { item, isSelected, shape -> // The shape is now provided here!

                            // The parent no longer knows or cares how the shape is made.
                            ConnectedSelectionGroupOption(
                                isSelected = isSelected,
                                onClick = { selectedHintsOption = item },
                                shape = shape, // Simply use the provided shape
                                modifier = Modifier.weight(1f),
                            ) {
//                                Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                                Text(
                                    item,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant

                                )
                            }
                        }
                        Button(
                            onClick = { startSoloGame() },
                            shapes = ButtonDefaults.shapes(),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = ButtonDefaults.MediumContentPadding
                        ) {
                            Text(
                                "Play solo",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}