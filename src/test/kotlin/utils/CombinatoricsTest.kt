package utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CombinatoricsTest {

    @Test
    fun simpleCombinationsOf2() {
        assertEquals(
            emptyList<Int>(),
            listOf(1).combinations(2).toList()
        )
        assertEquals(
            listOf(
                listOf(1, 2),
            ),
            listOf(1, 2).combinations(2).toList()
        )
        assertEquals(
            listOf(
                listOf(1, 2),
                listOf(1, 3),
                listOf(2, 3),
            ),
            listOf(1, 2, 3).combinations(2).toList()
        )
        assertEquals(
            listOf(
                listOf(1, 2),
                listOf(1, 3),
                listOf(1, 4),
                listOf(2, 3),
                listOf(2, 4),
                listOf(3, 4),
            ),
            listOf(1, 2, 3, 4).combinations(2).toList()
        )
    }


}