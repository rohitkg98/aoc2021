object Day1 : Day() {
    override fun main() {
        val scans = this.readInput("day1.txt").split("\n").map { it.toInt() }
        val (totalIncreasedScans, _) = scans.fold(Pair(0, 0)) { acc, i ->
            val isGreater = if (acc.second > i) 1 else 0
            Pair(acc.first + isGreater, i)
        }

        println("Total Increased Scans $totalIncreasedScans")

        val slidingWindowIncreasedScans = scans.slice(0..scans.size - 4)
            .foldIndexed(0) { index, acc, i -> if (i >= scans[index + 3]) acc else acc + 1 }

        println("Total Sliding Window Increased Scans $slidingWindowIncreasedScans")
    }
}
