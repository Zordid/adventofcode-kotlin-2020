package visualized

import Day11
import utils.*
import java.awt.Color

class Day11Graphical(part: Int) : PixelGameEngine() {

    private val day11 = Day11()
    private val area = day11.seatMap.area()
    private val sequence =
        if (part == 1) day11.sequencePart1().iterator() else day11.sequencePart2().iterator()

    init {
        construct(area.width, area.height, 5, 5, "${day11.title} - Part $part")
        limitFps = 10
    }

    override fun isActive() = sequence.hasNext()

    override fun onUpdate(elapsedTime: Long, frame: Long) {
        val map = sequence.next()
        appInfo = "generation #$frame"
        map.forArea { p, v ->
            when (v) {
                Day11.EMPTY -> draw(p.x, p.y, Color.GREEN)
                Day11.OCCUPIED -> draw(p.x, p.y, Color.RED)
            }
        }
        if (frame == 0L)
            sleep(3000)
    }

}

fun main() {
    Day11Graphical(1).start()
}