package model

class GameBoard(
    private val rows: Int,
    private val columns: Int,
    private val mines: Int
) {
    private val fields = ArrayList<ArrayList<Field>>()
    private val callbacks = ArrayList<(GameBoardEvent) -> Unit>()

    init {
        generateFields()
        associateNeighbors()
        sortMines()
    }

    private fun generateFields() {
        for (row in 0 until rows) {
            fields.add(ArrayList())
            for (column in 0 until columns) {
                val newField = Field(row, column)
                newField.onEvent(this::analyzeGameResult)
                fields[row].add(newField)
            }
        }
    }

    private fun associateNeighbors() {
        forEachFields {
            associateNeighbors(it)
        }
    }

    private fun associateNeighbors(field: Field) {
        val (row, column) = field
        val rows = arrayOf(row - 1, row, row + 1)
        val columns = arrayOf(column - 1, column, column + 1)

        rows.forEach { r ->
            columns.forEach { c ->
                val current = fields.getOrNull(r)?.getOrNull(c)
                current?.takeIf { field != it }?.let { field.addNeighbour(it) }
            }
        }
    }

    private fun sortMines() {
        val generator = java.util.Random()
        var currentMines = 0

        while (currentMines < mines) {
            val drawnRow = generator.nextInt(rows)
            val drawnColumn = generator.nextInt(columns)
            val drawnField = fields[drawnRow][drawnColumn]
            if (drawnField.safe) {
                drawnField.mine()
                currentMines++
            }
        }
    }

    private fun goalAchieved(): Boolean {
        var playerWon = true
        forEachFields { if (!it.goalAchieved) playerWon = false }
        return playerWon
    }

    private fun analyzeGameResult(field: Field, event: FieldEvent) {
        if (event == FieldEvent.EXPLOSION) {
            callbacks.forEach { it(GameBoardEvent.DEFEAT) }
        } else if (goalAchieved()) {
            callbacks.forEach { it(GameBoardEvent.VICTORY) }
        }
    }

    private fun forEachFields(callback: (Field) -> Unit) {
        fields.forEach { row ->
            row.forEach(callback)
        }
    }

    fun onEvent(callback: (GameBoardEvent) -> Unit) {
        callbacks.add(callback)
    }

    fun restart() {
        forEachFields { it.restart() }
        sortMines()
    }
}