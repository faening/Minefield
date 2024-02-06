package model

data class Field(
    val row: Int,
    val column: Int
) {
    private val neighbours = ArrayList<Field>()
    private val callbacks = ArrayList<(Field, FieldEvent) -> Unit>()

    var marked: Boolean = false
    var opened: Boolean = false
    var mined: Boolean = false

    // Readonly properties
    val unmarked: Boolean get() = !marked
    val closed: Boolean get() = !opened
    val safe: Boolean get() = !mined
    val goalAchieved: Boolean get() = safe && opened || mined && marked
    val minedNeighbors: Int get() = neighbours.filter { it.mined }.size
    val safeNeighbor: Boolean get() = neighbours.map { it.safe }.reduce { result, safe -> result && safe }

    fun addNeighbour(neighbour: Field) {
        neighbours.add(neighbour)
    }

    fun onEvent(callback: (Field, FieldEvent) -> Unit) {
        callbacks.add(callback)
    }

    fun open() {
        if (closed) {
            opened = true
            if (mined) {
                callbacks.forEach { it(this, FieldEvent.EXPLOSION) }
            } else {
                callbacks.forEach { it(this, FieldEvent.OPENED) }
                neighbours.filter { it.closed && it.safe && safeNeighbor }.forEach { it.open() }
            }
        }
    }

    fun changeMark() {
        if (closed) {
            marked = !marked
            val event = if (marked) FieldEvent.MARKED else FieldEvent.UNMARKED
            callbacks.forEach { it(this, event) }
        }
    }

    fun mine() {
        mined = true
    }

    fun restart() {
        marked = false
        opened = false
        mined = false
        callbacks.forEach { it(this, FieldEvent.RESTART) }
    }
}
