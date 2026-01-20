package com.example.multiplayersudoku.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.multiplayersudoku.classes.Difficulty
import com.example.multiplayersudoku.classes.GameSettings
import com.example.multiplayersudoku.components.ConnectedButtonSelectionGroup.ConnectedSelectionGroup
import com.example.multiplayersudoku.components.ConnectedButtonSelectionGroup.ConnectedSelectionGroupOption

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GameSettingsBottomSheet(
    sheetState: SheetState,
    toggleVisibility: () -> Unit,
    setDifficulty: (Difficulty) -> Unit,
    selectedDifficulty: Difficulty,
    selectedMistakesOption: String,
    setSelectedMistakesOption: (String) -> Unit,
    selectedHintsOption: String,
    setSelectedHintsOption: (String) -> Unit,
    confirmButtonText: String,
    confirmAction: () -> Unit,
) {
    val mistakesOptions = (1..GameSettings.maxMistakes).map { it.toString() }
    val hintsOptions = (1..GameSettings.maxHints).map { it.toString() }

    ModalBottomSheet(
        onDismissRequest = {
            toggleVisibility()
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
                onDifficultySelected = { difficulty -> setDifficulty(difficulty) },
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
                onItemSelected = { option -> setSelectedMistakesOption(option) }, // State is updated here
                modifier = Modifier.padding(horizontal = 8.dp)
            ) { item, isSelected, shape -> // The shape is now provided here!

                // The parent no longer knows or cares how the shape is made.
                ConnectedSelectionGroupOption(
                    isSelected = isSelected,
                    onClick = { setSelectedMistakesOption(item) },
                    shape = shape, // Simply use the provided shape
                    modifier = Modifier.weight(1f),
                ) {
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
                onItemSelected = { setSelectedHintsOption(it) }, // State is updated here
                modifier = Modifier.padding(horizontal = 8.dp)
            ) { item, isSelected, shape -> // The shape is now provided here!

                // The parent no longer knows or cares how the shape is made.
                ConnectedSelectionGroupOption(
                    isSelected = isSelected,
                    onClick = { setSelectedHintsOption(item) },
                    shape = shape, // Simply use the provided shape
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        item,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Button(
                onClick = { confirmAction() },
                shapes = ButtonDefaults.shapes(),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = ButtonDefaults.MediumContentPadding
            ) {
                Text(
                    confirmButtonText,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}