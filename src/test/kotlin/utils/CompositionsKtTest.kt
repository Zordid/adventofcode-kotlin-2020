package utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CompositionsKtTest {

    @Test
    fun `weak compositions test`() {
        assertEquals(
            listOf(
                listOf(5, 0, 0),
                listOf(4, 1, 0),
                listOf(4, 0, 1),
                listOf(3, 2, 0),
                listOf(3, 1, 1),
                listOf(3, 0, 2),
                listOf(2, 3, 0),
                listOf(2, 2, 1),
                listOf(2, 1, 2),
                listOf(2, 0, 3),
                listOf(1, 4, 0),
                listOf(1, 3, 1),
                listOf(1, 2, 2),
                listOf(1, 1, 3),
                listOf(1, 0, 4),
                listOf(0, 5, 0),
                listOf(0, 4, 1),
                listOf(0, 3, 2),
                listOf(0, 2, 3),
                listOf(0, 1, 4),
                listOf(0, 0, 5),
            ), weakCompositionsOf(5, 3).toList()
        )
    }

    @Test
    fun `strict compositions test`() {
        assertEquals(
            listOf(
                listOf(5),
                listOf(4, 1),
                listOf(3, 2),
                listOf(3, 1, 1),
                listOf(2, 3),
                listOf(2, 2, 1),
                listOf(2, 1, 2),
                listOf(2, 1, 1, 1),
                listOf(1, 4),
                listOf(1, 3, 1),
                listOf(1, 2, 2),
                listOf(1, 2, 1, 1),
                listOf(1, 1, 3),
                listOf(1, 1, 2, 1),
                listOf(1, 1, 1, 2),
                listOf(1, 1, 1, 1, 1),
            ), compositionsOf(5).toList()
        )
    }

    @Test
    fun `strict compositions test with minimum size`() {
        assertEquals(
            listOf(
                listOf(3, 1, 1),
                listOf(2, 2, 1),
                listOf(2, 1, 2),
                listOf(2, 1, 1, 1),
                listOf(1, 3, 1),
                listOf(1, 2, 2),
                listOf(1, 2, 1, 1),
                listOf(1, 1, 3),
                listOf(1, 1, 2, 1),
                listOf(1, 1, 1, 2),
                listOf(1, 1, 1, 1, 1),
            ), compositionsOf(5, 3).toList()
        )
    }

    @Test
    fun `strict compositions test with limits`() {
        assertEquals(
            emptyList<Int>(), compositionsOf(5, maxParts = 0).toList()
        )

        assertEquals(
            listOf(
                listOf(5),
            ), compositionsOf(5, maxParts = 1).toList()
        )

        assertEquals(
            listOf(
                listOf(5),
                listOf(4, 1),
                listOf(3, 2),
                listOf(2, 3),
                listOf(1, 4),
            ), compositionsOf(5, maxParts = 2).toList()
        )

        assertEquals(
            listOf(
                listOf(5),
                listOf(4, 1),
                listOf(3, 2),
                listOf(3, 1, 1),
                listOf(2, 3),
                listOf(2, 2, 1),
                listOf(2, 1, 2),
                listOf(1, 4),
                listOf(1, 3, 1),
                listOf(1, 2, 2),
                listOf(1, 1, 3),
            ), compositionsOf(5, maxParts = 3).toList()
        )
    }

    @Test
    fun `strict compositions test with restricted size`() {
        assertEquals(
            listOf(
                listOf(4, 1),
                listOf(3, 2),
                listOf(3, 1, 1),
                listOf(2, 3),
                listOf(2, 2, 1),
                listOf(2, 1, 2),
                listOf(1, 4),
                listOf(1, 3, 1),
                listOf(1, 2, 2),
                listOf(1, 1, 3),
            ), compositionsOf(5, 2, 3).toList()
        )
    }

    @Test
    fun `k-compositions test`() {
        assertEquals(
            listOf(
                listOf(3, 1, 1),
                listOf(2, 2, 1),
                listOf(2, 1, 2),
                listOf(1, 3, 1),
                listOf(1, 2, 2),
                listOf(1, 1, 3),
            ), kCompositionsOf(5, 3).toList()
        )
    }

    @Test
    fun `compositions test wrong bounds`() {
        assertEquals(
            emptyList<Int>(), compositionsOf(5, 3, 2).toList()
        )
    }

    @Test
    fun `partitions test`() {
        assertEquals(
            listOf(
                listOf(5),
                listOf(4, 1),
                listOf(3, 2),
                listOf(3, 1, 1),
                listOf(2, 2, 1),
                listOf(2, 1, 1, 1),
                listOf(1, 1, 1, 1, 1),
            ), partitionsOf(5).toList()
        )
    }

    @Test
    fun `partitions test with min parts`() {
        assertEquals(
            listOf(
                listOf(3, 1, 1),
                listOf(2, 2, 1),
                listOf(2, 1, 1, 1),
                listOf(1, 1, 1, 1, 1),
            ), partitionsOf(5, 3).toList()
        )
    }

    @Test
    fun `partitions test with limits`() {
        assertEquals(
            listOf(
                listOf(5),
                listOf(4, 1),
                listOf(3, 2),
                listOf(3, 1, 1),
                listOf(2, 2, 1),
                listOf(2, 1, 1, 1),
            ), partitionsOf(5, maxParts = 4).toList()
        )

        assertEquals(
            listOf(
                listOf(5),
                listOf(4, 1),
                listOf(3, 2),
                listOf(3, 1, 1),
                listOf(2, 2, 1),
            ), partitionsOf(5, 0, 3).toList()
        )

        assertEquals(
            listOf(
                listOf(5),
                listOf(4, 1),
                listOf(3, 2),
            ), partitionsOf(5, 0, 2).toList()
        )

        assertEquals(
            listOf(
                listOf(5),
            ), partitionsOf(5, 0, 1).toList()
        )
    }

    @Test
    fun `k-partitions test`() {
        assertEquals(
            listOf(
                listOf(3, 1, 1),
                listOf(2, 2, 1),
            ), kPartitionsOf(5, 3).toList()
        )
    }

    @Test
    fun `restricted compositions test`() {
        assertEquals(
            emptyList<Int>(),
            restrictedPartitionsOf(5, listOf(8, 9, 10)).toList()
        )

        assertEquals(
            listOf(
                listOf(5),
                listOf(4, 1),
                listOf(3, 2),
                listOf(2, 3),
                listOf(1, 4),
            ), restrictedCompositionsOf(5, (1..5).toList()).toList()
        )

        assertEquals(
            listOf(
                listOf(5),
                listOf(4, 1),
                listOf(3, 2),
            ), restrictedPartitionsOf(5, (1..5).toList()).toList()
        )

        assertEquals(
            listOf(
                listOf(20, 5),
                listOf(20, 5),
                listOf(15, 10),
                listOf(15, 5, 5),
            ), restrictedPartitionsOf(25, listOf(20, 15, 10, 5, 5)).toList()
        )
    }

    @Test
    fun `restricted compositions test with limits`() {
        repeat(20) {
            val n = it + 1
            repeat(6) { limit ->
                assertEquals(compositionsOf(n, 0, limit).toList(), restrictedCompositionsOf(n, 1..n, 0, limit).toList())
            }
        }


        assertEquals(
            emptyList<Int>(),
            restrictedCompositionsOf(5, 8..10).toList()
        )

        assertEquals(
            listOf(
                listOf(3, 2),
                listOf(3, 1, 1),
                listOf(2, 3),
                listOf(2, 2, 1),
                listOf(2, 1, 2),
                listOf(2, 1, 1, 1),
                listOf(1, 3, 1),
                listOf(1, 2, 2),
                listOf(1, 2, 1, 1),
                listOf(1, 1, 3),
                listOf(1, 1, 2, 1),
                listOf(1, 1, 1, 2),
                listOf(1, 1, 1, 1, 1),
            ),
            restrictedCompositionsOf(5, 1..3).toList()
        )

        assertEquals(
            emptyList<Int>(),
            compositionsOf(0).toList()
        )
        assertEquals(
            emptyList<Int>(),
            restrictedCompositionsOf(0, 1..10).toList()
        )


        assertEquals(
            listOf(
                listOf(5),
                listOf(4, 1),
                listOf(3, 2),
                listOf(2, 3),
                listOf(1, 4),
            ), compositionsOf(5, maxParts = 2).toList()
        )

        assertEquals(
            listOf(
                listOf(5),
                listOf(4, 1),
                listOf(3, 2),
                listOf(3, 1, 1),
                listOf(2, 3),
                listOf(2, 2, 1),
                listOf(2, 1, 2),
                listOf(1, 4),
                listOf(1, 3, 1),
                listOf(1, 2, 2),
                listOf(1, 1, 3),
            ), compositionsOf(5, maxParts = 3).toList()
        )
    }


}