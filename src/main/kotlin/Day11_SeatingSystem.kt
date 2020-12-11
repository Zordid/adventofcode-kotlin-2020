import utils.*

typealias SeatMap = Grid<Char>

class Day11 : Day(11, title = "Seating System") {

    val seatMap: SeatMap = mappedInput { it.toList() }

    override fun part1() = sequencePart1().last().sumOf { it.count { it == OCCUPIED } }

    fun sequencePart1() = conwaySequence(seatMap, ::rulesPart1)

    private fun rulesPart1(map: SeatMap, p: Point, here: Char): Char? {
        if (here == NO_SEAT) return null
        val occupied = p.surroundingNeighbors().count { n -> map[n] == OCCUPIED }
        return when {
            here == EMPTY && occupied == 0 -> OCCUPIED
            here == OCCUPIED && occupied >= 4 -> EMPTY
            else -> null
        }
    }

    override fun part2() = sequencePart2().last().sumOf { it.count { it == OCCUPIED } }

    fun sequencePart2() = conwaySequence(seatMap, ::rulesPart2)

    private fun rulesPart2(map: SeatMap, p: Point, here: Char): Char? {
        if (here == NO_SEAT) return null
        val occupied =
            Direction8.allVectors.count { v ->
                val lineOfSight = generateSequence(p + v) { it + v }
                val visibleSeat =
                    lineOfSight.map { map[it] }.dropWhile { it == NO_SEAT }.firstOrNull()
                visibleSeat == OCCUPIED
            }
        return when {
            here == EMPTY && occupied == 0 -> OCCUPIED
            here == OCCUPIED && occupied >= 5 -> EMPTY
            else -> null
        }
    }

    companion object {
        const val EMPTY = 'L'
        const val OCCUPIED = '#'
        const val NO_SEAT = '.'
    }
}

fun main() {
    Day11().solve()
}