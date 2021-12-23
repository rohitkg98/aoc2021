data class CoOrdinate(val row: Int, val col: Int) {
    operator fun compareTo(other: CoOrdinate): Int {
        return row.compareTo(other.row) + col.compareTo(other.col)
    }
}

typealias List2D<E> = List<List<E>>

operator fun <E> List2D<E>.get(c: CoOrdinate): E {
    return this[c.row][c.col]
}

fun <E> List2D<E>.getAdjacentPoints(c: CoOrdinate): List<E> {
    return getAdjacentPoints(c.row, c.col)
}

fun <E> List2D<E>.getAdjacentPoints(row: Int, col: Int): List<E> {
    return getAdjacentCoOrdinates(row, col).map { this[it] }
}

fun <E> List2D<E>.getAdjacentCoOrdinates(c: CoOrdinate): List<CoOrdinate> {
    return getAdjacentCoOrdinates(c.row, c.col)
}

fun <E> List2D<E>.getAdjacentCoOrdinates(row: Int, col: Int): List<CoOrdinate> {
    val rowLast = this.lastIndex
    val colLast = this[0].lastIndex

    return when {
        row == 0 && col == 0 -> listOf(CoOrdinate(row, 1), CoOrdinate(1, 0))
        row == 0 && col == colLast -> listOf(CoOrdinate(row, colLast - 1), CoOrdinate(1, col))
        row == 0 -> listOf(CoOrdinate(row, col - 1), CoOrdinate(row, col + 1), CoOrdinate(1, col))

        row == rowLast && col == 0 -> listOf(CoOrdinate(row, 1), CoOrdinate(rowLast - 1, 0))
        row == rowLast && col == colLast -> listOf(CoOrdinate(row, colLast - 1), CoOrdinate(rowLast - 1, col))
        row == rowLast -> listOf(CoOrdinate(row, col - 1), CoOrdinate(row, col + 1), CoOrdinate(rowLast - 1, col))

        col == 0 -> listOf(CoOrdinate(row - 1, 0), CoOrdinate(row + 1, 0), CoOrdinate(row, 1))
        col == colLast -> listOf(
            CoOrdinate(row - 1, colLast), CoOrdinate(row + 1, colLast), CoOrdinate(row, colLast - 1)
        )

        else -> listOf(
            CoOrdinate(row, col - 1), CoOrdinate(row, col + 1), CoOrdinate(row + 1, col), CoOrdinate(row - 1, col)
        )
    }
}

fun <E> List2D<E>.getDiagonallyAdjacentPoints(row: Int, col: Int): List<E> {
    val rowLast = this.lastIndex
    val colLast = this[0].lastIndex

    return when {
        row == 0 && col == 0 -> listOf(this[1][1])
        row == 0 && col == colLast -> listOf(this[1][colLast - 1])
        row == 0 -> listOf(this[1][col - 1], this[1][col + 1])

        row == rowLast && col == 0 -> listOf(this[rowLast - 1][1])
        row == rowLast && col == colLast -> listOf(this[rowLast - 1][colLast - 1])
        row == rowLast -> listOf(this[row - 1][col - 1], this[row - 1][col + 1])

        col == 0 -> listOf(this[row - 1][1], this[row + 1][1])
        col == colLast -> listOf(this[row - 1][colLast - 1], this[row + 1][colLast - 1])

        else -> listOf(
            this[row - 1][col - 1], this[row + 1][col + 1], this[row + 1][col - 1], this[row - 1][col + 1]
        )
    }
}

fun <E> List2D<E>.getAllAdjacentPoints(row: Int, col: Int): List<E> =
    getAdjacentPoints(row, col) + getDiagonallyAdjacentPoints(row, col)

fun <E> List2D<E>.lastCoOrdinate() = CoOrdinate(lastIndex, get(0).lastIndex)

fun <E> List2D<E>.verticalMerge(other: List2D<E>) = this.zip(other).map { (l1, l2) -> l1 + l2 }

