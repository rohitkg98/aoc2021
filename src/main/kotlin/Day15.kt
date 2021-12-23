import java.util.*

data class Vertex(val coOrd: CoOrdinate, val distance: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vertex

        return coOrd == other.coOrd
    }

    override fun hashCode() = coOrd.hashCode()
}

class AdjacencyMatrix(val edges: List2D<Int>) {
    fun findMinimumPathFromEntry(end: CoOrdinate): Int {
        val distances = mutableMapOf(CoOrdinate(0, 0) to 0)
        val queue: PriorityQueue<Vertex> = PriorityQueue(edges.lastIndex * edges[0].lastIndex) { o1, o2 ->
            o1.distance.compareTo(o2.distance)
        }

        queue.add(Vertex(CoOrdinate(0, 0), 0))

        while (queue.isNotEmpty()) {
            val (vertex, dist) = queue.poll()

            edges.getAdjacentCoOrdinates(vertex).forEach { next ->
                val distToNext = dist + edges[next]
                if (distToNext < distances.getOrDefault(next, Int.MAX_VALUE)) {
                    queue.remove(Vertex(next, distances.getOrDefault(next, Int.MAX_VALUE)))
                    queue.add(Vertex(next, distToNext))
                    distances[next] = distToNext
                }
            }
        }

        return distances.getOrDefault(end, Int.MAX_VALUE)
    }
}

private fun incrementByOne(list: List<Int>) = list.map { it.incRound(9) }

private fun Int.incRound(max: Int) = if (this >= max) 1 else this + 1

object Day15 : Day() {
    override fun main() {
        val edges = this.readInput("day15.txt").split("\n")
            .map { it.map { c -> c.toString().toInt() } }

        part1(edges)

        part2(edges)
    }

    private fun part2(edges: List<List<Int>>) {
        val incremented = (0..8).fold(listOf(edges)) { acc, _ -> acc + listOf(acc.last().map { incrementByOne(it) }) }

        val merged5x5Matrix = (0..4).map { x ->
            incremented[x] + incremented[x + 1] + incremented[x + 2] + incremented[x + 3] + incremented[x + 4]
        }.reduce { acc, list -> acc.verticalMerge(list) }

        val graph = AdjacencyMatrix(merged5x5Matrix)

        val minDist = graph.findMinimumPathFromEntry(graph.edges.lastCoOrdinate())

        println("minimum distance from entry to end for 5x5 graph: $minDist")
    }


    private fun part1(edges: List<List<Int>>) {
        val graph = AdjacencyMatrix(edges)

        val minDist = graph.findMinimumPathFromEntry(graph.edges.lastCoOrdinate())

        println("minimum distance from entry to end: $minDist")
    }
}
