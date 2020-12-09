import utils.combinations

class Day09 : Day(9, title = "Encoding Error") {

    private val data = inputAsLongs

    override fun part1() = data.windowed(26, 1).first { !it.isValid() }.last()

    private fun List<Long>.isValid(): Boolean {
        val x = last()
        val pre = dropLast(1)
        return pre.combinations(2).any { it.sum() == x }
    }

    override fun part2(): Long {
        val target = part1 as Long
        return data.indices.asSequence().map { start ->
            data.drop(start).runningFold(Triple(Long.MAX_VALUE, Long.MIN_VALUE, 0L)) { (min, max, sum), n ->
                Triple(min.coerceAtMost(n), max.coerceAtLeast(n), sum + n)
            }.takeWhile { (_, _, sum) -> sum <= target }.last()
        }.first { it.third == target }.let { it.first + it.second }
    }

}

fun main() {
    Day09().solve()
}