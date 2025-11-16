package com.example.multiplayersudoku.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.multiplayersudoku.classes.Difficulty

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainView(onNavigateToSudoku: (difficulty: Difficulty) -> Unit) {
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 10.dp),
        ) {
            Text("Material Sudoku", style = MaterialTheme.typography.displaySmallEmphasized)
            ButtonGroup(overflowIndicator = { menuState ->
                FilledIconButton(
                    onClick = {
                        if (menuState.isExpanded) {
                            menuState.dismiss()
                        } else {
                            menuState.show()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Localized description",
                    )
                }
            }) {
                Difficulty.entries.forEach { difficulty ->
                    toggleableItem(
                        checked = selectedDifficulty == difficulty,
                        onCheckedChange = { selectedDifficulty = difficulty },
                        label = difficulty.name
                    )
                }
            }
            Button(
                onClick = { onNavigateToSudoku(selectedDifficulty) },
                shapes = ButtonDefaults.shapes()
            ) {
                Text("New Game")
            }
        }
    }
}