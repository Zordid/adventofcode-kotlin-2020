package visualized

import Day11
import utils.*
import java.awt.Color

class Day11Graphical(part: Int) : PixelGameEngine() {

    private val day11 = Day11()
    private val area = day11.seatMap.area()
    private val generations =
        if (part == 1) day11.sequencePart1().iterator() else day11.sequencePart2().iterator()

    init {
        construct(area.width, area.height, 5, appName = "${day11.title}-$part")
        limitFps = 25
    }

    override fun isActive() = generations.hasNext()

    override fun onUpdate(elapsedTime: Long, frame: Long) {
        val map = generations.next()
        appInfo = "gen #$frame"
        map.forArea { p, v ->
            when (v) {
                Day11.EMPTY -> draw(p.x, p.y, Color.GREEN)
                Day11.OCCUPIED -> draw(p.x, p.y, Color.RED)
            }
        }
        if (frame == 0L)
            hold(3000)
    }

}

fun main() {
    Day11Graphical(1).start()
}