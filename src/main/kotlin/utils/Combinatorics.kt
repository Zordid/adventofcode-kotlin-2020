package utils

/**
 * Generates all combinations of the elements of the given list for the requested size.
 * @receiver the list to take elements from
 * @param size the size of the combinations to create
 * @return a sequence of all combinations
 */
fun <T> List<T>.combinations(size: Int): Sequence<Collection<T>> =
    when (size) {
        0 -> emptySequence()
        1 -> this@combinations.asSequence().map { listOf(it) }
        else -> sequence {
            this@combinations.forEachIndexed { index, element ->
                this@combinations.subList(index + 1, this@combinations.size).combinations(size - 1).forEach {
                    yield(listOf(element) + it)
                }
            }
        }
    }

