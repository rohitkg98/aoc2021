data class CoOrdinate(val row: Int, val col: Int)

data class Board(val board: String) {
    var rowSums = IntArray(5)
    var colSums = IntArray(5)

    private val coordinates = board.split("\n")
        .mapIndexed { row, it ->
            it.trim().split(" +".toRegex())
                .map { it.trim().toInt() }
                .foldIndexed(mapOf<Int, CoOrdinate>()) { col, acc, num -> acc + (num to CoOrdinate(row, col)) }
        }.reduce { acc, it -> acc + it }

    private val marked = mutableMapOf<Int, Boolean>()

    fun mark(num: Int): Boolean {
        if (!coordinates.containsKey(num))
            return false

        marked[num] = true

        val (row, col) = coordinates.getValue(num)
        rowSums[row]++
        colSums[col]++

        return rowSums[row] == 5 || colSums[col] == 5
    }

    fun hasWon(): Boolean {
        return rowSums.contains(5) || colSums.contains(5)
    }

    fun markedSum(): Int {
        return coordinates.map { it.key }.filter { !marked.containsKey(it) }.sum()
    }
}

object Day4 : Day() {
    override fun main() {
        val input = this.readInput("day4.txt").split("\n\n")
        val draws = input.first().split(",").map { it.toInt() }

        part1(draws, input.drop(1).map { Board(it) })

        part2(draws, input.drop(1).map { Board(it) })
    }

    private fun part2(draws: List<Int>, boards: List<Board>) {
        val finalScore = draws.fold(0) { previousWinner, draw ->
            val lastWinner = boards.filter { !it.hasWon() }.fold(0) { lastWinner, b ->
                if (b.mark(draw))
                    b.markedSum() * draw
                else lastWinner
            }
            if (lastWinner == 0) previousWinner else lastWinner
        }

        println("The Final Score of last winning board is $finalScore")
    }

    private fun part1(draws: List<Int>, boards: List<Board>) {
        val finalScore = draws.fold(0) { acc, draw ->
            if (acc == 0)
                boards.fold(0) { firstWinner, b -> if (firstWinner == 0 && b.mark(draw)) b.markedSum() * draw else firstWinner }
            else acc
        }

        println("The Final Score of winning board is $finalScore")
    }
}
