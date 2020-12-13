
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class Day13Test {

    @Test
    fun part1() {
        Assertions.assertEquals(2995, Day13().part1)
    }

    @Test
    fun part2() {
        Assertions.assertEquals("1012171816131114", Day13().part2.toString())
    }

}