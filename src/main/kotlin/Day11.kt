data class Octopus(val row: Int, val col: Int, var power: Int) {
    var isLit = false

    fun nextPower() {
        when {
            power == 9 -> {
                power = 0; isLit = true
            }
            !isLit && power in 0..8 -> power += 1
        }
    }
}

class OctopiPower(powers: List<List<Int>>) {
    val octopi = powers.mapIndexed { row, it -> it.mapIndexed { col, pow -> Octopus(row, col, pow) } }

    fun totalLit() = octopi.fold(0) { acc, row ->
        acc + row.fold(0) { rowAcc, ock -> rowAcc + if (ock.isLit) 1 else 0 }
    }

    fun stepAll() {
        octopi.forEach { row ->
            row.forEach { ock ->
                step(ock)
            }
        }
    }

    fun step(octopus: Octopus) {
        if (!octopus.isLit) {
            octopus.nextPower()

            if (octopus.isLit)
                octopi.getAllAdjacentPoints(octopus.row, octopus.col).forEach { step(it) }
        }
    }

    fun resetLit() = octopi.forEach { row -> row.forEach { ock -> ock.isLit = false } }

    fun isFullyLit() = octopi.fold(true) { acc, row -> acc and row.fold(true) { rowAcc, ock -> rowAcc and ock.isLit } }
}

object Day11 : Day() {
    override fun main() {
        val octopiPower = this.readInput("day11.txt").split("\n").map { line -> line.map { it.toString().toInt() } }

        part1(octopiPower)

        part2(octopiPower)
    }

    private fun part2(powers: List<List<Int>>) {
        val octopi = OctopiPower(powers)

        var count = 0
        while (!octopi.isFullyLit()) {
            octopi.resetLit()
            count += 1
            octopi.stepAll()
        }

        println("the iterations taken to fully lit are $count")
    }


    private fun part1(powers: List<List<Int>>) {
        val octopi = OctopiPower(powers)

        var totalLitScore = 0

        repeat(100) {
            octopi.stepAll()
            totalLitScore += octopi.totalLit()
            octopi.resetLit()
        }

        println("the total lit score after 100 step is $totalLitScore")
    }
}
