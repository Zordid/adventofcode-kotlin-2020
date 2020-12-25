class Day25 : Day(25, title = "Combo Breaker") {

    private val pkCard = input[0].toLong()
    private val pkDoor = input[1].toLong()

    override fun part1(): Long {
        val lsCard = detectSecretLoopSize(pkCard)
        return transform(pkDoor, lsCard)
    }

    private fun detectSecretLoopSize(publicKey: Long) =
        cryptoSequence(7L).takeWhile { it != publicKey }.count()

    private fun transform(subjectNumber: Long, loopSize: Int) =
        cryptoSequence(subjectNumber).drop(loopSize).first()

    override fun part2() = "Merry X-Mas"

    companion object {
        private const val DIVISOR = 20201227L
        private fun cryptoSequence(subjectNumber: Long) = generateSequence(1L) { n ->
            n * subjectNumber % DIVISOR
        }
    }
}

fun main() {
    Day25().solve()
}