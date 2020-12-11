package utils

fun <T : Comparable<T>> Iterable<T>.minMaxOrNull(): Pair<T, T>? {
    val iterator = iterator()
    if (!iterator.hasNext()) return null
    var min = iterator.next()
    var max = min
    while (iterator.hasNext()) {
        val e = iterator.next()
        if (min > e) min = e
        if (e > max) max = e
    }
    return min to max
}

inline fun <T, R : Comparable<R>> Iterable<T>.minMaxByOrNull(selector: (T) -> R): Pair<T, T>? {
    val iterator = iterator()
    if (!iterator.hasNext()) return null
    var minElem = iterator.next()
    var maxElem = minElem
    if (!iterator.hasNext()) return minElem to maxElem
    var minValue = selector(minElem)
    var maxValue = minValue
    do {
        val e = iterator.next()
        val v = selector(e)
        if (minValue > v) {
            minElem = e
            minValue = v
        }
        if (v > maxValue) {
            maxElem = e
            maxValue = v
        }
    } while (iterator.hasNext())
    return minElem to maxElem
}

fun Iterable<Int>.rangeOrNull(): IntRange? = minMaxOrNull()?.let { it.first..it.second }
fun Iterable<Long>.rangeOrNull(): LongRange? = minMaxOrNull()?.let { it.first..it.second }

fun List<List<*>>.area(): Area = origin to (first().size - 1 to size - 1)

fun List<List<*>>.indices(): Sequence<Point> = sequence {
    for (y in this@indices.indices) {
        for (x in this@indices[y].indices)
            yield(x to y)
    }
}

inline fun <T> List<List<T>>.forArea(f: (p: Point, v: T) -> Unit) {
    for (y in this.indices)
        for (x in this[y].indices)
            f(x to y, this[y][x])
}

inline fun <T> List<List<T>>.forArea(f: (p: Point) -> Unit) {
    for (y in this.indices)
        for (x in this[y].indices)
            f(x to y)
}

operator fun <T> List<List<T>>.get(p: Point): T? =
    if (p.y in indices && p.x in this[p.y].indices) this[p.y][p.x] else null

operator fun <T> List<MutableList<T>>.set(p: Point, v: T) {
    if (p.y in indices && p.x in this[p.y].indices) this[p.y][p.x] = v
}

operator fun List<String>.get(p: Point): Char? =
    if (p.y in indices && p.x in this[p.y].indices) this[p.y][p.x] else null

fun <T> List<List<T>>.matchingIndices(predicate: (T) -> Boolean): List<Point> =
    flatMapIndexed { y, l -> l.mapIndexedNotNull { x, item -> if (predicate(item)) x to y else null } }


class EndlessList<T>(private val backingList: List<T>) : List<T> by backingList {
    init {
        check(backingList.isNotEmpty()) { "Cannot build an endless list from an empty list" }
    }

    override val size: Int
        get() = Int.MAX_VALUE

    override fun get(index: Int): T = backingList[index % backingList.size]

    override fun iterator(): Iterator<T> = backingList.asEndlessSequence().iterator()

    override fun listIterator(): ListIterator<T> = listIterator(0)

    override fun listIterator(index: Int): ListIterator<T> {
        TODO("Not yet implemented")
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        TODO("Not yet implemented")
    }

    override fun toString(): String = "inf$backingList"
}

fun <T> Iterable<T>.asEndlessSequence() = sequence { while (true) yieldAll(this@asEndlessSequence) }

fun <K, V> Map<K, V>.flip(): Map<V, K> = map { it.value to it.key }.toMap()

fun Iterable<Long>.product(): Long = reduce(Long::times)
fun Sequence<Long>.product(): Long = reduce(Long::times)
fun Iterable<Int>.productAsLong(): Long = fold(1L, Long::times)
fun Sequence<Int>.productAsLong(): Long = fold(1L, Long::times)
fun Iterable<Int>.product(): Int = productAsLong().checkedToInt()
fun Sequence<Int>.product(): Int = productAsLong().checkedToInt()

fun Long.checkedToInt(): Int = let {
    check(it in Int.MIN_VALUE..Int.MAX_VALUE) { "Value does not fit in Int: $it" }
    it.toInt()
}

/**
 * Returns a list containing the runs of equal elements and their respective count as Pairs.
 */
fun <T> Iterable<T>.runs(): List<Pair<T, Int>> {
    val iterator = iterator()
    if (!iterator.hasNext())
        return emptyList()
    val result = mutableListOf<Pair<T, Int>>()
    var current = iterator.next()
    var count = 1
    while (iterator.hasNext()) {
        val next = iterator.next()
        if (next != current) {
            result.add(current to count)
            current = next
            count = 0
        }
        count++
    }
    result.add(current to count)
    return result
}

fun <T> Iterable<T>.runsOf(e: T): List<Int> {
    val iterator = iterator()
    if (!iterator.hasNext())
        return emptyList()
    val result = mutableListOf<Int>()
    var count = 0
    while (iterator.hasNext()) {
        val next = iterator.next()
        if (next == e) {
            count++
        } else if (count > 0) {
            result.add(count)
            count = 0
        }
    }
    if (count > 0)
        result.add(count)
    return result
}

/**
 * Returns a sequence containing the runs of equal elements and their respective count as Pairs.
 */
fun <T> Sequence<T>.runs(): Sequence<Pair<T, Int>> = sequence {
    val iterator = iterator()
    if (iterator.hasNext()) {
        var current = iterator.next()
        var count = 1
        while (iterator.hasNext()) {
            val next: T = iterator.next()
            if (next != current) {
                yield(current to count)
                current = next
                count = 0
            }
            count++
        }
        yield(current to count)
    }
}

fun <T> Sequence<T>.runsOf(e: T): Sequence<Int> = runs().filter { it.first == e }.map { it.second }