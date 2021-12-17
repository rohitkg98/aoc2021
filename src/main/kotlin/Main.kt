fun main(args: Array<String>) {
    val dayToExe: Map<String, Day> = mapOf(
        "1" to Day1,
        "2" to Day2,
        "3" to Day3,
        "4" to Day4,
        "5" to Day5,
        "6" to Day6,
        "7" to Day7,
        "8" to Day8,
        "9" to Day9,
    )

    dayToExe[args[0]]?.main()
}