class Day17 : Day(17, title = "Conway Cubes") {

    private val cubes3D = input.flatMapIndexed { y: Int, s: String ->
        s.mapIndexedNotNull { x, c -> if (c == '#') Point3D(x, y, 0) else null }
    }.show("Cubes").toSet()
    private val cubes4D = cubes3D.map { Point4D(it.x, it.y, it.z, 0) }.toSet()

    override fun part1() =
        conwaySequence(cubes3D, ::rules).take(7).last().size

    override fun part2() =
        conwaySequence(cubes4D, ::rules).take(7).last().size

    private fun rules(isAlive: Boolean, aliveNeighbors: Int) =
        (isAlive && aliveNeighbors in 2..3) || (!isAlive && aliveNeighbors == 3)

    private fun <T : HasNeighbors<T>> conwaySequence(
        start: Set<T>,
        alive: (Boolean, Int) -> Boolean
    ): Sequence<Set<T>> =
        generateSequence(start) { gen ->
            val nextGen = mutableSetOf<T>()

            val relevant = gen.toMutableSet()
            gen.forEach { relevant += it.neighbors() }
            relevant.forEach { p ->
                if (alive(p in gen, p.neighbors().count { it in gen })) nextGen += p
            }

            nextGen
        }

}

interface HasNeighbors<T> {
    fun neighbors(): Sequence<T>
}

data class Point3D(val x: Int, val y: Int, val z: Int) : HasNeighbors<Point3D> {
    override fun neighbors() = sequence {
        for (dz in -1..+1) {
            for (dy in -1..+1) {
                for (dx in -1..+1) {
                    if (!(dx == 0 && dy == 0 && dz == 0))
                        yield(Point3D(x + dx, y + dy, z + dz))
                }
            }
        }
    }
}

operator fun Point3D.plus(other: Point3D) = Point3D(x + other.x, y + other.y, z + other.z)
operator fun Point3D.minus(other: Point3D) = Point3D(x - other.x, y - other.y, z - other.z)
operator fun Point3D.times(factor: Int) = Point3D(x * factor, y * factor, z * factor)
operator fun Point3D.unaryMinus() = Point3D(-x, -y, -z)

data class Point4D(val x: Int, val y: Int, val z: Int, val w: Int) : HasNeighbors<Point4D> {
    override fun neighbors() = sequence {
        for (dw in -1..+1) {
            for (dz in -1..+1) {
                for (dy in -1..+1) {
                    for (dx in -1..+1) {
                        if (!(dx == 0 && dy == 0 && dz == 0 && dw == 0))
                            yield(Point4D(x + dx, y + dy, z + dz, w + dw))
                    }
                }
            }
        }
    }
}

operator fun Point4D.plus(other: Point4D) = Point4D(x + other.x, y + other.y, z + other.z, w + other.w)
operator fun Point4D.minus(other: Point4D) = Point4D(x - other.x, y - other.y, z - other.z, w - other.w)
operator fun Point4D.times(factor: Int) = Point4D(x * factor, y * factor, z * factor, w * factor)
operator fun Point4D.unaryMinus() = Point4D(-x, -y, -z, -w)

fun main() {
    Day17().solve()
    globalTestData = """
        .#.
        ..#
        ###
    """.trimIndent()
}