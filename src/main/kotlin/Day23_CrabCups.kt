import utils.productAsLong
import utils.rangeOrNull

class Day23 : Day(23, title = "Crab Cups") {

    private val cups = inputAsString.toList().map { it.toString().toInt() }.show("Cups")
    private val cupValueRange = cups.rangeOrNull()!!


    override fun part1(): String {
        val circle = Element.createFrom(cups)
        shuffleTheCups(100, circle, cupValueRange) { current, value ->
            current.firstOrNull(value)!!
        }
        return circle.firstOrNull(1)!!.toList().drop(1).joinToString("")
    }

    override fun part2(): Long {
        val circle = Element.createFrom(cups)

        val quickAccess = Array<Element?>(1_000_001) { null }
        circle.forEach { quickAccess[it.v] = it }

        val highest = cupValueRange.last
        var last = circle.prev
        (highest + 1..1_000_000).forEach { n ->
            Element(n).also {
                last.insertAfter(it)
                quickAccess[n] = it
                last = it
            }
        }

        val valueRange = cupValueRange.first..1_000_000
        shuffleTheCups(10_000_000, circle, valueRange) { _, v -> quickAccess[v]!! }
        return quickAccess[1]!!.next.toList(2).productAsLong()
    }

    private inline fun shuffleTheCups(times: Int, cups: Element, range: IntRange, search: (Element, Int) -> Element) {
        var current = cups
        repeat(times) { move ->
//            println("\n-- move ${move + 1} --")
//            println("cups: (${current.v}) ${current.toList().drop(1).joinToString(" ")}")
            val label: Int = current.v

            val picked = current.removeAfter(3)
            val pickedValues = picked.toList()
//            println("pick up: ${pickedValues.joinToString(" ")}")

            var destinationValue = label - 1
            if (destinationValue < range.first) destinationValue = range.last
            while (destinationValue in pickedValues) {
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

class Element(val v: Int) {
    val next: Element get() = _next ?: this
    val prev: Element get() = _prev ?: this

    private var _next: Element? = null
    private var _prev: Element? = null

    fun insertAfter(e: Element) {
        this._next = e.also { e.prev._next = next }
        e.next._prev = e.prev.also { e._prev = this }
    }

    fun removeAfter(count: Int): Element {
        val cutFrom = next
        var cutTo = this
        repeat(count) { cutTo = cutTo.next }
        _next = cutTo.next
        cutTo.next._prev = this
        cutFrom._prev = cutTo
        cutTo._next = cutFrom
        return cutFrom
    }

    fun firstOrNull(target: Int): Element? {
        var result = this
        while (result.v != target && result.next != this) {
            result = result.next
        }
        return if (result.v == target) result else null
    }

    inline fun forEach(lbd: (Element) -> Unit) {
        var current = this
        do {
            lbd(current)
            current = current.next
        } while (current != this)
    }

    fun toList(limit: Int? = null): List<Int> {
        val result = mutableListOf<Int>()
        var current = this
        do {
            result += current.v
            current = current.next
        } while (current != this && result.size != limit)
        return result
    }

    companion object {
        fun createFrom(values: Collection<Int>): Element {
            val first = Element(values.first())
            var current = first
            values.drop(1).forEach { v ->
                Element(v).also {
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