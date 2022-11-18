@file:Suppress("unused", "MemberVisibilityCanBePrivate")

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
 * PixelGameEngine
 *
 * An absolutely simple and rudimentary engine to quickly draw rough, pixelized things on screen. Good for demos,
 * little games, mazes, etc. Inspired by olcPixelGameEngine for C++!
 *
 * The displays a Swing window of defined size and runs in an endless game loop, allowing your code to act
 * directly before start and within each iteration of the game loop.
 *
 * Usage:
 * Derive from this abstract class and overwrite the methods [onCreate] and [onUpdate].
 *
 * Version history:
 * V2.0 - 17/11/2022 improve repainting only the dirty area
 * V2.1 - 18/11/2022 add triangle functions, add frame sequence builder, fixed refresh
 *
 */
abstract class PixelGameEngine {

    private inner class GamePanel(val pixelWidth: Int, val pixelHeight: Int) : JPanel() {

        fun refresh() = repaint()

        fun refresh(xL: Int, yL: Int, xH: Int, yH: Int) {
            if (xL in 0 until screenWidth)
                repaint(xL * pixelWidth, yL * pixelHeight, (xH - xL + 1) * pixelWidth, (yH - yL + 1) * pixelHeight)
        }

        override fun paint(g: Graphics) {
            super.paint(g)
            val stableCopy = displayBuffer.copyOf()
            var y = g.clipBounds.y / pixelHeight
            val startX = g.clipBounds.x / pixelWidth
            val endY =
                ((g.clipBounds.y + g.clipBounds.height) / pixelHeight).coerceAtMost(screenHeight)
            val endX =
                ((g.clipBounds.x + g.clipBounds.width) / pixelWidth).coerceAtMost(screenWidth)
            //println(g.clipBounds)
            //println("$startX $y - $endX $endY (max: $screenWidth $screenHeight)")
            while (y < endY) {
                var x = startX
                var p = y * screenWidth + x
                while (x < endX) {
                    val color = stableCopy[p++]
                    var length = 1
                    while (x + length < endX && stableCopy[p] == color) {
                        p++
                        length++
                    }
                    if (color != Color.BLACK) {
                        g.color = color
                        g.fillRect(x * pixelWidth, y * pixelHeight, pixelWidth * length, pixelHeight)
                    }
                    x += length
                }
                y++
            }
        }

    }

    private var appName = ""
    var appInfo: Any = ""

    var limitFps: Int = Int.MAX_VALUE
        set(value) {
            field = value
            millisPerFrame = (1000.0 / value).toLong()
        }
    private var millisPerFrame: Long = 0
    private lateinit var frame: JFrame
    var screenWidth = 0
        private set
    var screenHeight = 0
        private set

    private lateinit var displayBuffer: Array<Color>
    private lateinit var buffer: Array<Color>
    private lateinit var panel: GamePanel

    private var dirtyXLow = Int.MAX_VALUE
    private var dirtyYLow = Int.MAX_VALUE
    private var dirtyXHigh = Int.MIN_VALUE
    private var dirtyYHigh = Int.MIN_VALUE

    init {
        limitFps = 50
    }

    private fun resetDirty() {
        dirtyXLow = Int.MAX_VALUE
        dirtyYLow = Int.MAX_VALUE
        dirtyXHigh = Int.MIN_VALUE
        dirtyYHigh = Int.MIN_VALUE
    }

    fun construct(
        screenWidth: Int,
        screenHeight: Int,
        pixelWidth: Int = 1,
        pixelHeight: Int = pixelWidth,
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
        panel.background = Color.BLACK
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
    private var hold = 0L

    open fun isActive() = true

    fun start() {
        onCreate()
        panel.repaint()
        val startTime = System.currentTimeMillis()
        var frame = 0L

        var holdCurrentFrame = 0L

        while (!halted && isActive()) {
            var time = measureTimeMillis {
                resetDirty()
                buffer = buffer.copyOf()
                onUpdate(System.currentTimeMillis() - startTime, frame++)
            }
            val fillTime = (millisPerFrame - time).coerceAtLeast(holdCurrentFrame)
            if (fillTime > 0) {
                holdCurrentFrame = 0
                Thread.sleep(fillTime)
                time += fillTime
            }
            displayBuffer = buffer
            panel.refresh(dirtyXLow, dirtyYLow, dirtyXHigh, dirtyYHigh)
            updateTitle(1000.0 / time)
            if (hold > 0) {
                holdCurrentFrame = hold
                hold = 0
            }
        }
        onStop(System.currentTimeMillis() - startTime, frame)
        panel.refresh()
        updateTitle("stopped")
        while (true) Thread.sleep(10_000)
    }

    /**
     * Draws a pixel on the screen in the defined color.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param color the color to draw
     */
    @JvmOverloads
    fun draw(x: Int, y: Int, color: Color = Color.WHITE) {
        val pos = y * screenWidth + x
        if (x in 0 until screenWidth && y in 0 until screenHeight && buffer[pos] != color) {
            buffer[pos] = color
            if (dirtyXLow > x) dirtyXLow = x
            if (dirtyXHigh < x) dirtyXHigh = x
            if (dirtyYLow > y) dirtyYLow = y
            if (dirtyYHigh < y) dirtyYHigh = y
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
                for (y in y2.coerceAtLeast(0)..y1.coerceAtMost(screenHeight - 1)) {
                    if (rol()) draw(x1, y, color)
                } else {
                for (y in y1.coerceAtLeast(0)..y2.coerceAtMost(screenHeight - 1))
                    if (rol()) draw(x1, y, color)
            }
            return
        }

        if (dy == 0) {
            if (x2 < x1)
                for (x in x2.coerceAtLeast(0)..x1.coerceAtMost(screenWidth - 1)) {
                    if (rol()) draw(x, y1, color)
                } else {
                for (x in x1.coerceAtLeast(0)..x2.coerceAtMost(screenWidth - 1))
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

    /**
     * Draws a rectangle on the screen in the defined color using the given pattern.
     *
     * @param x the top left corner's x coordinate
     * @param y the top left corner's y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param color the color to use
     * @param pattern the pattern to use
     */
    @JvmOverloads
    fun drawRect(x: Int, y: Int, width: Int, height: Int, color: Color = Color.WHITE, pattern: Long = 0xFFFFFFFF) {
        drawLine(x, y, x + width - 1, y, color, pattern)
        drawLine(x + width - 1, y, x + width - 1, y + height - 1, color, pattern)
        drawLine(x + width - 1, y + height - 1, x, y + height - 1, color, pattern)
        drawLine(x, y + height - 1, x, y, color, pattern)
    }

    /**
     * Fills a rectangle on the screen in the defined color using the given pattern.
     *
     * @param x the top left corner's x coordinate
     * @param y the top left corner's y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param color the color to use for fill
     */
    @JvmOverloads
    fun fillRect(x: Int, y: Int, width: Int, height: Int, color: Color = Color.WHITE) {
        val x1 = x.coerceIn(0, screenWidth - 1)
        val y1 = y.coerceIn(0, screenHeight - 1)
        val x2 = (x + width - 1).coerceIn(0, screenWidth - 1)
        val y2 = (y + height - 1).coerceIn(0, screenHeight - 1)

        for (i in x1..x2)
            for (j in y1..y2)
                draw(i, j, color)
    }

    /**
     * Draws a triangle on the screen in the defined color using the given pattern.
     *
     * @param x1 first vertex's x coordinate
     * @param y1 first vertex's y coordinate
     * @param x2 second vertex's x coordinate
     * @param y2 second vertex's y coordinate
     * @param x3 third vertex's x coordinate
     * @param y3 third vertex's y coordinate
     */
    @JvmOverloads
    fun drawTriangle(
        x1: Int,
        y1: Int,
        x2: Int,
        y2: Int,
        x3: Int,
        y3: Int,
        color: Color = Color.WHITE,
        pattern: Long = 0xFFFFFFFF
    ) {
        drawLine(x1, y1, x2, y2, color, pattern)
        drawLine(x2, y2, x3, y3, color, pattern)
        drawLine(x3, y3, x1, y1, color, pattern)
    }

    /**
     * Draws a circle on the screen in the defined color using the given pattern.
     *
     * @param x the center's x coordinate
     * @param y the center's y coordinate
     * @param radius the radius of the circle
     * @param color the color to use
     * @param pattern the pattern to use
     */
    @JvmOverloads
    fun drawCircle(x: Int, y: Int, radius: Int, color: Color = Color.WHITE, pattern: Int = 0xFF) {
        var x0 = 0
        var y0 = radius
        var d = 3 - 2 * radius
        if (radius == 0) return

        while (y0 >= x0) {
            if (pattern and 0x01 != 0) draw(x + x0, y - y0, color)
            if (pattern and 0x02 != 0) draw(x + y0, y - x0, color)
            if (pattern and 0x04 != 0) draw(x + y0, y + x0, color)
            if (pattern and 0x08 != 0) draw(x + x0, y + y0, color)
            if (pattern and 0x10 != 0) draw(x - x0, y + y0, color)
            if (pattern and 0x20 != 0) draw(x - y0, y + x0, color)
            if (pattern and 0x40 != 0) draw(x - y0, y - x0, color)
            if (pattern and 0x80 != 0) draw(x - x0, y - y0, color)
            d += if (d < 0)
                4 * x0++ + 6
            else
                4 * (x0++ - y0--) + 10
        }
    }

    /**
     * Fills a circle on the screen in the defined color.
     *
     * @param x the center's x coordinate
     * @param y the center's y coordinate
     * @param radius the radius of the circle
     * @param color the color to use
     */
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

    /**
     * Clears the buffer using the given color.
     *
     * @param color the color to clear with
     */
    @JvmOverloads
    fun clear(color: Color = Color.BLACK) {
        dirtyXLow = 0
        dirtyYLow = 0
        dirtyXHigh = screenWidth - 1
        dirtyYHigh = screenHeight - 1
        buffer.fill(color)
    }

    /**
     * Sleeps for [millis] milliseconds. Can be used to slow down the animation in [onUpdate].
     */
    fun sleep(millis: Long) = Thread.sleep(millis)

    /**
     * After updating the screen, the current frame will be shown at least the given amount of time.
     */
    fun hold(millis: Long) {
        hold = millis
    }

    /**
     * Stops the frame updates. [onUpdate] will not be called anymore.
     */
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

    /**
     * Called once right before stopping
     */
    open fun onStop(elapsedTime: Long, frame: Long) {
        // nop
    }

    private fun updateTitle(fps: Double) {
        frame.title = "$appName - $appInfo - ${"%.1f".format(fps)} fps"
    }

    private fun updateTitle(state: String) {
        frame.title = "$appName - $appInfo - $state"
    }

    fun Iterator<Unit>.use() {
        val hasNext = hasNext()
        if (!hasNext) stop() else {
            next()
        }
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

        suspend fun SequenceScope<Unit>.frame() = yield(Unit)

        fun frameSequence(block: suspend SequenceScope<Unit>.() -> Unit) = sequence(block).iterator()
    }

}