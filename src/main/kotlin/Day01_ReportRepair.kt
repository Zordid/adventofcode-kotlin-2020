import utils.combinations
import utils.product

class Day01 : Day(1, title = "Report Repair") {

    val report = inputAsInts

    override fun part1() = report.combinations(2).first { it.sum() == 2020 }.product()

    override fun part2() = report.combinations(3).first { it.sum() == 2020 }.product()

}

fun main() {
    Day01().solve()
}