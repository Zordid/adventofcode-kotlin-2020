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

inline fun <T> List<List<T>>.forArea(f: (p: Point, v: T) -> Unit) =
    indices().forEach { p -> f(p, this[p]!!) }

operator fun <T> List<List<T>>.get(p: Point): T? =
    if (p.y in indices && p.x in this[p.y].indices) this[p.y][p.x] else null

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

fun Iterable<Int>.product(): Int =
    productAsLong().let {
        check(it <= Int.MAX_VALUE) { "Product overflows Int range: $it" }
        it.toInt()
    }

fun Iterable<Long>.product(): Long = reduce(Long::times)
fun Iterable<Int>.productAsLong(): Long = fold(1L, Long::times)
