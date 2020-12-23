import utils.productAsLong
import utils.rangeOrNull

class Day23 : Day(23, title = "Crab Cups") {

    private val cups = inputAsString.toList().map { it.toString().toInt() }.show("Cups")
    private val cupValueRange = cups.rangeOrNull()!!

    override fun part1(): String {
        val circle = CircularList.createFrom(cups)
        shuffleTheCups(100, circle, cupValueRange) { current, value ->
            current.firstOrNull(value)!!
        }
        return circle.firstOrNull(1)!!.next.toList().joinToString("")
    }

    override fun part2(): Long {
        val circle = CircularList.createFrom(cups)

        val quickAccess = Array(1_000_001) { circle }
        circle.forEach { quickAccess[it.value] = it }

        val highest = cupValueRange.last
        (highest + 1..1_000_000).forEach { n ->
            circle.prev.insertAfter(CircularList(n))
            quickAccess[n] = circle.prev
        }

        val valueRange = cupValueRange.first..1_000_000
        shuffleTheCups(10_000_000, circle, valueRange) { _, v -> quickAccess[v] }
        return quickAccess[1].next.toList(2).productAsLong()
    }

    private inline fun shuffleTheCups(
        times: Int,
        cups: CircularList,
        range: IntRange,
        search: (CircularList, Int) -> CircularList
    ) {
        var current = cups
        repeat(times) {
//            println("\n-- move ${it + 1} --")
//            println("cups: (${current.v}) ${current.toList().drop(1).joinToString(" ")}")
            val label: Int = current.value

            val picked = current.removeAfter(3)
//            println("pick up: ${picked.toList().joinToString(" ")}")

            var destinationValue = label - 1
            if (destinationValue < range.first) destinationValue = range.last
            while (destinationValue in picked) {
                destinationValue--
                if (destinationValue < range.first) destinationValue = range.last
            }
//            println("destination: $destinationValue")

            val destination = search(current, destinationValue)
            destination.insertAfter(picked)

            current = current.next
        }
    }

}

class CircularList(val value: Int) {
    val next: CircularList get() = _next
    val prev: CircularList get() = _prev

    private var _next: CircularList = this
    private var _prev: CircularList = this

    fun insertAfter(e: CircularList) {
        this._next = e.also { e.prev._next = next }
        e.next._prev = e.prev.also { e._prev = this }
    }

    fun removeAfter(count: Int = 1): CircularList {
        val cutFrom = next
        var cutTo = this
        repeat(count) { cutTo = cutTo.next }
        _next = cutTo.next
        cutTo.next._prev = this
        cutFrom._prev = cutTo
        cutTo._next = cutFrom
        return cutFrom
    }

    fun firstOrNull(target: Int): CircularList? {
        var current = this
        do {
            if (current.value == target) return current
            current = current.next
        } while (current != this)
        return null
    }

    operator fun contains(target: Int): Boolean {
        var current = this
        do {
            if (current.value == target) return true
            current = current.next
        } while (current != this)
        return false
    }

    inline fun forEach(lbd: (CircularList) -> Unit) {
        var current = this
        do {
            lbd(current)
            current = current.next
        } while (current != this)
    }

    fun asSequence(): Sequence<Int> = sequence {
        var current = this@CircularList
        while (true) {
            yield(current.value)
            current = current.next
        }
    }

    fun toList(limit: Int = -1): List<Int> {
        val result = if (limit != -1) ArrayList(limit) else mutableListOf<Int>()
        var current = this
        do {
            result += current.value
            current = current.next
        } while (current != this && result.size != limit)
        return result
    }

    companion object {
        fun createFrom(values: Collection<Int>): CircularList {
            val first = CircularList(values.first())
            var current = first
            values.drop(1).forEach { v ->
                CircularList(v).also {
                    current.insertAfter(it)
                    current = it
                }
            }
            return first
        }
    }

}

fun main() {
    Day23().solve()
    globalTestData = "389125467"
}