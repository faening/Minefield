package view

import model.Field
import model.FieldEvent
import java.awt.Color
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.SwingUtilities

private var COLOR_BG_DEFAULT = Color(184, 184, 184)
private var COLOR_BG_MARKED = Color(8, 179, 247)
private var COLOR_BG_EXPLOSION = Color(189, 66, 68)
private var COLOR_TXT_GREEN = Color(0, 100, 0)

class FieldButton(private val field: Field) : JButton() {
    init {
        font = font.deriveFont(Font.BOLD)
        background = COLOR_BG_DEFAULT
        isOpaque = true
        border = BorderFactory.createBevelBorder(0)

        addMouseListener(MouseClickListener(
            field = field,
            onLeftButton = { it.open() },
            onRightButton = { it.changeMark() })
        )

        field.onEvent(this::applyStyle)
    }

    private fun applyStyle(field: Field, event: FieldEvent) {
        when (event) {
            FieldEvent.EXPLOSION -> applyStyleExploded()
            FieldEvent.OPENED -> applyStyleOpened()
            FieldEvent.MARKED -> applyStyleMarked()
            else -> applyStyleDefault()
        }

        SwingUtilities.invokeLater {
            repaint()
            validate()
        }
    }

    fun applyStyleExploded() {
        background = COLOR_BG_EXPLOSION
        text = "X"
    }

    fun applyStyleOpened() {
        background = COLOR_BG_DEFAULT
        border = BorderFactory.createLineBorder(Color.GRAY)
        text = if (field.minedNeighbors > 0) field.minedNeighbors.toString() else ""
        foreground = when (field.minedNeighbors) {
            1 -> COLOR_TXT_GREEN
            2 -> Color.BLUE
            3 -> Color.RED
            4 -> Color.YELLOW
            5 -> Color.PINK
            6 -> Color.CYAN
            7 -> Color.MAGENTA
            8 -> Color.ORANGE
            else -> Color.BLACK
        }
    }

    fun applyStyleMarked() {
        background = COLOR_BG_MARKED
        foreground = Color.BLACK
        text = "M"
    }

    fun applyStyleDefault() {
        background = COLOR_BG_DEFAULT
        border = BorderFactory.createBevelBorder(0)
        text = ""
    }
}