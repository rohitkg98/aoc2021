fun hasLesserThan(v: Height, vararg other: Height) = other.fold(true) { acc, i -> acc && (v < i) }

data class Height(val row: Int, val col: Int, val value: Int) {
    operator fun compareTo(other: Height): Int = this.value.compareTo(other.value)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Height

        if (row != other.row) return false
        if (col != other.col) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int = 31 * row + col
}

data class HeightMap(val locHeights: List<IntArray>) {
    val heights: List<List<Height>> =
        locHeights.mapIndexed { row, it -> it.mapIndexed { col, v -> Height(row, col, v) } }
    private val rowLast = heights.lastIndex
    private val colLast = heights[0].lastIndex

    fun lowPoints(): List<Height> {

        return heights.flatMap { row ->
            row.filter { v ->
                hasLesserThan(v, *getAdjacentPoints(v).toTypedArray())
            }
        }
    }

    fun findBasin(h: Height): Set<Height> {
        return getAdjacentPoints(h).filter { it.value != 9 && it > h }
            .fold(setOf(h)) { acc, it -> acc + findBasin(it) }
    }

    private fun getAdjacentPoints(h: Height): List<Height> {
        val row = heights[h.row]
        val col = h.col
        return when {
            h.row == 0 && col == 0 -> listOf(row[1], heights[1][0])
            h.row == 0 && col == colLast -> listOf(row[colLast - 1], heights[1][col])
            h.row == 0 -> listOf(row[col - 1], row[col + 1], heights[1][col])

            h.row == rowLast && col == 0 -> listOf(row[1], heights[rowLast - 1][0])
            h.row == rowLast && col == colLast -> listOf(row[colLast - 1], heights[rowLast - 1][col])
            h.row == rowLast -> listOf(row[col - 1], row[col + 1], heights[rowLast - 1][col])

            col == 0 -> listOf(heights[h.row - 1][0], heights[h.row + 1][0], row[1])
            col == colLast -> listOf(
                heights[h.row - 1][colLast], heights[h.row + 1][colLast], row[colLast - 1]
            )

            else -> listOf(
                row[col - 1], row[col + 1], heights[h.row + 1][col], heights[h.row - 1][col]
            )
        }
    }
}

object Day9 : Day() {
    override fun main() {
        val locationHeights =
            this.readInput("day9.txt").split("\n").map { it.map { it.toString().toInt() }.toIntArray() }

        val heightMap = HeightMap(locationHeights)

        part1(heightMap)

        part2(heightMap)
    }

    private fun part2(heightMap: HeightMap) {
        val total = heightMap.lowPoints()
            .map { heightMap.findBasin(it).size }
            .sortedDescending()
            .take(3)
            .reduce { acc, i -> acc * i }

        println("total product of size of largest 3 basins is $total")
    }

    private fun part1(heightMap: HeightMap) {
        val totalRiskLevels = heightMap.lowPoints().sumOf { it.value + 1 }

        println("total sum of risk levels of all low points $totalRiskLevels")
    }
}

