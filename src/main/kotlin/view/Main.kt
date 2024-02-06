package view

import model.GameBoard
import model.GameBoardEvent
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    MainFrame()
}

class MainFrame : JFrame() {
    private val gameBoard = GameBoard(rows = 16, columns = 30, mines = 89)
    private val gameBoardPanel = GameBoardPanel(gameBoard)

    init {
        gameBoard.onEvent(this::showResult)

        // Screen settings
        add(gameBoardPanel)
        setSize(690, 438)
        setLocationRelativeTo(null) // Centralize the screen
        defaultCloseOperation = EXIT_ON_CLOSE // Close the application when the window is closed
        title = "Minefield"
        isVisible = true
    }

    private fun showResult(event: GameBoardEvent) {
        SwingUtilities.invokeLater {
            val message = when (event) {
                GameBoardEvent.VICTORY -> "You won!"
                GameBoardEvent.DEFEAT -> "You lost!"
            }
            JOptionPane.showMessageDialog(this, message)
            gameBoard.restart()
            gameBoardPanel.repaint()
        }
    }
}