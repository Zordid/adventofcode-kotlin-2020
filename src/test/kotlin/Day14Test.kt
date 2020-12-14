import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class Day14Test {

    @Test
    fun demo1() {
        globalTestData = """
            mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X
            mem[8] = 11
            mem[7] = 101
            mem[8] = 0
        """.trimIndent()
        assertEquals(165L, Day14().part1)
    }

    @Test
    fun demo2() {
        globalTestData = """
            mask = 000000000000000000000000000000X1001X
            mem[42] = 100
            mask = 00000000000000000000000000000000X0XX
            mem[26] = 1
        """.trimIndent()
        assertEquals(208L, Day14().part2)
    }

    @Test
    fun part1() {
        assertEquals(17765746710228L, Day14().part1)
    }

    @Test
    fun part2() {
        assertEquals(4401465949086L, Day14().part2)
    }

}