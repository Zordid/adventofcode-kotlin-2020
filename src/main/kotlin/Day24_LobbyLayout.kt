import utils.*

class Day24 : Day(24, title = "Lobby Layout") {

    private val directions = listOf("e", "se", "sw", "w", "nw", "ne")

    private val instructions = mappedInput { line ->
        val r = mutableListOf<String>()
        var l = line
        while (l.isNotEmpty()) {
            val d = directions.single { l.startsWith(it) }
            l = l.substring(d.length)
            r += d
        }
        r.toList()
    }

    override fun part1() = initiallyBlack().count()

    override fun part2(): Int {
        val initialGeneration = initiallyBlack()
        val seq = generateSequence(initialGeneration) { gen ->
            val area = gen.boundingArea()!!.grow()
            area.allPoints().filter { p ->
                val isBlack = p in gen
                val blackNeighbors = p.allHexNeighbors().count { it in gen }

                (isBlack && blackNeighbors != 0 && blackNeighbors <= 2) ||
                        (!isBlack && blackNeighbors == 2)
            }.toSet()
        }
        return seq.drop(100).first().count()
    }

    private fun initiallyBlack(): Set<Point> {
        val c = instructions.map { directions ->
            val target = directions.fold(origin) { coordinate, direction ->
                coordinate.gotoHex(direction)
            }
            target
        }.groupingBy { it }.eachCount()
        return c.filterValues { it % 2 != 0 }.keys
    }

    private fun Point.gotoHex(d: String): Point = when (d to (second % 2 != 0)) {
        "e" to true, "e" to false -> first + 1 to second
        "w" to true, "w" to false -> first - 1 to second

        "se" to true -> first + 1 to second + 1
        "sw" to true -> first to second + 1
        "nw" to true -> first to second - 1
        "ne" to true -> first + 1 to second - 1

        "se" to false -> first to second + 1
        "sw" to false -> first - 1 to second + 1
        "nw" to false -> first - 1 to second - 1
        "ne" to false -> first to second - 1

        else -> error(d)
    }

    private fun Point.allHexNeighbors() = directions.map { this.gotoHex(it) }

}

fun main() {
    Day24().solve()
}