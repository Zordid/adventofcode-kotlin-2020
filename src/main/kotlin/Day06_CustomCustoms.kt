class Day06 : Day(6, title = "Custom Customs") {

    val customsAnswers = input.joinToString(" ")
        .split("  ")
        .map { it.split(" ").map(String::toSet) }
        .show("Customs answers groups")

    override fun part1() = customsAnswers.map { it.reduce(Set<Char>::union) }.sumOf { it.size }

    override fun part2() = customsAnswers.map { it.reduce(Set<Char>::intersect) }.sumOf { it.size }

}

fun main() {
    Day06().solve()
}