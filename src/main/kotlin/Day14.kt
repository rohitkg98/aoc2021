class OptimizedPolymer(private val pairs: Map<Pair<Char, Char>, Long>) {
    fun apply(rules: List<InsertionRule>): OptimizedPolymer {
        val newPairs = rules.fold(mutableMapOf<Pair<Char, Char>, Long>()) { newPairs, rule ->
            val count = pairs.getOrDefault(rule.pair, 0)
            if (count > 0) {
                for (pair in rule.pairs()) {
                    newPairs[pair] = newPairs.getOrDefault(pair, 0) + count
                }
            }

            newPairs
        }

        return OptimizedPolymer(newPairs)
    }

    fun diffOfMaxAndMin(startChar: Char): Long {
        val counts = pairs.toList().map { it.first.second to it.second }
            .groupBy({ it.first }, { it.second })
            .map { it.key to it.value.sum() }.sortedBy { it.second }
            .sortedByDescending { it.second }

        val addOneIfStartChar = {count: Pair<Char, Long> ->
            if (count.first == startChar) count.second + 1
            else count.second
        }

        return addOneIfStartChar(counts.first()) - addOneIfStartChar(counts.last())
    }
}

data class Polymer(val template: String) {
    private val pairs = template.zipWithNext()

    fun apply(rules: Map<Pair<Char, Char>, InsertionRule>): Polymer {
        val newPairs = pairs.flatMap { rules.getValue(it).pairs() }

        return Polymer(newPairs.toTemplate())
    }

    fun optimized() = OptimizedPolymer(pairs.groupingBy { it }.eachCount().mapValues { it.value.toLong() })
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
        }

        val polymer = Polymer(template)

        part1(polymer, parsedRules.associateBy { it.pair })

        part2(polymer, parsedRules)
    }

    private fun part2(polymer: Polymer, rules: List<InsertionRule>) {
        val optimized = polymer.optimized()

        val final = (1..40).fold(optimized) { p, _ -> p.apply(rules) }

        val diff = final.diffOfMaxAndMin(polymer.template[0])

        println("the difference b/w max occurring and least occurring after polymerizing 40 times: $diff")
    }

    private fun part1(polymer: Polymer, rules: Map<Pair<Char, Char>, InsertionRule>) {
        val final = (1..10).fold(polymer) { p, _ -> p.apply(rules) }

        val count = final.template.groupingBy { it }.eachCount()
        val max = count.maxOf { it.value }
        val min = count.minOf { it.value }

        println("the difference b/w max occurring and least occuring after polymerizing 10 times: ${max - min}")
    }
}
