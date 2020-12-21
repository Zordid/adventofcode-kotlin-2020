class Day15 : Day(15, title = "Rambunctious Recitation") {

    private val numbers = parsedInput { ints }[0].toIntArray()

    override fun part1() = game(numbers, 2020)

    override fun part2() = gameFast(numbers, 30000000)

    private fun game(startNumbers: IntArray, n: Int): Int {
        val stats = mutableMapOf<Int, Int>()

        startNumbers.dropLast(1).forEachIndexed { index, i -> stats[i] = index }

        return (startNumbers.lastIndex until n - 1).fold(startNumbers.last()) { lastSpoken, index ->
            val lastSeen = stats[lastSpoken]
            stats[lastSpoken] = index
            lastSeen?.let { index - it } ?: 0
        }
    }

    private fun gameFast(startNumbers: IntArray, n: Int): Int {
        val lastIndex = startNumbers.size - 1
        val stats = IntArray(29584775)
        for (index in 0 until lastIndex) {
            stats[startNumbers[index]] = index
        }
        val firstSpoken = startNumbers[0]
        var lastSpoken = startNumbers[lastIndex]
        for (index in lastIndex until n - 1) {
            val lastSeen = stats[lastSpoken].also { stats[lastSpoken] = index}
            lastSpoken = if (lastSeen > 0 || lastSpoken == firstSpoken) {
                index - lastSeen
            } else {
                0
            }
        }
        return lastSpoken
    }
}

fun main() {
    val day15 = Day15()
    day15.part2()
    day15.solve()
}