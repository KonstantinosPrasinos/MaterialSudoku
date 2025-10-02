package com.example.multiplayersudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.multiplayersudoku.classes.SudokuBoardData
import com.example.multiplayersudoku.components.ActionButtons
import com.example.multiplayersudoku.components.NumberButtons
import com.example.multiplayersudoku.components.SudokuBoard
import com.example.multiplayersudoku.components.SudokuTopAppBar
import com.example.multiplayersudoku.components.TimerBar
import com.example.multiplayersudoku.ui.theme.MultiplayerSudokuTheme

class MainActivity : ComponentActivity() {
    val initialBoardValues = listOf(
        listOf(5, 3, null, null, 7, null, null, null, null),
        listOf(6, null, null, 1, 9, 5, null, null, null),
        listOf(null, 9, 8, null, null, null, null, 6, null),
        listOf(8, null, null, null, 6, null, null, null, 3),
        listOf(4, null, null, 8, null, 3, null, null, 1),
        listOf(7, null, null, null, 2, null, null, null, 6),
        listOf(null, 6, null, null, null, null, 2, 8, null),
        listOf(null, null, null, 4, 1, 9, null, null, 5),
        listOf(null, null, null, null, 8, null, null, 7, 9)
    )

    // Using the new secondary constructor
    val sudokuBoard = SudokuBoardData.fromInitialValues(initialBoardValues)

    @OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultiplayerSudokuTheme {
                val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                var isPaused by remember { mutableStateOf(false) }
                var isEditing by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        SudokuTopAppBar(
                            scrollBehavior = scrollBehavior,
                            isPaused = isPaused,
                            togglePaused = { isPaused = !isPaused })
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TimerBar(isPaused = isPaused)
                        SudokuBoard(sudokuBoard)
                        NumberButtons()
                        ActionButtons(isEditing, toggleEditing = { isEditing = !isEditing })
                    }
                }
            }
        }
    }
}