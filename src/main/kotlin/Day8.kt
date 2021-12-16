data class Digit(val signal: String) {
    val set = signal.toSet()
    val size = set.size
    fun isUnique() = set.size == 2 || set.size == 4 || set.size == 3 || set.size == 7

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Digit

        if (set != other.set) return false

        return true
    }

    override fun hashCode() = set.hashCode()
}

data class JournalEntry(val entry: String) {
    private val signals: List<Digit>
    private val output: List<Digit>

    init {
        val (signals, output) = entry.split(" | ").map { it.split(" ") }
        this.signals = signals.map { Digit(it) }
        this.output = output.map { Digit(it) }
    }

    fun uniqueDigits() = output.filter { it.isUnique() }.size

    fun decodeOutput(): Int {
        /*
          t
         ----
     vtl|    |vtr
        | m  |
         ----
     vbl|    |vbr
        | b  |
         ----

        1. vtr, vbr               l-2
        4. 1 + vtl, m             l-4
        7. 1 + t                  l-3

        2. t, vtr, m, vbl, b      l-5, set(1) n set(2) = 1, set(4) n set(2) = 2, set(7) n set(2) = 2
        3. 7, m, b                l-5, set(1) n set(3) = 2, set(4) n set(3) = 3, set(7) n set(3) = 3
        5. t + vtl, m, vbr, b     l-5, set(1) n set(5) = 1, set(4) n set(5) = 3, set(7) n set(5) = 2
        6. 5 + vbl                l-6, set(1) n set(6) = 1, set(4) n set(6) = 3, set(7) n set(6) = 2
        9. 7 + vtl, m, b          l-6, set(1) n set(9) = 2, set(4) n set(9) = 4, set(7) n set(9) = 3
        0. 8 - m                  l-6, set(1) n set(0) = 2, set(4) n set(0) = 3, set(7) n set(0) = 3
        */

        val uniqueSignals = signals.filter { it.isUnique() }.associateBy {
            when (it.size) {
                2 -> 1
                4 -> 4
                3 -> 7
                7 -> 8
                else -> throw Exception("No unique number")
            }
        }

        val one = uniqueSignals.getValue(1)
        val four = uniqueSignals.getValue(4)

        return output.map {
            when {
                it.size == 2 -> 1
                it.size == 4 -> 4
                it.size == 3 -> 7
                it.size == 7 -> 8
                it.size == 5 && it.set.intersect(one.set).size == 1 && it.set.intersect(four.set).size == 2 -> 2
                it.size == 5 && it.set.intersect(one.set).size == 2 && it.set.intersect(four.set).size == 3 -> 3
                it.size == 5 && it.set.intersect(one.set).size == 1 && it.set.intersect(four.set).size == 3 -> 5
                it.size == 6 && it.set.intersect(one.set).size == 1 && it.set.intersect(four.set).size == 3 -> 6
                it.size == 6 && it.set.intersect(one.set).size == 2 && it.set.intersect(four.set).size == 4 -> 9
                it.size == 6 && it.set.intersect(one.set).size == 2 && it.set.intersect(four.set).size == 3 -> 0
                else -> throw Exception("unexpected condition")
            }
        }.joinToString("").toInt()
    }
}

object Day8 : Day() {
    override fun main() {
        val entries = this.readInput("day8.txt").split("\n").map { JournalEntry(it) }

        val uniqueDigits = entries.sumOf { it.uniqueDigits() }

        println("total unique digits in journal entry $uniqueDigits")

        val outputSum = entries.sumOf { it.decodeOutput() }

        println("total sum of output values $outputSum")
    }
}
