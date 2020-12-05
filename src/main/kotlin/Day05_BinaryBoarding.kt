class Day05 : Day(5, title = "Binary Boarding") {

    val boardingPasses = mappedInput { it.extractRow() * 8 + it.extractCol() }

    fun String.extractRow() = take(7).binarySpace(0..127, 'F')

    fun String.extractCol() = drop(7).binarySpace(0..7, 'L')

    fun String.binarySpace(startRange: IntRange, lowId: Char) = fold(startRange) { range, c ->
        val halfWidth = ((range.last - range.first) + 1) / 2
        if (c == lowId) range.first until range.first + halfWidth
        else range.first + halfWidth..range.last
    }.first

    override fun part1() = boardingPasses.maxOrNull()!!

    override fun part2() =
        (1 until part1()).first {
            it !in boardingPasses
                    && (it - 1) in boardingPasses
                    && (it + 1) in boardingPasses
        }

}

fun main() {
    Day05().solve()
}