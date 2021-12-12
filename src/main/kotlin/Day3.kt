data class ReportNumber(val number: String) {
    val bitValues = number.map { it == '0' }
    val length = number.length
}

data class Freq(val numbers: List<ReportNumber>) {
    val counts = numbers.map { it.bitValues }
        .fold((0..numbers[0].length).map { Pair(0, 0) })
        { acc, it ->
            acc.zip(it).map { (total, isZero) ->
                if (isZero) total.copy(first = total.first + 1)
                else total.copy(second = total.second + 1)
            }
        }

    private val gammaStr = counts.fold("") { acc, pair -> if (pair.first > pair.second) acc + "0" else acc + "1" }

    val gamma = gammaStr.toInt(2)
    val epsilon = gammaStr.map { if (it == '0') '1' else '0' }.joinToString("").toInt(2)
}

fun findRating(numbers: List<ReportNumber>, index: Int, filterChar: (Pair<Int, Int>) -> Char): Int {
    val freq = Freq(numbers)


    val filtered = numbers.filter { it.number[index] == filterChar(freq.counts[index]) }

    return if (filtered.size == 1)
        filtered.first().number.toInt(2)
    else
        findRating(filtered, index + 1, filterChar)
}

object Day3 : Day() {
    override fun main() {
        val numbers = this.readInput("day3.txt").split("\n").map { ReportNumber(it) }

        val freq = Freq(numbers)

        part1(freq)

        part2(numbers)
    }

    private fun part2(numbers: List<ReportNumber>) {
        val oxygenGeneratorRating = findRating(numbers, 0) { if (it.first > it.second) '0' else '1'}

        val co2ScrubberRating = findRating(numbers, 0) { if (it.first <= it.second) '0' else '1'}

        println("The life support rating of the submarine is ${oxygenGeneratorRating * co2ScrubberRating}")
    }

    private fun part1(freq: Freq) {
        println("The power consumption of submarine is ${freq.gamma * freq.epsilon}")
    }
}
