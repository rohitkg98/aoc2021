import java.security.InvalidParameterException

sealed class Step {
    class Forward(val magnitude: Int): Step()
    class Down(val magnitude: Int): Step()
    class Up(val magnitude: Int): Step()
}

object Day2 : Day() {
    override fun main() {
        val steps = this.readInput("day2.txt").split("\n").map {
            val split = it.split(" ")
            when (split[0]) {
                "forward" -> Step.Forward(split[1].toInt())
                "down" -> Step.Down(split[1].toInt())
                "up" -> Step.Up(split[1].toInt())
                else -> throw InvalidParameterException("invalid input $it")
            }
        }

        part1(steps)

        part2(steps)
    }

    private fun part2(steps: List<Step>) {
        val (horizontal, depth, _) = steps.fold(Triple(0, 0, 0)) { acc, step ->
            when (step) {
                is Step.Forward -> acc.copy(acc.first + step.magnitude, acc.second + acc.third*step.magnitude)
                is Step.Down -> acc.copy(third = acc.third + step.magnitude)
                is Step.Up -> acc.copy(third = acc.third - step.magnitude)
            }
        }

        println("Multiplication of horizontal and depth along with aim is ${horizontal * depth}")
    }

    private fun part1(steps: List<Step>) {
        val (horizontal, depth) = steps.fold(Pair(0, 0)) { acc, it ->
            when (it) {
                is Step.Forward -> acc.copy(acc.first + it.magnitude)
                is Step.Down -> acc.copy(second = acc.second + it.magnitude)
                is Step.Up -> acc.copy(second = acc.second - it.magnitude)
            }
        }

        println("Multiplication of final horizontal and depth ${horizontal * depth}")
    }
}
