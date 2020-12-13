import utils.*
import utils.Direction4.EAST

class Day12 : Day(12, title = "Rain Risk") {

    val navInstructions = parsedInput { it[0] to ints[0] }

    override fun part1(): Int =
        navInstructions.fold(origin to EAST, operationPart1).first.manhattanDistance

    val operationPart1: (Pair<Point, Direction>, Pair<Char, Int>) -> Pair<Point, Direction> =
        { (pos, direction), (cmd, n) ->
            when (cmd) {
                'F' -> pos + direction * n to direction
                'L' -> pos to direction.applyTimes(n / 90) { it.left }
                'R' -> pos to direction.applyTimes(n / 90) { it.right }
                else -> pos + Direction4.interpret(cmd)!! * n to direction
            }
        }

    override fun part2(): Int =
        navInstructions.fold(origin to (10 to -1), operationPart2).first.manhattanDistance

    val operationPart2: (Pair<Point, Point>, Pair<Char, Int>) -> Pair<Point, Point> =
        { (ship, waypoint), (cmd, n) ->
            when (cmd) {
                'F' -> ship + waypoint * n to waypoint
                'L' -> ship to waypoint.rotateLeft90(times = n / 90)
                'R' -> ship to waypoint.rotateRight90(times = n / 90)
                else -> ship to waypoint + Direction4.interpret(cmd)!! * n
            }
        }

}

fun main() {
    Day12().solve()
}