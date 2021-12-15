import kotlin.math.abs

sealed class Move {
    object Up : Move()
    object Down : Move()
}

fun nSum(n: Int) = (n * (n + 1)) / 2

data class Interval(val start: Int, val end: Int) {
    fun profit(current: Int, move: Move): Int {
        val currentCost = this.cost(current)
        val newCost = when (move) {
            Move.Up -> this.cost(current - 1)
            Move.Down -> this.cost(current + 1)
        }

        return currentCost - newCost
    }

    fun cost(to: Int) = nSum(abs(start - to)) + nSum(abs(end - to))
}

data class Positions(val positions: List<Int>) {
    val intervals: List<Interval>

    init {
        val sorted = positions.sorted()

        val firstHalf = sorted.take(sorted.size / 2)
        val secondHalf = sorted.takeLast(sorted.size / 2)

        intervals = firstHalf.zip(secondHalf.reversed()).map { (first, second) -> Interval(first, second) }
    }

    fun intervals(): List<Int> {
        val sorted = positions.sorted()

        val firstHalf = sorted.take(sorted.size / 2)
        val secondHalf = sorted.takeLast(sorted.size / 2)

        return firstHalf.zip(secondHalf).map { (first, second) -> second - first }
    }

    fun optimalFuelUsage() = optimalFuelUsage(0)

    private fun optimalFuelUsage(start: Int): Int {
        val (up, down) = intervals.map { it.profit(start, Move.Up) to it.profit(start, Move.Down) }.unzip()

        val upProfit = up.sum(); val downProfit = down.sum()

        return when {
            upProfit <= 0 && downProfit <= 0 -> intervals.sumOf { it.cost(start) }
            upProfit > downProfit -> optimalFuelUsage(start - 1)
            else -> optimalFuelUsage(start + 1)
        }
    }
}

object Day7 : Day() {
    override fun main() {
        val levels = this.readInput("day7.txt").split(",").map { it.toInt() }.sorted()

        val positions = Positions(levels)

        println("the minimal fuel cost to align ${positions.intervals().sum()}")


        println("the minimal fuel cost to align using crab engineering ${positions.optimalFuelUsage()}")
    }
}
