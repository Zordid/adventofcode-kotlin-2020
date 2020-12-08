class Day08 : Day(8, title = "Handheld Halting") {

    private val code = parsedInput {
        cols[0] to ints[0]
    }

    private fun List<Pair<String, Int>>.execute(): Pair<Int, Boolean> {
        var ip = 0
        var acc = 0
        val touched = mutableSetOf<Int>()
        while (ip < size && ip !in touched) {
            touched += ip
            val (mne, operand) = this[ip]
            when (mne) {
                "acc" -> acc += operand
                "jmp" -> ip += operand - 1
                "nop" -> pass
                else -> error("$mne unknown")
            }
            ip++
        }
        return acc to (ip !in indices)
    }

    private fun List<Pair<String, Int>>.patch(pos: Int): List<Pair<String, Int>> =
        slice(0 until pos) + listOf(
            this[pos].copy(
                first = when (val mne = this[pos].first) {
                    "jmp" -> "nop"
                    "nop" -> "jmp"
                    else -> mne
                }
            )
        ) + slice(pos + 1 until size)

    override fun part1() = code.execute().first

    override fun part2() = code.indices.map { code.patch(it).execute() }.single { it.second }.first

}

fun main() {
    Day08().solve()
}