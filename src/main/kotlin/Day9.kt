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

    fun lowPoints(): List<Height> {
        return heights.flatMap { row ->
            row.filter { v ->
                hasLesserThan(v, *heights.getAdjacentPoints(v.row, v.col).toTypedArray())
            }
        }
    }

    fun findBasin(h: Height): Set<Height> {
        return heights.getAdjacentPoints(h.row, h.col).filter { it.value != 9 && it > h }
            .fold(setOf(h)) { acc, it -> acc + findBasin(it) }
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

