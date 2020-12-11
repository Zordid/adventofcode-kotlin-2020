package utils

typealias Grid<T> = List<List<T>>

fun <T> conwaySequence(
    start: Grid<T>,
    rule: (grid: Grid<T>, p: Point, v: T) -> T?
): Sequence<Grid<T>> =
    generateSequence(start) { prevGeneration ->
        val nextGeneration = prevGeneration.copyMutable()
        var anyChange = false
        prevGeneration.forArea { p, oldValue ->
            rule(prevGeneration, p, oldValue)?.also { newValue ->
                anyChange = anyChange || (oldValue != newValue)
                nextGeneration[p] = newValue
            }
        }
        if (anyChange) nextGeneration else null
    }

fun <T> Grid<T>.copyMutable() = List(this.size) { this[it].toMutableList() }