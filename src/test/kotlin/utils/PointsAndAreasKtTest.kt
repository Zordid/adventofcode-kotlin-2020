package utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class PointsAndAreasKtTest {

    private fun Area.assertArea(emptiness: Boolean, width: Int, height: Int, border: Int) {
        println(this)
        assertEquals(emptiness, this.isEmpty())
        assertEquals(width, this.width)
        assertEquals(height, this.height)
        assertEquals(width * height, this.size)
        assertEquals(width * height, this.allPoints().count())
        assertEquals(border, this.border().count())
        this.allPoints().forEach { assertTrue(it in this) }
    }

    @Test
    fun someRandomAreaTests() {
        var a = Point(10, 10).toArea()

        a.assertArea(false, 1, 1, 1)

        a = a.grow()
        a.assertArea(false, 3, 3, 8)

        a = a.shrink()
        a.assertArea(false, 1, 1, 1)

        a = a.shrink()
        a.assertArea(true, 0, 0, 0)

        a = a.fix()
        a.assertArea(false, 3, 3, 8)

        a = a.grow()
        a.assertArea(false, 5, 5, 4 * 4)
    }

    @Test
    fun testInvalidAreas() {
        val a: Area = (Point(10, 10) to Point(5, 20)).fix()
        a.assertArea(false, 6, 11, 30)
    }


}