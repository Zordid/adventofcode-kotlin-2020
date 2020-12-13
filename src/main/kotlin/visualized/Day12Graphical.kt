package visualized

import Day12
import utils.*
import utils.Direction4.EAST
import java.awt.Color
import java.util.*

class Day12Graphical : PixelGameEngine() {

    private val day12 = Day12()

    private val path1 = day12.navInstructions.runningFold(origin to EAST, day12.operationPart1)
    private val path2 = day12.navInstructions.runningFold(origin to (10 to -1), day12.operationPart2)
    private val shipMovement = path1.map { Triple(it.first, it.second, 0 to 0) }

    //    private val shipMovement = path2.map { Triple(it.first, Direction4.NORTH, it.second) }
    private val area = shipMovement.map { it.first }.boundingArea()!!.grow(10)

    val transpose: Point

    init {
        construct(area.width, area.height, 1, 1, day12.title)
        transpose = -area.first.x to -area.first.y
        limitFps = 100
        println(area)
        println(transpose)
    }

    var current = 1

    override fun isActive() = current in shipMovement.indices

    var shipOnScreen: Triple<Point, Direction, Point>? = null
    var currentTarget = shipMovement[0]
    var lastPoints: LinkedList<Point> = LinkedList()

    override fun onUpdate(elapsedTime: Long, frame: Long) {
        shipOnScreen?.let { drawShip(it, Color.BLACK) }

        if (shipOnScreen != null && shipOnScreen != currentTarget) {
            if (shipOnScreen!!.first != currentTarget.first) {
                val delta = currentTarget.first - shipOnScreen!!.first
                val direction = Direction4.ofVector(delta)!!
                shipOnScreen = shipOnScreen!!.copy(first = shipOnScreen!!.first + direction.vector)
                lastPoints.addFirst(shipOnScreen!!.first)
                if (lastPoints.size > 200) lastPoints.removeLast()
            } else {
                shipOnScreen = currentTarget
            }
        } else {
            shipOnScreen = currentTarget
            while (currentTarget.first == shipOnScreen!!.first)
                currentTarget = shipMovement[current++]
        }

        val color = Color.BLUE
        lastPoints.drop(1).forEachIndexed { idx, p ->
            val factor = 1.0 - idx / 200.0
            draw(
                p.x + transpose.x,
                p.y + transpose.y,
                Color((color.red * factor).toInt(), (color.green * factor).toInt(), (color.blue * factor).toInt())
            )
        }

        drawAxis()

        drawShip(shipOnScreen!!, Color.RED)

        appInfo = "$current / ${shipMovement.size}"
    }

    private fun drawAxis() {
        val color = Color.WHITE
        val pattern = "000100010001000100010001000100010001000100010001000100010001000".toULong(2).toLong()
        drawLine(
            area.first.x + transpose.x,
            0 + transpose.y,
            area.second.x + transpose.x,
            0 + transpose.y,
            color, pattern
        )
        drawLine(
            0 + transpose.x,
            area.first.y + transpose.y,
            0 + transpose.x,
            area.second.y + transpose.y,
            color, pattern
        )
    }

    private fun drawShip(p: Triple<Point, Direction, Point>, color: Color) {
        val pos = p.first
        val heading = p.second * 10
        drawCircle(pos.x + transpose.x, pos.y + transpose.y, 10, color)
        drawLine(
            pos.x + transpose.x,
            pos.y + transpose.y,
            pos.x + heading.x + transpose.x,
            pos.y + heading.y + transpose.y,
            color
        )
    }

}

fun main() {
    Day12Graphical().start()
}