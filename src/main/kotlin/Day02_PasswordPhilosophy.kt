class Day02 : Day(2, title = "Password Philosophy") {

    val pw = parsedInput { PasswordAndPolicy(ints[0], -ints[1], cols[1][0], cols[2]) }

    override fun part1() = pw.count { it.validPart1() }

    override fun part2() = pw.count { it.validPart2() }
}

data class PasswordAndPolicy(val a: Int, val b: Int, val c: Char, val pw: String) {
    fun validPart1() = pw.count { it == c } in (a..b)
    fun validPart2() = (pw[a - 1] == c) xor (pw[b - 1] == c)
}

fun main() {
    Day02().solve()
}