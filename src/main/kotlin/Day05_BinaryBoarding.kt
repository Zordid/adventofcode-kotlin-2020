import utils.rangeOrNull

class Day05 : Day(5, title = "Binary Boarding") {

    val boardingPassIDs = mappedInput { it.toSeatId() }

    fun String.toSeatId() = this
        .replace(Regex("[FL]"), "0")
        .replace(Regex("[BR]"), "1")
        .toInt(2)

    override fun part1() = boardingPassIDs.maxOrNull()

    override fun part2() = boardingPassIDs.rangeOrNull()?.single { it !in boardingPassIDs }

}

fun main() {
    Day05().solve()
}