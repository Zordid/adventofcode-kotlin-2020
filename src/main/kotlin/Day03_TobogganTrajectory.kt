import utils.*

class Day03 : Day(3, title = "Toboggan Trajectory") {

    val grid = mappedInput { EndlessList(it.toList()) }
    val area = grid.area()

    fun trajectoryOf(slope: Point) =
        generateSequence(origin) { it + slope }

    override fun part1() =
        trajectoryOf(3 to 1).takeWhile { it in area }.count { grid[it] == '#' }

    override fun part2(): Long {
        val slopesToTest = listOf(1 to 1, 3 to 1, 5 to 1, 7 to 1, 1 to 2)
        return slopesToTest.map { slope ->
            trajectoryOf(slope).takeWhile { it in area }.count { grid[it] == '#' }
        }.productAsLong()
    }

}

fun main() {
    Day03().solve()
}