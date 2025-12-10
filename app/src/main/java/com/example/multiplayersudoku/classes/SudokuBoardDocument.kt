enum class GameStatus(val value: String) {
    WAITING("waiting"),
    IN_PROGRESS("in_progress"),
    FINISHED("finished")
}

data class SudokuBoardDocument(
    val canonicalBoard: List<List<Int>> = emptyList(),
    @JvmField
    val status: GameStatus = GameStatus.WAITING,
    val players: Map<String, Player> = emptyMap(),
    val solutions: Map<String, List<List<Int>>> = emptyMap()
)