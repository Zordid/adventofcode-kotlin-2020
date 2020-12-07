class Day07 : Day(7, title = "Handy Haversacks") {

    val rules = parsedInput { line ->
        val c = line.split(Regex(" bags?[,.\\s]?")).filter { it.isNotEmpty() }.map { it.replace("contain ", "").trim() }
        if (c[1].startsWith("no"))
            c[0] to emptyMap()
        else
            c[0] to c.drop(1).map { it.split(" ", limit = 2).let { it[1] to it[0].toInt() } }.toMap()
    }.toMap()

    val shinyGold = "shiny gold"

    override fun part1() = (rules.keys - listOf(shinyGold)).count { canContain(it, shinyGold) }

    private fun canContain(c: String, searchFor: String): Boolean =
        (searchFor == c) || rules[c]!!.any { (color, _) -> canContain(color, searchFor) }

    override fun part2() = countContains(shinyGold) - 1

    private fun countContains(c: String): Int =
        1 + rules[c]!!.entries.sumBy { (color, count) -> count * countContains(color) }

}

fun main() {
    Day07().solve()
}