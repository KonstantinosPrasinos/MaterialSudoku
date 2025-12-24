package com.example.multiplayersudoku.views.statisticsView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multiplayersudoku.classes.Difficulty
import com.example.multiplayersudoku.datastore.gameResult.StatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class StatisticsUiState(
    val averageDuration: Long? = null,
    val bestTime: Long? = null,
    val totalGames: Int = 0,
    val completedGames: Int = 0,
    val totalDuration: Long? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: StatisticsRepository
) : ViewModel() {

    private val _selectedDifficulty = MutableStateFlow<Difficulty?>(null)
    val selectedDifficulty = _selectedDifficulty.asStateFlow()

    fun setDifficulty(difficulty: Difficulty?) {
        _selectedDifficulty.value = difficulty
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<StatisticsUiState> = _selectedDifficulty
        .flatMapLatest { difficulty ->
            repository.getStatisticsSummary(difficulty?.name)
        }
        .map { summary ->
            StatisticsUiState(
                averageDuration = summary.averageDuration,
                bestTime = summary.bestTime,
                totalGames = summary.totalGames,
                completedGames = summary.completedGames,
                totalDuration = summary.totalDuration,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StatisticsUiState(isLoading = true)
        )
}