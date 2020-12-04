import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class Day01Test {

    private val demoData = """
            1721
            979
            366
            299
            675
            1456
        """.trimIndent()

    @Test
    fun demo() {
        globalTestData = demoData
        with(Day01()) {
            assertEquals(514579, part1)
            assertEquals(241861950, part2)
        }
    }

    @Test
    fun part1() {
        assertEquals(539851, Day01().part1)
    }

    @Test
    fun part2() {
        assertEquals(212481360, Day01().part2)
    }

}