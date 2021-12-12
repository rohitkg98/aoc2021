fun main(args: Array<String>) {
    val dayToExe: Map<String, Day> = mapOf(
        "1" to Day1,
        "2" to Day2,
        "3" to Day3
    )

    dayToExe[args[0]]?.main()
}