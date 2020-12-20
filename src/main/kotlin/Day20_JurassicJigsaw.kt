import utils.*

class Day20 : Day(20, title = "Jurassic Jigsaw") {

    private val pieces = chunkedInput().map {
        it.first().sequenceContainedIntegers().first() to it.drop(1).takeWhile { it.isNotEmpty() }.map { it.toList() }
    }.toMap().show("Tiles")

    val operations = (0..3).flatMap { rotation -> listOf(false, true).map { flip -> rotation to flip } }

    val piecesWithAllOrientations = pieces.mapValues { (_, piece) ->
        operations.map { (r, f) -> piece.modify(r, f) }
    }

    private fun puzzle(
        idsOnTable: Map<Point, Int> = emptyMap(),
        piecesOnTable: Map<Point, PuzzlePiece> = emptyMap(),
        remainingPieces: Collection<Int> = pieces.keys
    ): Pair<Map<Point, Int>, Map<Point, PuzzlePiece>>? {

        if (remainingPieces.isEmpty())
            return idsOnTable to piecesOnTable
        if (piecesOnTable.isEmpty()) {
            val firstPiece = remainingPieces.first()
            return puzzle(
                mapOf(origin to firstPiece),
                mapOf(origin to pieces[firstPiece]!!),
                remainingPieces - firstPiece
            )
        }

        val freePositions = piecesOnTable.keys.freePositions()

        val allPiecesAllOrientations = remainingPieces.asSequence()
            .flatMap { id -> piecesWithAllOrientations[id]!!.map { id to it } }

        val allPiecesAllPlaces = allPiecesAllOrientations.flatMap {
            freePositions.map { freePosition -> Triple(freePosition, it.first, it.second) }
        }

        val possibleMoves = allPiecesAllPlaces.filter { (freePosition, _, piece) ->
            freePosition.directNeighbors().all { neighborPos ->
                val onTable = piecesOnTable[neighborPos]
                piece.match(onTable, Direction4.ofVector(freePosition - neighborPos)!!)
            }
        }

        val solutions = possibleMoves.mapNotNull { (newPos, pieceId, piece) ->
            puzzle(
                idsOnTable + (newPos to pieceId),
                piecesOnTable + (newPos to piece),
                remainingPieces - pieceId
            )
        }

        return solutions.firstOrNull()
    }

    private fun Set<Point>.freePositions() = this.flatMap { it.directNeighbors() }.toSet() - this

    private val solution: Pair<Map<Point, Int>, Map<Point, PuzzlePiece>> by lazy { puzzle()!! }

    override fun part1(): Long {

//        var p = pieces[3779]!!
//        p = listOf("123".toList(), "456".toList(), "789".toList())
//        repeat(4) {
//            println("\n$it rotations:")
//            p.print()
//            p = p.rotate()
//        }
//        return 0L

//        val edges = piecesWithAllOrientations.values.flatMap { it.map { it.first().joinToString("")}}
//        edges.forEach { println(it) }
//        println(pieces.size)
//        println(edges.size)
//        println(edges.distinct().size)
//        return 0

//        pieces.forEach { (id, piece) ->
//            println("-".repeat(100))
//            println(id)
//            println()
//
//            val flipped = piece.flipHorizontal().rotate().rotate()
//            piece.zip(flipped).forEach { (p, f) ->
//                println(p.joinToString("\t") + "\t\t" + f.joinToString("\t"))
//            }
//            println("-".repeat(100))
//        }
//        return 0

        val result = solution.first
        result.printIds()
        val area = result.keys.boundingArea()!!
        return area.corners().map { result[it]!! }.productAsLong()
    }

    override fun part2(): Int {
        val result = solution.second

        val area = result.keys.boundingArea()!!

        val image = area.allPoints().map {
            result[it]!!.shrink()
        }.chunked(area.width).map { row: List<PuzzlePiece> ->
            row.first().indices
                .map { y -> row.map { it[y] }.reduce { acc, list -> acc + list } }
        }.flatMap { it }.toList()

        image.print()

        val monsters = operations.map { (rotations, flip) ->
            image.modify(rotations, flip).detectImage(monsterPoints).count()
        }.single { it > 0 }

        return image.sumBy { it.count { it == '#' } } - monsters * monsterPoints.size

    }

    private fun PuzzlePiece.detectImage(subImagePoints: Collection<Point>) =
        area().allPoints().filter { p ->
            subImagePoints.all { relative -> this[p + relative] == '#' }
        }

    companion object {
        private val monster: PuzzlePiece = """
                  # 
#    ##    ##    ###
 #  #  #  #  #  #           
    """.trimIndent().split("\n").map { it.toList() }

        private val monsterPoints = monster.matchingIndices { it == '#' }

        private fun Map<Point, Int>.printIds() {
            val area = this.keys.boundingArea()!!
            area.forEach { p ->
                if (this[p] != null) {
                    print("${this[p]} ")
                } else print("     ")
                if (p.x == area.second.x)
                    println()
            }
        }

    }
}

fun main() {
    Day20().solve()
}

typealias PuzzlePiece = List<List<Char>>

fun PuzzlePiece.modify(rotations: Int, flip: Boolean): PuzzlePiece {
    val m = (0 until rotations).fold(this) { acc, _ -> acc.rotate() }
    return if (flip) m.flipHorizontal() else m
}

private fun PuzzlePiece.rotate() = this[0].indices.reversed().map { x -> this.map { it[x] } }

private fun PuzzlePiece.flipHorizontal() = this.asReversed()

private fun PuzzlePiece.match(other: PuzzlePiece?, heading: Direction4) =
    other == null || when (heading) {
        Direction4.NORTH -> this.last() == other.first()
        Direction4.SOUTH -> this.first() == other.last()
        Direction4.EAST -> this.map { it.first() } == other.map { it.last() }
        Direction4.WEST -> this.map { it.last() } == other.map { it.first() }
    }

private fun PuzzlePiece.shrink() =
    drop(1).dropLast(1).map { it.drop(1).dropLast(1) }

private fun PuzzlePiece.print() {
    this.forEach { println(it.joinToString("")) }
    println("[${this.first().size} x ${this.size}]")
}
