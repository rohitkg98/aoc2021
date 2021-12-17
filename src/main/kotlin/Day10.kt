sealed class Result {
    object Valid : Result()
    object Incomplete : Result()
    class Failure(val lastChar: Char) : Result()
}

val completingCost = mapOf(
    ')' to 1L,
    ']' to 2L,
    '}' to 3L,
    '>' to 4L,
)

val failureScores = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137,
)

val bracePairs = mapOf(
    ')' to '(',
    ']' to '[',
    '}' to '{',
    '>' to '<',
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>',
)

class ProgramLine(line: String) {
    private val stack: List<Char>
    val status: Result

    init {
        stack = mutableListOf()

        var status: Result = Result.Incomplete
        for (c in line) {
            when (c) {
                '(', '[', '{', '<' -> stack.add(c)
                ')', ']', '}', '>' ->
                    if (stack.last() != bracePairs.getValue(c)) {
                        status = Result.Failure(c)
                        break
                    } else stack.removeLast()
            }
        }

        if (stack.isEmpty()) status = Result.Valid

        this.status = status
    }

    fun completionScore(): Long {
        return stack.reversed().fold(0L) { acc, c -> 5 * acc + completingCost.getValue(bracePairs.getValue(c)) }
    }
}

object Day10 : Day() {
    override fun main() {
        val lines = this.readInput("day10.txt").split("\n").map { ProgramLine(it) }

        part1(lines)

        part2(lines)
    }

    private fun part2(lines: List<ProgramLine>) {
        val scores = lines.filter { it.status is Result.Incomplete }.map { it.completionScore() }.sorted()

        println("the middle score for autocompleting is ${scores[scores.size / 2]}")
    }

    private fun part1(lines: List<ProgramLine>) {
        val totalScore = lines.fold(0) { acc, line ->
            when (val status = line.status) {
                is Result.Failure -> acc + failureScores.getValue(status.lastChar)
                else -> acc
            }
        }

        println("total error score $totalScore")
    }
}
