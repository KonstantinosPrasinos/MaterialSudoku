package com.example.multiplayersudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.example.multiplayersudoku.state.GlobalState
import com.example.multiplayersudoku.state.GlobalStateContext
import com.example.multiplayersudoku.ui.theme.MultiplayerSudokuTheme
import com.example.multiplayersudoku.utils.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Using the new secondary constructor
    @OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val globalState = remember { GlobalStateContext() }

            CompositionLocalProvider(GlobalState provides globalState) {
                MultiplayerSudokuTheme {
                    AppNavigation()
                }
            }
        }
    }
}