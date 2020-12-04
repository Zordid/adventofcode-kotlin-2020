package utils

import kotlin.math.absoluteValue
import kotlin.math.sign

typealias Point = Pair<Int, Int>
typealias Area = Pair<Point, Point>

val Point.x: Int
    get() = first

val Point.y: Int
    get() = second

val Point.manhattanDistance: Int
    get() = x.absoluteValue + y.absoluteValue

infix fun Point.manhattanDistanceTo(other: Point) = (this - other).manhattanDistance

fun Point.right(steps: Int = 1) = x + steps to y
fun Point.left(steps: Int = 1) = x - steps to y
fun Point.up(steps: Int = 1) = x to y - steps
fun Point.down(steps: Int = 1) = x to y + steps

fun Point.neighbor(direction: Direction, steps: Int = 1) = this + (direction.vector * steps)

/**
 * calculates the list of the four direct neighbors of the point.
 */
fun Point.neighbors(): List<Point> = Direction.values().map { neighbor(it) }
fun Point.surrounding(): List<Point> = toArea().grow().border().toList()

val origin = 0 to 0

infix operator fun Point.plus(other: Point) = x + other.x to y + other.y
infix operator fun Point.minus(other: Point) = x - other.x to y - other.y
infix operator fun Point.times(factor: Int) = when (factor) {
    0 -> origin
    1 -> this
    else -> x * factor to y * factor
}

infix operator fun Point.div(factor: Int) = when (factor) {
    1 -> this
    else -> x / factor to y / factor
}

operator fun Point.compareTo(other: Point): Int =
    if (y == other.y) x.compareTo(other.x) else y.compareTo(other.y)

fun Point.toArea(): Area = this to this

fun Area.isValid(): Boolean = first.x <= second.x && first.y <= second.y
fun Area.fix(): Area = if (isValid()) this else listOf(first, second).boundingArea()!!

fun Area.grow(by: Int = 1) = upperLeft.left(by).up(by) to lowerRight.right(by).down(by)
fun Area.shrink(by: Int = 1) = upperLeft.left(-by).up(-by) to lowerRight.right(-by).down(-by)

fun Area.isEmpty() = size == 0
val Area.size: Int
    get() = width * height

val Area.upperLeft: Point
    get() = first
val Area.lowerRight: Point
    get() = second
val Area.upperRight: Point
    get() = second.x to first.y
val Area.lowerLeft: Point
    get() = first.x to second.y

fun allPointsInArea(from: Point, to: Point): Sequence<Point> =
    listOf(from, to).boundingArea()!!.allPoints()

private val areaRegex = ".*?(\\d+)\\D+(\\d+)\\D+(\\d+)\\D+(\\d+).*".toRegex()

fun areaFromString(s: String): Area? =
    areaRegex.matchEntire(s)?.groupValues
        ?.let { (it[1].toInt() to it[2].toInt()) to (it[3].toInt() to it[4].toInt()) }

fun Area.allPoints(): Sequence<Point> = sequence { forEach { yield(it) } }
fun Area.border(): Sequence<Point> = sequence { forBorder { yield(it) } }
fun Area.corners(): Sequence<Point> =
    if (isEmpty())
        emptySequence()
    else
        listOf(upperLeft, upperRight, lowerRight, lowerLeft).distinct().asSequence()

inline fun Area.forEach(f: (p: Point) -> Unit) {
    for (y in first.y..second.y) {
        for (x in first.x..second.x) {
            f(x to y)
        }
    }
}

inline fun Area.forBorder(f: (p: Point) -> Unit) {
    for (y in first.y..second.y) {
        when (y) {
            first.y, second.y -> for (x in first.x..second.x) {
                f(x to y)
            }
            else -> {
                f(first.x to y)
                f(second.x to y)
            }
        }
    }
}

operator fun Area.contains(p: Point) = p.x in first.x..second.x && p.y in first.y..second.y

val Area.width: Int
    get() = (second.x - first.x + 1).coerceAtLeast(0)

val Area.height: Int
    get() = (second.y - first.y + 1).coerceAtLeast(0)

fun Iterable<Point>.boundingArea(): Area? {
    val (minX, maxX) = minMaxByOrNull { it.x } ?: return null
    val (minY, maxY) = minMaxByOrNull { it.y }!!
    return (minX.x to minY.y) to (maxX.x to maxY.y)
}

enum class Direction {
    UP, RIGHT, DOWN, LEFT;

    val right: Direction
        get() = values()[(this.ordinal + 1) % values().size]
    val left: Direction
        get() = values()[(this.ordinal - 1 + values().size) % values().size]
    val opposite: Direction
        get() = values()[(this.ordinal + 2) % values().size]
    val vector: Point
        get() = when (this) {
            UP -> 0 to -1
            DOWN -> 0 to 1
            LEFT -> -1 to 0
            RIGHT -> 1 to 0
        }

    companion object {
        fun ofVector(v: Point): Direction? =
            with(v) {
                when (x.sign to y.sign) {
                    0 to -1 -> UP
                    1 to 0 -> RIGHT
                    0 to 1 -> DOWN
                    -1 to 0 -> LEFT
                    else -> null
                }
            }
    }
}
