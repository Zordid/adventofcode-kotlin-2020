import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class Day21Test {

    val demo = """
        mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
        trh fvjkl sbzzf mxmxvkd (contains dairy)
        sqjhc fvjkl (contains soy)
        sqjhc mxmxvkd sbzzf (contains fish)
    """.trimIndent()

    @Test
    fun part1() {
        assertEquals(2211, Day21().part1)
    }

    @Test
    fun part2() {
        assertEquals("vv,nlxsmb,rnbhjk,bvnkk,ttxvphb,qmkz,trmzkcfg,jpvz", Day21().part2)
    }

    @Test
    fun demo1() {
        globalTestData = demo
        assertEquals(5, Day21().part1)
    }

    @Test
    fun demo2() {
        globalTestData = demo
        assertEquals("mxmxvkd,sqjhc,fvjkl", Day21().part2)
    }

}