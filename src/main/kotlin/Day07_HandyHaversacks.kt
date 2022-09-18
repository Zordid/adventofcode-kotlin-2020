class Day07 : Day(7, title = "Handy Haversacks") {

    private val rules = parsedInput(columnSeparator = Regex(" bags?[,.]?\\s?(contain )?")) {
        val coloredBag = nonEmptyCols[0]
        val mustContain = nonEmptyCols.drop(1)
            .filter { !it.startsWith("no ") }
            .map { it.split(" ", limit = 2).let { (cnt, clr) -> clr to cnt.toInt() } }
        coloredBag to mustContain.toMap()
    }.toMap()

    override fun part1() = rules.keys.count { it.canContain("shiny gold") }

    private fun String.canContain(searchFor: String): Boolean =
        rules[this]?.any { (color, _) -> color == searchFor || color.canContain(searchFor) } == true

    override fun part2() = "shiny gold".countContains()

    private fun String.countContains(): Int =
        rules[this]?.entries?.sumOf { (color, count) -> count + count * color.countContains() } ?: 0

}

fun main() {
    Day07().solve()
}