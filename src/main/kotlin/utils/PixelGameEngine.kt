package utils

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.system.measureTimeMillis

/**
 * # PixelGameEngine
 *
 * An absolutely simple and rudimentary engine to quickly draw rough, pixelized things on screen. Good for demos,
 * little games, mazes, etc.
 *
 * The displays a Swing window of defined size and runs in an endless game loop, allowing your code to act
 * directly before start and within each iteration of the game loop.
 *
 * Usage:
 * Derive from this abstract class and overwrite the methods [onCreate] and [onUpdate].
 *
 */
abstract class PixelGameEngine {

    private inner class GamePanel(val pixelWidth: Int, val pixelHeight: Int) : JPanel() {

        override fun paint(g: Graphics) {
            super.paint(g)
            var p = 0
            val stableCopy = displayBuffer.copyOf()
            for (y in 0 until screenHeight) {
                for (x in 0 until screenWidth) {
                    g.color = stableCopy[p]
                    g.fillRect(x * pixelWidth, y * pixelHeight, pixelWidth, pixelHeight)
                    p++
                }
            }
        }

    }

    private var appName = ""
    var limitFps: Int? = null
        set(value) {
            field = value
            millisPerFrame = value?.let { (1000.0 / it).toLong() }
        }
    private var millisPerFrame: Long? = null
    var appInfo = ""
    lateinit var frame: JFrame
    var screenWidth = 0
        private set
    var screenHeight = 0
        private set

    private lateinit var displayBuffer: Array<Color>
    private lateinit var buffer: Array<Color>
    private lateinit var panel: GamePanel

    fun construct(
        screenWidth: Int,
        screenHeight: Int,
        pixelWidth: Int,
        pixelHeight: Int,
        appName: String = "PixelGameEngine",
    ) {
        require(screenWidth > 0 && screenHeight > 0) { "Unsupported dimensions: $screenWidth x $screenHeight" }
        require(pixelWidth > 0 && pixelHeight > 0) { "Unsupported pixel dimensions: $pixelWidth x $pixelHeight" }

        this.screenWidth = screenWidth
        this.screenHeight = screenHeight
        buffer = Array(screenWidth * screenHeight) { Color.BLACK }
        displayBuffer = buffer

        this.appName = appName
        panel = GamePanel(pixelWidth, pixelHeight)
        frame = JFrame()
        with(frame) {
            updateTitle("initialized")
            defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            isResizable = false
            pack()
            size = with(insets) {
                Dimension(
                    screenWidth * pixelWidth + left + right,
                    screenHeight * pixelHeight + top + bottom
                )
            }
            panel.alignmentX = JComponent.CENTER_ALIGNMENT
            panel.alignmentY = JComponent.CENTER_ALIGNMENT
            add(panel)
            setLocationRelativeTo(null)
            isVisible = true
        }
    }

    private var halted = false
    var hold = 0L

    open fun isActive() = true

    fun start() {
        onCreate()
        panel.repaint()
        val startTime = System.currentTimeMillis()
        var frame = 0L
        while (!halted && isActive()) {
            var time = measureTimeMillis {
                onUpdate(System.currentTimeMillis() - startTime, frame++)
                displayBuffer = buffer
                buffer = buffer.copyOf()
                panel.repaint()
                if (hold > 0L) {
                    sleep(hold)
                    hold = 0L
                }
            }
            millisPerFrame?.let {
                val sleepTime = (it - time).coerceAtLeast(0)
                sleep(sleepTime)
                time += sleepTime
            }
            updateTitle(1000.0 / time)
        }
        onStop(System.currentTimeMillis() - startTime, frame - 1)
        updateTitle("stopped")
        while (true) sleep(1000)
    }

    /**
     * Draws a pixel on the screen in the defined color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param color the color to draw
     */
    @JvmOverloads
    fun draw(x: Int, y: Int, color: Color = Color.WHITE) {
        if (x in 0 until screenWidth && y in 0 until screenHeight) {
            val pos = y * screenWidth + x
            buffer[pos] = color
        }
    }

    /**
     * Draws a line on the screen in the defined color using the given pattern.
     *
     * @param x1 start x coordinate
     * @param y1 start y coordinate
     * @param x2 end x coordinate
     * @param y2 end y coordinate
     * @param color the color to use
     * @param pattern the pattern to use
     */
    @JvmOverloads
    fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int, color: Color = Color.WHITE, pattern: Long = 0xFFFFFFFF) {
        var p = pattern and 0xFFFFFFFF

        fun rol(): Boolean {
            p = (p shl 1) or (p ushr 31)
            return p and 1L == 1L
        }

        val dx = x2 - x1
        val dy = y2 - y1

        if (dx == 0) {
            if (y2 < y1)
                for (y in y2..y1) {
                    if (rol()) draw(x1, y, color)
                } else {
                for (y in y1..y2)
                    if (rol()) draw(x1, y, color)
            }
            return
        }

        if (dy == 0) {
            if (x2 < x1)
                for (x in x2..x1) {
                    if (rol()) draw(x, y1, color)
                } else {
                for (x in x1..x2)
                    if (rol()) draw(x, y1, color)
            }
            return
        }

        val dx1 = dx.absoluteValue
        val dy1 = dy.absoluteValue
        var px = 2 * dy1 - dx1
        var py = 2 * dx1 - dy1

        if (dy1 <= dx1) {
            var x: Int
            var y: Int
            val xe: Int
            if (dx >= 0) {
                x = x1
                y = y1
                xe = x2
            } else {
                x = x2
                y = y2
                xe = x1
            }

            if (rol()) draw(x, y, color)
            while (x < xe) {
                x += 1
                if (px < 0)
                    px += 2 * dy1
                else {
                    if ((dx < 0 && dy < 0) || (dx > 0 && dy > 0)) y += 1 else y -= 1
                    px += 2 * (dy1 - dx1)
                }
                if (rol()) draw(x, y, color)
            }
        } else {
            var x: Int
            var y: Int
            val ye: Int
            if (dy >= 0) {
                x = x1
                y = y1
                ye = y2
            } else {
                x = x2
                y = y2
                ye = y1
            }

            if (rol()) draw(x, y, color)
            while (y < ye) {
                y += 1
                if (py < 0)
                    py += 2 * dx1
                else {
                    if ((dx < 0 && dy < 0) || (dx > 0 && dy > 0)) x += 1 else x -= 1
                    py += 2 * (dx1 - dy1)
                }
                if (rol()) draw(x, y, color)
            }
        }
    }

    @JvmOverloads
    fun drawRect(x: Int, y: Int, width: Int, height: Int, color: Color = Color.WHITE, pattern: Long = 0xFFFFFFFF) {
        drawLine(x, y, x + width - 1, y, color, pattern)
        drawLine(x + width - 1, y, x + width - 1, y + height - 1, color, pattern)
        drawLine(x + width - 1, y + height - 1, x, y + height - 1, color, pattern)
        drawLine(x, y + height - 1, x, y, color, pattern)
    }

    @JvmOverloads
    fun fillRect(x: Int, y: Int, width: Int, height: Int, color: Color = Color.WHITE) {
        var x1 = x
        var y1 = y
        var x2 = x + width - 1
        var y2 = y + height - 1

        if (x1 < 0) x1 = 0
        if (x1 >= screenWidth) x1 = screenWidth
        if (y1 < 0) y1 = 0
        if (y1 >= screenHeight) y1 = screenHeight

        if (x2 < 0) x2 = 0
        if (x2 >= screenWidth) x2 = screenWidth
        if (y2 < 0) y2 = 0
        if (y2 >= screenHeight) y2 = screenHeight

        for (i in x1..x2)
            for (j in y1..y2)
                draw(i, j, color)
    }

    @JvmOverloads
    fun drawCircle(x: Int, y: Int, radius: Int, color: Color = Color.WHITE, mask: Int = 0xFF) {
        var x0 = 0
        var y0 = radius
        var d = 3 - 2 * radius
        if (radius == 0) return

        while (y0 >= x0) {
            if (mask and 0x01 != 0) draw(x + x0, y - y0, color)
            if (mask and 0x02 != 0) draw(x + y0, y - x0, color)
            if (mask and 0x04 != 0) draw(x + y0, y + x0, color)
            if (mask and 0x08 != 0) draw(x + x0, y + y0, color)
            if (mask and 0x10 != 0) draw(x - x0, y + y0, color)
            if (mask and 0x20 != 0) draw(x - y0, y + x0, color)
            if (mask and 0x40 != 0) draw(x - y0, y - x0, color)
            if (mask and 0x80 != 0) draw(x - x0, y - y0, color)
            d += if (d < 0)
                4 * x0++ + 6
            else
                4 * (x0++ - y0--) + 10
        }
    }

    @JvmOverloads
    fun fillCircle(x: Int, y: Int, radius: Int, color: Color = Color.WHITE) {
        var x0 = 0
        var y0 = radius
        var d = 3 - 2 * radius
        if (radius == 0) return

        fun drawLine(sx: Int, ex: Int, ny: Int) {
            for (i in sx..ex) draw(i, ny, color)
        }

        while (y0 >= x0) {
            // Modified to draw scan-lines instead of edges
            drawLine(x - x0, x + x0, y - y0)
            drawLine(x - y0, x + y0, y - x0)
            drawLine(x - x0, x + x0, y + y0)
            drawLine(x - y0, x + y0, y + x0)
            d += if (d < 0)
                4 * x0++ + 6
            else
                4 * (x0++ - y0--) + 10
        }
    }

    @JvmOverloads
    fun clear(color: Color = Color.BLACK) = buffer.fill(color)

    /**
     * Sleeps for [millis] milliseconds. Can be used to slow down the animation in [onUpdate].
     */
    fun sleep(millis: Long) = Thread.sleep(millis)

    fun hold(millis: Long) {
        hold = millis
    }

    fun stop() {
        halted = true
    }

    /**
     * Will be called from the game engine right before the endless game loop. Can be used to initialize things.
     */
    open fun onCreate() {
        // nop
    }

    /**
     * Will be called once per game loop to update the screen. Use the supplied methods to interact with the screen.
     * @see draw
     * @see drawLine
     * @see drawRect
     * @see fillRect
     * @see drawCircle
     * @see fillCircle
     * @see sleep
     */
    open fun onUpdate(elapsedTime: Long, frame: Long) {
        sleep(1000)
    }

    open fun onStop(elapsedTime: Long, frame: Long) {
        // nop
    }

    private fun updateTitle(fps: Double) {
        frame.title = "$appName - $appInfo - ${"%.1f".format(fps)} fps"
    }

    private fun updateTitle(state: String) {
        frame.title = "$appName - $appInfo - $state"
    }

    companion object {
        fun gradientColor(from: Color, to: Color, percent: Float): Color {
            val resultRed: Float = from.red + percent * (to.red - from.red)
            val resultGreen: Float = from.green + percent * (to.green - from.green)
            val resultBlue: Float = from.blue + percent * (to.blue - from.blue)
            return Color(resultRed.roundToInt(), resultGreen.roundToInt(), resultBlue.roundToInt())
        }

        fun createGradient(from: Color, to: Color, steps: Int): List<Color> =
            (0 until steps).map { gradientColor(from, to, it / (steps - 1).toFloat()) }
    }

}