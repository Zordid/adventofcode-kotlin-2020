import utils.productAsLong
import utils.restrictedCompositionsOf
import utils.runsOf

class Day10 : Day(10, title = "Adapter Array") {

    private val adapterRatings = inputAsInts
    private val maxRating = (adapterRatings.maxOrNull()!! + 3).show("Max Jolt")
    val allRatings = (adapterRatings + listOf(0, maxRating)).sorted()
    private val joltDifferences = allRatings.windowed(2, 1).map { it[1] - it[0] }

    override fun part1() = joltDifferences.count { it == 1 } * joltDifferences.count { it == 3 }

    override fun part2() = joltDifferences.runsOf(1).map { numberOfPossibleWays(it) }.productAsLong()

    private fun numberOfPossibleWays(length: Int) =
        restrictedCompositionsOf(length, restriction = 1..3).count()

    // the "brute force" path finding using a cache works, too
    private val cache = mutableMapOf<Int, Long>()

    private fun countWays(inJolts: Int = 0): Long {
        if (inJolts == maxRating) return 1L
        val fits = allRatings.filter { rating -> inJolts in rating - 3 until rating }
        return fits.sumOf { cache.getOrPut(it) { countWays(it) } }
    }

    fun part2BruteForce() = countWays()

}

fun main() {
    with(Day10()) {
        solve()
        runWithTiming("2 - using plain recursive path calculation") { part2BruteForce() }
    }
}
