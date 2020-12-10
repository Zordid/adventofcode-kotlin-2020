package utils

// https://en.wikipedia.org/wiki/Composition_(combinatorics)

/**
 * In mathematics, a composition of an integer n is a way of writing n as the sum of a sequence of (strictly)
 * positive integers.
 * @param n the number to be composed
 * @param minParts the minimum number of parts for the compositions created
 * @param maxParts the maximum number of parts for the compositions created
 */
fun compositionsOf(n: Int, minParts: Int = 0, maxParts: Int = Int.MAX_VALUE): Sequence<List<Int>> =
    when {
        minParts > maxParts || maxParts <= 0 || minParts < 0 -> emptySequence()
        n <= 0 -> emptySequence()
        n < minParts -> emptySequence()
        maxParts == 1 -> sequenceOf(listOf(n))
        else -> sequence {
            val newMin = (minParts - 1).coerceAtLeast(0)
            val newMax = maxParts - 1
            (n downTo 1).forEach { v ->
                if (n == v && newMin == 0)
                    yield(listOf(n))
                else
                    compositionsOf(n - v, newMin, newMax).forEach { yield(listOf(v) + it) }
            }
        }
    }

fun kCompositionsOf(n: Int, k: Int): Sequence<List<Int>> = compositionsOf(n, k, k)

fun restrictedCompositionsOf(
    n: Int,
    restriction: IntRange,
    minParts: Int = 0,
    maxParts: Int = Int.MAX_VALUE
): Sequence<List<Int>> =
    when {
        minParts > maxParts || maxParts <= 0 || minParts < 0 -> emptySequence()
        n <= 0 -> emptySequence()
        n < minParts * restriction.first.coerceAtLeast(1) -> emptySequence()
        maxParts == 1 -> if (n in restriction) sequenceOf(listOf(n)) else emptySequence()
        else -> sequence {
            val newMin = (minParts - 1).coerceAtLeast(0)
            val newMax = maxParts - 1
            (n.coerceAtMost(restriction.last) downTo restriction.first.coerceAtLeast(1)).forEach { v ->
                if (v == n && newMin == 0)
                    yield(listOf(n))
                else
                    restrictedCompositionsOf(n - v, restriction, newMin, newMax).forEach { yield(listOf(v) + it) }
            }
        }
    }

fun restrictedCompositionsOf(n: Int, parts: List<Int>): Sequence<List<Int>> {
    val availableParts = parts.toMutableList().apply { sortDescending() }

    fun restrictedCompositions(n: Int): Sequence<List<Int>> = when {
        n <= 0 -> emptySequence()
        availableParts.isEmpty() -> emptySequence()
        availableParts.sum() < n -> emptySequence()
        else -> sequence {
            val possible = availableParts.filter { it <= n }
            possible.forEach { v ->
                if (v == n)
                    yield(listOf(n))
                else {
                    availableParts -= v
                    restrictedCompositions(n - v).forEach { yield(listOf(v) + it) }
                    availableParts += v
                }
            }
        }
    }

    return restrictedCompositions(n)
}

/**
 * In mathematics, a composition of an integer n is a way of writing n as the sum of a sequence of (strictly)
 * positive integers. Two sequences that differ in the order of their terms define different compositions of
 * their sum, while they are considered to define the same partition of that number.
 * @param n the number to be composed
 * @param maxParts the maximum number of parts for the partitions created
 * @param maxN limit the highest used number to maxN
 */
fun partitionsOf(n: Int, minParts: Int = 0, maxParts: Int = Int.MAX_VALUE, maxN: Int = n): Sequence<Collection<Int>> =
    when {
        minParts > maxParts || maxParts <= 0 || minParts < 0 -> emptySequence()
        n <= 0 -> emptySequence()
        n < minParts -> emptySequence()
        maxParts == 1 -> if (n <= maxN) sequenceOf(listOf(n)) else emptySequence()
        else -> sequence {
            val newMin = (minParts - 1).coerceAtLeast(0)
            val newMax = maxParts - 1
            (n.coerceAtMost(maxN) downTo 1).forEach { v ->
                if (v == n && newMin == 0)
                    yield(listOf(n))
                else {
                    partitionsOf(n - v, newMin, newMax, v).forEach { yield(listOf(v) + it) }
                }
            }
        }
    }

fun kPartitionsOf(n: Int, k: Int): Sequence<Collection<Int>> = partitionsOf(n, k, k)

fun restrictedPartitionsOf(n: Int, parts: List<Int>): Sequence<List<Int>> {

    fun restrictedPartitions(n: Int, availableParts: List<Int>): Sequence<List<Int>> = when {
        n <= 0 -> sequenceOf(emptyList())
        availableParts.sum() < n -> emptySequence()
        availableParts.last() > n -> emptySequence()
        else -> sequence {
            val firstFitting = availableParts.indexOfFirst { it <= n }
            (firstFitting until availableParts.size).forEach { idx ->
                val v = availableParts[idx]
                restrictedPartitions(n - v, availableParts.subList(idx + 1, availableParts.size)).forEach {
                    yield(listOf(v) + it)
                }
            }
        }
    }

    return restrictedPartitions(n, parts.sortedDescending())
}

/**
 * A weak composition of an integer n is similar to a composition of n, but allowing terms of the sequence
 * to be zero: it is a way of writing n as the sum of a sequence of non-negative integers.
 * @param n the number to be composed
 * @param k the number of parts for the weak compositions created
 * @param maxN limit the highest used number to maxN
 */
fun weakCompositionsOf(n: Int, k: Int, maxN: Int = n): Sequence<List<Int>> =
    when {
        k <= 0 -> emptySequence()
        k == 1 -> if (n <= maxN) sequenceOf(listOf(n)) else emptySequence()
        else -> sequence {
            (n.coerceAtMost(maxN) downTo 0).forEach { v ->
                weakCompositionsOf(n - v, k - 1, maxN).forEach { yield(listOf(v) + it) }
            }
        }
    }

