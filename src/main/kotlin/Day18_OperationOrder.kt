class Day18 : Day(18, title = "Operation Order") {

    private val homework = input

    override fun part1() = homework.sumOf { it.evaluatePart1() }

    override fun part2() = homework.sumOf { it.evaluatePart2() }

    private fun String.evaluatePart1(): Long {
        trim().toLongOrNull()?.let { return it }

        val tokens = tokenize()
        return tokens.drop(1).windowed(2, step = 2).fold(tokens.first().evaluatePart1()) { acc, (operator, operand) ->
            when (operator) {
                "+" -> acc + operand.evaluatePart1()
                "*" -> acc * operand.evaluatePart1()
                else -> error("?? $acc $operator $operand")
            }
        }
    }

    private fun String.evaluatePart2(): Long {
        trim().toLongOrNull()?.let { return it }

        val tokens = tokenize().toMutableList()
        val operationsInOrder = listOf<Pair<String, Long.(Long) -> Long>>("+" to Long::plus, "*" to Long::times)
        operationsInOrder.forEach { (operator, operation) ->
            while (tokens.contains(operator)) {
                val idx = tokens.indexOf(operator)
                val value = tokens[idx - 1].evaluatePart2().operation(tokens[idx + 1].evaluatePart2())
                tokens[idx - 1] = value.toString()
                tokens.removeAt(idx)
                tokens.removeAt(idx)
            }
        }

        return tokens.single().toLong()
    }

    private fun String.tokenize(): List<String> {
        var s = this.trim()
        val tokens = mutableListOf<String>()
        do {
            val (token, rest) = s.trim().nextToken()
            if (token != null) tokens += token
            s = rest
        } while (token != null)
        return tokens
    }

    private fun String.nextToken(): Pair<String?, String> {
        return when (val c = firstOrNull()) {
            null -> null to ""
            in '0'..'9' -> takeWhile { it.isDigit() } to dropWhile { it.isDigit() }
            '(' -> matchingClosingParenthesisAt().let {
                substring(1, it) to drop(it + 1)
            }

            else -> c.toString() to drop(1)
        }
    }

    private fun String.matchingClosingParenthesisAt(): Int {
        var depth = 0
        forEachIndexed { idx, c ->
            when (c) {
                '(' -> depth++
                ')' -> depth--
            }
            if (depth == 0)
                return idx
        }
        error("Unbalanced parenthesis")
    }

}

fun main() {
    Day18().solve()
}