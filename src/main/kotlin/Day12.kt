const val startCaveId = "start"
const val endCaveId = "end"

data class Cave(val id: String) {
    fun isBig() = id.matches("[A-Z]+".toRegex())
    fun isStart() = id == startCaveId
}

data class Path(val start: Cave, val end: Cave) {
    fun invert() = Path(end, start)
}

data class Graph(val paths: List<Path>, val visited: Map<Cave, Int> = mapOf()) {
    private val connections =
        paths.flatMap { listOf(it.start to it, it.end to it.invert()) }.groupBy({ it.first }, { it.second })

    fun visit(cave: Cave) = if (cave.isBig()) this
    else copy(visited = visited + (cave to visited.getOrDefault(cave, 0) + 1))

    fun isVisited(cave: Cave) = visited.containsKey(cave)

    fun isVisitedTwice(cave: Cave) = cave.isStart() || visited.getOrDefault(cave, 0) == 2

    fun isEvenOneVisitedTwice() = visited.containsValue(2)

    fun findPaths(start: Cave, end: Cave): List<List<Path>> {
        return connections.getOrDefault(start, listOf()).flatMap { availablePath ->
            val pathsFound = when {
                availablePath.end == end -> listOf(listOf(availablePath))
                this.isVisited(availablePath.end) -> emptyList()
                else -> this.visit(availablePath.start).visit(availablePath.end).findPaths(availablePath.end, end)
            }
            pathsFound.map { it + availablePath }
        }
    }

    fun findPathsWithSingleTwice(start: Cave, end: Cave): List<List<Path>> {
        return connections.getOrDefault(start, listOf()).flatMap { availablePath ->
            val pathsFound = when {
                availablePath.end == end -> listOf(listOf(availablePath))
                isVisitedTwice(availablePath.end) -> emptyList()
                isVisited(availablePath.end) && isEvenOneVisitedTwice() -> emptyList()
                else -> this.visit(availablePath.end).findPathsWithSingleTwice(availablePath.end, end)
            }
            pathsFound.map { it + availablePath }
        }
    }
}

object Day12 : Day() {
    override fun main() {
        val paths = this.readInput("day12.txt").split("\n").map { path ->
            val (start, end) = path.split("-")
            Path(Cave(start), Cave(end))
        }

        val graph = Graph(paths)


        part1(graph)

        part2(graph)
    }

    private fun part2(graph: Graph) {
        val totalPaths = graph.findPathsWithSingleTwice(Cave(startCaveId), Cave(endCaveId)).size

        println("total paths from start to end with one twice visit $totalPaths")
    }

    private fun part1(graph: Graph) {
        val totalPaths = graph.findPaths(Cave(startCaveId), Cave(endCaveId)).size

        println("total paths from start to end $totalPaths")
    }
}
