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
fun Point.directNeighbors(): List<Point> = Direction4.allVectors.map { this + it }
fun Point.surroundingNeighbors(): List<Point> = Direction8.allVectors.map { this + it }

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

fun Point.rotateLeft90(times: Int = 1): Point = when (times % 4) {
    0 -> this
    1 -> y to -x
    2 -> -x to -y
    3 -> -y to x
    else -> error("can't rotate $times timess")
}

fun Point.rotateRight90(times: Int = 1): Point = when (times % 4) {
    0 -> this
    1 -> -y to x
    2 -> -x to -y
    3 -> y to -x
    else -> error("can't rotate $times timess")
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

interface Direction {
    val name: String
    val right: Direction
    val left: Direction
    val opposite: Direction
    val vector: Point
}

operator fun Direction.times(n: Int): Point = vector * n

enum class Direction4 : Direction {
    NORTH, EAST, SOUTH, WEST;

    override val right: Direction4
        get() = values()[(this.ordinal + 1) % values().size]
    override val left: Direction4
        get() = values()[(this.ordinal - 1 + values().size) % values().size]
    override val opposite: Direction4
        get() = values()[(this.ordinal + 2) % values().size]
    override val vector: Point
        get() = when (this) {
            NORTH -> 0 to -1
            SOUTH -> 0 to 1
            WEST -> -1 to 0
            EAST -> 1 to 0
        }

    companion object {
        val UP = NORTH
        val RIGHT = EAST
        val DOWN = SOUTH
        val LEFT = WEST
        fun ofVector(v: Point): Direction4? =
            with(v) {
                when (x.sign to y.sign) {
                    0 to -1 -> NORTH
                    1 to 0 -> EAST
                    0 to 1 -> SOUTH
                    -1 to 0 -> WEST
                    else -> null
                }
            }

        val allVectors: List<Point> = values().map { it.vector }
    }
}

enum class Direction8 : Direction {
    NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;

    override val right: Direction8
        get() = values()[(this.ordinal + 1) % values().size]
    override val left: Direction8
        get() = values()[(this.ordinal - 1 + values().size) % values().size]
    override val opposite: Direction8
        get() = values()[(this.ordinal + 4) % values().size]
    override val vector: Point
        get() = when (this) {
            NORTH -> 0 to -1
            NORTHEAST -> 1 to -1
            EAST -> 1 to 0
            SOUTHEAST -> 1 to 1
            SOUTH -> 0 to 1
            SOUTHWEST -> -1 to 1
            WEST -> -1 to 0
            NORTHWEST -> -1 to -1
        }

    companion object {
        val UP = NORTH
        val RIGHT = EAST
        val DOWN = SOUTH
        val LEFT = WEST
        fun ofVector(v: Point): Direction8? =
            with(v) {
                when (x.sign to y.sign) {
                    NORTH.vector -> NORTH
                    NORTHEAST.vector -> NORTHEAST
                    EAST.vector -> EAST
                    SOUTHEAST.vector -> SOUTHEAST
                    SOUTH.vector -> SOUTH
                    SOUTHWEST.vector -> SOUTHWEST
                    WEST.vector -> WEST
                    NORTHWEST.vector -> NORTHWEST
                    else -> null
                }
            }

        val allVectors: List<Point> = values().map { it.vector }
    }
}
