data class Polymer(val template: String) {
    private val pairs = template.zipWithNext()

    fun apply(rules: Map<Pair<Char, Char>, InsertionRule>): Polymer {
        val newPairs = pairs.flatMap { rules.getValue(it).pairs() }

        return Polymer(newPairs.toTemplate())
    }
}

private fun List<Pair<Char, Char>>.toTemplate(): String {
    return (map { it.first } + last().second).joinToString("")
}

data class InsertionRule(val pair: Pair<Char, Char>, val mid: Char) {
    fun pairs() = listOf(pair.first to mid, mid to pair.second)
}

object Day14 : Day() {
    override fun main() {
        val (template, rules) = this.readInput("day14.txt").split("\n\n")

        val parsedRules = rules.split("\n").map {
            val (pairs, mid) = it.split(" -> ")
            InsertionRule(pairs[0] to pairs[1], mid[0])
        }.associateBy { it.pair }

        val polymer = Polymer(template)

        part1(polymer, parsedRules)

        part2(polymer, parsedRules)
    }

    private fun part2(polymer: Polymer, parsedRules: Map<Pair<Char, Char>, InsertionRule>) {
        polymerizeNTimes(polymer, parsedRules, 40)
    }

    private fun part1(polymer: Polymer, rules: Map<Pair<Char, Char>, InsertionRule>) {
        polymerizeNTimes(polymer, rules, 10)
    }

    private fun polymerizeNTimes(
        polymer: Polymer,
        rules: Map<Pair<Char, Char>, InsertionRule>,
        n: Int
    ) {
        val final = (1..n).fold(polymer) { p, _ -> p.apply(rules) }

        val count = final.template.groupingBy { it }.eachCount()
        val max = count.maxOf { it.value }
        val min = count.minOf { it.value }

        println("the difference b/w max occuring and least occuring after polymerizing $n times: ${max - min}")
    }
}
