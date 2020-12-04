import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class Day03Test {

    val demoData = """
        ..##.......
        #...#...#..
        .#....#..#.
        ..#.#...#.#
        .#...##..#.
        ..#.##.....
        .#.#.#....#
        .#........#
        #.##...#...
        #...##....#
        .#..#...#.#
    """.trimIndent()

    @Test
    fun demo() {
        globalTestData = demoData
        with(Day03()) {
            assertEquals(7, part1)
            assertEquals(336L, part2)
        }
    }

    @Test
    fun part1() {
        assertEquals(195, Day03().part1)
    }

    @Test
    fun part2() {
        assertEquals(3772314000L, Day03().part2)
    }

}