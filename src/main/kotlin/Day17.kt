import kotlin.math.abs

private data class Position(val x: Int, val y: Int) {
    fun willEverBeInRange(xRange: IntRange, yRange: IntRange): Boolean {
        var steps = 1

        while (!this.travel(steps).hasOvershot(xRange, yRange)) {
            val position = this.travel(steps)
            steps += 1

            if (position.x in xRange && position.y in yRange) return true
        }

        return false
    }

    fun travel(steps: Int) = Position(travelX(x, steps), travelY(y, steps))

    private fun travelX(start: Int, steps: Int) = nSum(start) - if (steps > start) 0 else nSum(start - steps)

    private fun travelY(start: Int, steps: Int) = (steps * ((2 * start) + (steps - 1) * -1)) / 2

    fun hasOvershot(xRange: IntRange, yRange: IntRange) = x > xRange.last || y < yRange.first
}

object Day17 : Day() {
    override fun main() {
        val (xRange, yRange) = readInput("day17.txt").split(", ")
            .map { it.split("=").last().split("..").map { it.toInt() } }

        val (minX, maxX) = xRange
        val (minY, maxY) = yRange

        // x dist is x * (x+1)/2
        // highest y point is y*(y+1)/2
        // highest y point falls on minimum possible y
        // y gains equivalent negative velocity as when shot up when it reaches back to 0
        // because it is shot up from zero, it will reach zero back
        // when it reaches zero back, it's negative velocity is same as start
        // now this velocity should be in range to make the distance travelled from 0 in given range, maximum possible
        // therefore abs(min(Y)) - 1 is our max shoot up speed

        val maxYVelocity = abs(minY) - 1

        println("max possible height is ${nSum(maxYVelocity)}")

        // 1 step - all points in the given range are possible velocities -> (deltaX + 1) * (deltaY + 1)
        // n steps - minX <= x + x - 1 <= maxX and so on
        // for all x found above, get compatible y

        val targetAreaPoints = (abs(maxX - minX) + 1) * (abs(maxY - minY) + 1)

        val allStartPositions = (6 until minX).flatMap { x ->
            (minY + 1..maxYVelocity).map { y -> Position(x, y) }
        }.filter { it.willEverBeInRange(minX..maxX, minY..maxY) }.toSet()


        println("total distinct points are ${targetAreaPoints + allStartPositions.size}")
    }

}
