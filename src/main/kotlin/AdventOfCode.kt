import org.reflections.Reflections
import java.io.File
import java.net.URL
import kotlin.system.measureTimeMillis
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@ExperimentalTime
fun main() {
    verbose = false
    println("\n~~~ Advent Of Code Runner ~~~\n")
    val dayClasses = getAllDayClasses().sortedBy(::dayNumber)
    val totalDuration = dayClasses.map { it.execute() }.reduce(Duration::plus)
    println("\nTotal runtime: $totalDuration")
}

private fun getAllDayClasses(): Set<Class<out Day>> =
    Reflections("").getSubTypesOf(Day::class.java)

@ExperimentalTime
private fun Class<out Day>.execute(): Duration {
    val day = constructors[0].newInstance() as Day
    print("${day.day}: ${day.title}".paddedTo(30, 30))
    val part1 = measureTimedValue { day.part1 }
    println("Part 1 [${part1.duration.toString().padStart(6)}]: ${part1.value}")
    print(" ".repeat(30))
    val part2 = measureTimedValue { day.part2 }
    println("Part 2 [${part2.duration.toString().padStart(6)}]: ${part2.value}")
    return part1.duration + part2.duration
}

private fun dayNumber(day: Class<out Day>) = day.simpleName.replace("Day", "").toInt()

/**
 * Dirty, but effective way to inject test data globally for one-time use only!
 * Will be reset after usage.
 */
var globalTestData: String? = null
    get() = field?.also {
        println("USING TEST DATA")
        field = null
    }

/**
 * Global flag to indicate verbosity or silence
 */
var verbose = true

@Suppress("unused")
abstract class Day(val day: Int, private val year: Int = 2020, val title: String = "unknown") {

    private val header: Unit by lazy { if (verbose) println("--- AoC $year, Day $day: $title ---\n") }

    private val rawInput: List<String> by lazy { globalTestData?.split("\n") ?: AoC.getPuzzleInput(day, year) }

    // all the different ways to get your input
    val input: List<String> by lazy { rawInput.show("Raw") }
    val inputAsGrid: List<List<Char>> by lazy { rawInput.map { it.toList() }.show("Grid") }
    val inputAsInts: List<Int> by lazy { rawInput.map { it.extractInt() }.show("Int") }
    val inputAsLongs: List<Long> by lazy { rawInput.map { it.extractLong() }.show("Long") }
    val inputAsString: String by lazy { rawInput.joinToString("").also { listOf(it).show("One string") } }

    fun <T> mappedInput(lbd: (String) -> T): List<T> =
        rawInput.map(catchingMapper(lbd)).show("Mapped")

    fun <T> parsedInput(lbd: ParserContext.(String) -> T): List<T> =
        rawInput.map(parsingMapper(lbd)).show("Parsed")

    fun <T> matchedInput(regex: Regex, lbd: (List<String>) -> T): List<T> =
        rawInput.map(matchingMapper(regex, lbd)).show("Matched")

    val part1: Any? by lazy { runCatching { part1() }.getOrElse { it } }
    val part2: Any? by lazy { runCatching { part2() }.getOrElse { it } }

    open fun part1(): Any? = "not yet implemented"
    open fun part2(): Any? = "not yet implemented"

    fun solve() {
        header
        runWithTiming(1) { part1 }
        runWithTiming(2) { part2 }
    }

    fun <T> T.show(prompt: String = ""): T {
        if (!verbose) return this
        header
        if (this is List<*>)
            this.show(prompt)
        else
            println("$prompt: $this")
        return this
    }

    private fun <T : Any?> List<T>.show(type: String): List<T> {
        if (!verbose) return this
        header
        println("=== $type input data ${"=".repeat(50 - type.length - 4 - 12)}")
        val idxWidth = lastIndex.toString().length
        preview { idx, data ->
            val original = rawInput.getOrNull(idx)
            val s = when {
                rawInput.size != this.size -> "$data"
                original != "$data" -> "${
                    original.paddedTo(
                        20,
                        20
                    )
                } => $data"
                else -> original
            }
            println("${idx.toString().padStart(idxWidth)}: ${s.paddedTo(0, 140)}")
        }
        println("=".repeat(50))
        return this
    }

    companion object {
        private fun <T> matchingMapper(regex: Regex, lbd: (List<String>) -> T): (String) -> T = { s ->
            regex.matchEntire(s)?.groupValues?.let {
                runCatching { lbd(it) }.getOrElse { error("Exception when matching \"$s\" - $it") }
            } ?: error("Input line does not match regex: \"$s\"")
        }

        private fun <T> catchingMapper(lbd: (String) -> T): (String) -> T = { s ->
            runCatching { lbd(s) }.getOrElse { error("Exception when mapping \"$s\" - $it") }
        }

        private fun <T> parsingMapper(lbd: ParserContext.(String) -> T): (String) -> T = { s ->
            runCatching { ParserContext(s).lbd(s) }.getOrElse { error("Exception when parsing \"$s\" - $it") }
        }

        private fun <T> List<T>.preview(maxLines: Int = 7, f: (idx: Int, data: T) -> Unit) {
            if (size <= maxLines) {
                forEachIndexed(f)
            } else {
                val cut = (maxLines - 1) / 2
                (0 until maxLines - cut - 1).forEach { f(it, this[it]!!) }
                if (size > maxLines) println("...")
                (lastIndex - cut + 1..lastIndex).forEach { f(it, this[it]!!) }
            }
        }

        private inline fun runWithTiming(part: Int, f: () -> Any?) {
            var result: Any?
            val millis = measureTimeMillis { result = f() }
            val duration = if (millis < 1000) "$millis ms" else "${"%.3f".format(millis / 1000.0)} s"
            println("\nSolution $part: (took $duration)\n$result")
        }
    }

}

@Suppress("unused")
class ParserContext(private val line: String) {
    val cols: List<String> by lazy { line.split("\\s+".toRegex()) }
    val ints: List<Int> by lazy { line.extractAllIntegers() }
    val longs: List<Long> by lazy { line.extractAllLongs() }
    fun columnsBy(separator: Regex) = line.split(separator)
}

private fun String.extractInt() = toIntOrNull() ?: sequenceContainedIntegers().first()
private fun String.extractLong() = toLongOrNull() ?: sequenceContainedLongs().first()

private val numberPattern = "(-+)?\\d+".toRegex().toPattern()

fun String.sequenceContainedIntegers(): Sequence<Int> {
    val numberMatcher = numberPattern.matcher(this)
    return sequence {
        while (numberMatcher.find()) {
            numberMatcher.group(0).let { group ->
                group.toIntOrNull()?.apply { yield(this) } ?: warn { "Number too large for Int: $group" }
            }
        }
    }
}

fun String.sequenceContainedLongs(): Sequence<Long> {
    val numberMatcher = numberPattern.matcher(this)
    return sequence {
        while (numberMatcher.find()) {
            numberMatcher.group(0).let { group ->
                group.toLongOrNull()?.apply { yield(this) } ?: warn { "Number too large for Long: $group" }
            }
        }
    }
}

fun String.extractAllIntegers(): List<Int> = sequenceContainedIntegers().toList()
fun String.extractAllLongs(): List<Long> = sequenceContainedLongs().toList()

private fun warn(msg: () -> String) {
    println("WARNING: ${msg()}")
}

private fun Any?.paddedTo(minWidth: Int, maxWidth: Int) = with(this.toString()) {
    when {
        length > maxWidth -> this.substring(0, maxWidth - 3) + "..."
        length < minWidth -> this.padEnd(minWidth)
        else -> this
    }
}


object AoC {
    fun getPuzzleInput(day: Int, year: Int): List<String> {
        val cached = readInputFile(day, year)
        if (cached != null) return cached

        return runCatching {
            downloadInput(day, year).also {
                writeInputFile(day, year, it)
            }
        }.getOrElse { listOf("Unable to download $day/$year: $it") }
    }

    private fun downloadInput(day: Int, year: Int): List<String> {
        println("Downloading puzzle for $year, day $day...")
        val uri = "https://adventofcode.com/$year/day/$day/input"
        val cookies = mapOf("session" to getSessionCookie())

        val url = URL(uri)
        val connection = url.openConnection()
        connection.setRequestProperty(
            "Cookie", cookies.entries.joinToString(separator = "; ") { (k, v) -> "$k=$v" }
        )
        connection.connect()
        val result = arrayListOf<String>()
        connection.getInputStream().bufferedReader().useLines { result.addAll(it) }
        return result
    }

    private fun getSessionCookie() =
        System.getenv("AOC_COOKIE")
            ?: object {}.javaClass.getResource("session-cookie")?.readText()
            ?: error("Cookie missing")

    private fun readInputFile(day: Int, year: Int): List<String>? {
        val file = File(fileNameFor(day, year))
        file.exists() || return null
        return file.bufferedReader().readLines()
    }

    private fun writeInputFile(day: Int, year: Int, puzzle: List<String>) {
        File(pathNameForYear(year)).mkdirs()
        File(fileNameFor(day, year)).writeText(puzzle.joinToString("\n"))
    }

    private fun pathNameForYear(year: Int) = "puzzles/$year"
    private fun fileNameFor(day: Int, year: Int) = "${pathNameForYear(year)}/day${"%02d".format(day)}.txt"

}