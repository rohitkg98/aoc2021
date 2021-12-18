fun <E> List<List<E>>.getAdjacentPoints(row: Int, col: Int): List<E> {
    val rowLast = this.lastIndex
    val colLast = this[0].lastIndex

    return when {
        row == 0 && col == 0 -> listOf(this[row][1], this[1][0])
        row == 0 && col == colLast -> listOf(this[row][colLast - 1], this[1][col])
        row == 0 -> listOf(this[row][col - 1], this[row][col + 1], this[1][col])

        row == rowLast && col == 0 -> listOf(this[row][1], this[rowLast - 1][0])
        row == rowLast && col == colLast -> listOf(this[row][colLast - 1], this[rowLast - 1][col])
        row == rowLast -> listOf(this[row][col - 1], this[row][col + 1], this[rowLast - 1][col])

        col == 0 -> listOf(this[row - 1][0], this[row + 1][0], this[row][1])
        col == colLast -> listOf(
            this[row - 1][colLast], this[row + 1][colLast], this[row][colLast - 1]
        )

        else -> listOf(
            this[row][col - 1], this[row][col + 1], this[row + 1][col], this[row - 1][col]
        )
    }
}

fun <E> List<List<E>>.getDiagonallyAdjacentPoints(row: Int, col: Int): List<E> {
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

fun <E> List<List<E>>.getAllAdjacentPoints(row: Int, col: Int): List<E> =
    getAdjacentPoints(row, col) + getDiagonallyAdjacentPoints(row, col)
