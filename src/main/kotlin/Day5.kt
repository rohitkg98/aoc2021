import kotlin.math.abs

data class Point(val x: Int, val y: Int)

data class Line(val start: Point, val end: Point) {
    fun isHorizontalOrVertical(): Boolean {
        return start.x == end.x || start.y == end.y
    }

    fun points(): List<Point> {
        return when {
            start.x == end.x && start.y >= end.y -> (end.y..start.y).map { Point(start.x, it) }
            start.x == end.x -> (start.y..end.y).map { Point(start.x, it) }

            start.y == end.y && start.x >= end.x -> (end.x..start.x).map { Point(it, start.y) }
            start.y == end.y -> (start.x..end.x).map { Point(it, start.y) }

            else -> {
                val step = Point(if (start.x < end.x) 1 else -1, if (start.y < end.y) 1 else -1)

                (0..abs(start.x - end.x)).map { Point(start.x + it * step.x, start.y + it * step.y) }
            }
        }
    }
}

class Plane(lines: List<Line>) {
    private val markedLines = Array(1000) { IntArray(1000) }

    operator fun set(p: Point, i: Int) {
        markedLines[p.x][p.y] = i
    }

    operator fun get(p: Point): Int {
        return markedLines[p.x][p.y]
    }

    fun getMoreThanTwiceVisited(): Int {
        return markedLines.flatMap { it.filter { i -> i >= 2 } }.size
    }
}

object Day5 : Day() {
    override fun main() {
        val ventLines = this.readInput("day5.txt").split("\n").map {
            val (start, end) = it.split(" -> ")
            val (startX, startY) = start.split(",").map { it.toInt() }
            val (endX, endY) = end.split(",").map { it.toInt() }

            Line(Point(startX, startY), Point(endX, endY))
        }

        part1(ventLines)

        part2(ventLines)
    }

    private fun part2(ventLines: List<Line>) {
        val plane = Plane(ventLines)

        ventLines.forEach { line -> line.points().forEach { p -> plane[p] += 1 } }

        println("Total lines with more than two visits ${plane.getMoreThanTwiceVisited()}")
    }

    private fun part1(ventLines: List<Line>) {
        val plane = Plane(ventLines)

        ventLines.filter { it.isHorizontalOrVertical() }.forEach { line ->
            line.points().forEach { p -> plane[p] += 1 }
        }

        println("Total vertical/horizontal lines with more than 2 visits ${plane.getMoreThanTwiceVisited()}")
    }
}
