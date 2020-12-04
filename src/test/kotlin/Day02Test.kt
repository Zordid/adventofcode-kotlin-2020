import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class Day02Test {

    private val demoData = """
        1-3 a: abcde
        1-3 b: cdefg
        2-9 c: ccccccccc
    """.trimIndent()

    @Test
    fun demo() {
        globalTestData = demoData
        with(Day02()) {
            assertEquals(2, part1)
            assertEquals(1, part2)
        }
    }

    @Test
    fun part1() {
        assertEquals(447, Day02().part1)
    }

    @Test
    fun part2() {
        assertEquals(249, Day02().part2)
    }

}