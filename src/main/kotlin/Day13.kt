import java.security.InvalidParameterException
import kotlin.math.abs

sealed class Fold {
    class Vertical(val x: Int) : Fold()
    class Horizontal(val y: Int) : Fold()
}

class Paper(dots: List<CoOrdinate>) {
    val paper: List<IntArray>

    init {
        val maxCol = dots.maxOf { it.col }
        val maxRow = dots.maxOf { it.row }

        paper = (0..maxRow).map { IntArray(maxCol + 1) }

        dots.forEach { (y, x) -> paper[y][x] += 1 }
    }

    fun fold(fold: Fold): Paper {
        val newDots = when (fold) {
            is Fold.Vertical -> {
                // in a vertical fold, y stays constant, x is halved
                paper.flatMapIndexed { rowIdx, row ->
                    row.flatMapIndexed { col, v ->
                        List(v) {
                            CoOrdinate(
                                rowIdx,
                                if (col > fold.x) fold.x - abs(col - fold.x) else col
                            )
                        }
                    }
                }
            }
            is Fold.Horizontal -> {
                // in a vertical fold, y stays constant, x is halved
                paper.flatMapIndexed { rowIdx, row ->
                    row.flatMapIndexed { col, v ->
                        List(v) {
                            CoOrdinate(
                                if (rowIdx > fold.y) fold.y - abs(rowIdx - fold.y) else rowIdx,
                                col,
                            )
                        }
                    }
                }
            }
        }

        return Paper(newDots)
    }

    fun visibleDots() = paper.sumOf { ints -> ints.filter { it > 0 }.size }

    fun debug() = paper.joinToString("\n") { row -> row.joinToString("") { if (it > 0) "#" else "." } }
}


object Day13 : Day() {
    override fun main() {
        val (coordinates, folds) = this.readInput("day13.txt").split("\n\n")

        val parsedCoordinates = coordinates.split("\n").map {
            val (x, y) = it.split(",")
            CoOrdinate(y.toInt(), x.toInt())
        }

        val parsedFolds = folds.split("\n").map {
            val (along, coord) = it.split("=")
            when (along.last()) {
                'x' -> Fold.Vertical(coord.toInt())
                'y' -> Fold.Horizontal(coord.toInt())
                else -> throw InvalidParameterException("neither x nor y")
            }
        }

        val paper = Paper(parsedCoordinates)

        part1(paper, parsedFolds[0])

        part2(paper, parsedFolds)
    }

    private fun part2(paper: Paper, parsedFolds: List<Fold>) {
        val afterAllFold = parsedFolds.fold(paper) { acc, fold -> acc.fold(fold) }

        println("letters formed after all folds")
        println(afterAllFold.debug())
    }

    private fun part1(paper: Paper, firstFold: Fold) {
        println("visible dots after first fold ${paper.fold(firstFold).visibleDots()}")
    }
}
