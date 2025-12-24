package com.example.multiplayersudoku.views.SudokuView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multiplayersudoku.datastore.gameResult.GameResult
import com.example.multiplayersudoku.datastore.gameResult.StatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SudokuViewModel @Inject constructor(
    private val repository: StatisticsRepository
) : ViewModel() {

    // Converts the Flow into a StateFlow that the UI can observe
    fun onGameFinished(result: GameResult) {
        viewModelScope.launch {
            repository.saveResult(result)
        }
    }
}