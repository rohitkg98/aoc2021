class FishCounter(fishes: List<LanternFish>) {
    private val counts = LongArray(9)

    init {
        fishes.forEach { counts[it.timer] = counts[it.timer] + 1 }
    }

    fun tick() {
        val newFishes = counts[0]
        counts[0] = counts[1]
        counts[1] = counts[2]
        counts[2] = counts[3]
        counts[3] = counts[4]
        counts[4] = counts[5]
        counts[5] = counts[6]
        counts[6] = counts[7] + newFishes
        counts[7] = counts[8]
        counts[8] = newFishes
    }

    fun totalFish(): Long {
        return counts.sum()
    }
}

data class LanternFish(var timer: Int)

object Day6 : Day() {
    override fun main() {
        val allFish = this.readInput("day6.txt").trim().split(",").map { LanternFish(it.toInt()) }

        println("total fishes after 80 days are ${runSimulation(allFish, 80)}")

        println("total fishes after 256 days are ${runSimulation(allFish, 256)}")
    }

    private fun runSimulation(startingFish: List<LanternFish>, nDays: Int): Long {
        val fishCounter = FishCounter(startingFish)
        repeat(nDays) { fishCounter.tick() }

        return fishCounter.totalFish()
    }
}

