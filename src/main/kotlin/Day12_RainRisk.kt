import utils.*
import utils.Direction4.*

class Day12 : Day(12, title = "Rain Risk") {

    private val navInstructions = parsedInput { it[0] to ints[0] }

    private val directions = mapOf('N' to NORTH, 'S' to SOUTH, 'W' to WEST, 'E' to EAST)

    override fun part1(): Int =
        navInstructions.fold(origin to EAST) { (pos, direction), (cmd, n) ->
            when (cmd) {
                'F' -> pos + direction * n to direction
                'L' -> pos to direction.applyTimes(n / 90) { it.left }
                'R' -> pos to direction.applyTimes(n / 90) { it.right }
                else -> pos + directions[cmd]!! * n to direction
            }
        }.first.manhattanDistance

    override fun part2(): Int =
        navInstructions.fold(origin to (10 to -1)) { (ship, waypoint), (cmd, n) ->
            when (cmd) {
                'F' -> ship + waypoint * n to waypoint
                'L' -> ship to waypoint.rotateLeft90(times = n / 90)
                'R' -> ship to waypoint.rotateRight90(times = n / 90)
                else -> ship to waypoint + directions[cmd]!! * n
            }
        }.first.manhattanDistance

}

fun main() {
    Day12().solve()
}