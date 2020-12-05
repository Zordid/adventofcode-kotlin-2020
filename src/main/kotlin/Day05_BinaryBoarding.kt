class Day05 : Day(5, title = "Binary Boarding") {

    val boardingPasses = mappedInput { it.toSeatId() }.sorted()

    fun String.toSeatId() = this
        .replace(Regex("[FL]"), "0")
        .replace(Regex("[BR]"), "1")
        .toInt(2)

    override fun part1() = boardingPasses.last()

    override fun part2() =
        (boardingPasses.first() until boardingPasses.last()).single { it !in boardingPasses }

}

fun main() {
    Day05().solve()
}