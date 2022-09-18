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

fun conwaySequence(
    start: Set<Point>,
    ruleAlive: (p: Point, alive: Set<Point>) -> Boolean,
    ruleDead: (p: Point, alive: Set<Point>) -> Boolean
): Sequence<Set<Point>> =
    generateSequence(start) { prevGeneration ->
        val area = prevGeneration.boundingArea()?.grow()
        val nextGeneration = mutableSetOf<Point>()
        area?.forEach { p ->
            when (p in prevGeneration) {
                true -> if (ruleAlive(p, prevGeneration)) nextGeneration += p
                false -> if (ruleDead(p, prevGeneration)) nextGeneration += p
            }
        }
        if (nextGeneration.isNotEmpty() && nextGeneration != prevGeneration) nextGeneration else null
    }
