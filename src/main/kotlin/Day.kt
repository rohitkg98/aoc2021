abstract class Day {
    fun readInput(fileName: String): String {
        return this.javaClass.classLoader.getResource(fileName)!!.readText()
    }

    abstract fun main()
}
