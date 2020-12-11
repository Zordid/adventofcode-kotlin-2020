import utils.*

typealias SeatMap = List<List<Char>>

class Day11 : Day(11, title = "Seating System") {

    private val seatMap: SeatMap = mappedInput { it.toList() }
    private val seatPositions = seatMap.matchingIndices { it != NO_SEAT }

    override fun part1(): Int {
        val generations = generateSequence(seatMap) { prev ->
            val next = prev.nextGeneration()
            if (next != prev) next else null
        }

        return generations.last().sumOf { it.count { it == OCCUPIED } }
    }

    private fun SeatMap.nextGeneration(): SeatMap {
        val result = this.copyMutable()
        seatPositions.forEach { p ->
            val occupied = p.surroundingNeighbors().count { n -> this[n] == OCCUPIED }
            if (this[p] == EMPTY && occupied == 0)
                result[p] = OCCUPIED
            else if (this[p] == OCCUPIED && occupied >= 4)
                result[p] = EMPTY
        }
        return result
    }

    override fun part2(): Int {
        val generations = generateSequence(seatMap) { prev ->
            val next = prev.nextGeneration2()
            if (next != prev) next else null
        }

        return generations.last().sumOf { it.count { it == OCCUPIED } }
    }

    private fun SeatMap.nextGeneration2(): SeatMap {
        val result = copyMutable()
        seatPositions.forEach { p ->
            val occupied =
                Direction8.allVectors.count { v ->
                    val lineOfSight = generateSequence(p + v) { it + v }
                    val visibleSeat =
                        lineOfSight.map { this[it] }.dropWhile { it == NO_SEAT }.firstOrNull()
                    visibleSeat == OCCUPIED
                }

            if (this[p] == EMPTY && occupied == 0)
                result[p] = OCCUPIED
            else if (this[p] == OCCUPIED && occupied >= 5)
                result[p] = EMPTY
        }
        return result
    }

    companion object {
        private const val EMPTY = 'L'
        private const val OCCUPIED = '#'
        private const val NO_SEAT = '.'
    }
}

fun main() {
    Day11().solve()
}