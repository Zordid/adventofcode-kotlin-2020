class Day15 : Day(15, title = "Rambunctious Recitation") {

    private val numbers = parsedInput { ints }[0]

    override fun part1() = game(numbers, 2020)

    override fun part2() = game(numbers, 30000000)

    private fun game(startNumbers: List<Int>, n: Int): Int {
        val stats = mutableMapOf<Int, Int>()

        startNumbers.dropLast(1).forEachIndexed { index, i -> stats[i] = index }

        return (startNumbers.lastIndex until n - 1).fold(startNumbers.last()) { lastSpoken, index ->
            val lastSeen = stats[lastSpoken]
            stats[lastSpoken] = index
            lastSeen?.let { index - it } ?: 0
        }
    }
}

fun main() {
    Day15().solve()
}