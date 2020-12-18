import java.math.BigInteger

class Day18 : Day(18, title = "Operation Order") {

    val homework = input

    fun String.evaluate(): Long {
        val s = this.trim().replace(" ", "")
        s.toLongOrNull()?.let { return it }

        var (operand1, index) = if (s.firstOrNull() == '(') {
            val pTerm = s.asSequence().drop(1).runningFold("" to 1) { (r, p), c ->
                when (c) {
                    '(' -> r + c to p + 1
                    ')' -> r + c to p - 1
                    else -> r + c to p
                }
            }.takeWhile { (_, p) -> p > 0 }.last().first
            pTerm.evaluate() to pTerm.length + 2
        } else {
            val number = s.takeWhile { it in '0'..'9' }
            number.toLongOrNull()!! to number.length
        }
        var operator = s[index]
        index++
        while (index < s.length) {
            val (operand2, newIndex) = if (s[index] == '(') {
                val pTerm = s.asSequence().drop(index + 1).runningFold("" to 1) { (r, p), c ->
                    when (c) {
                        '(' -> r + c to p + 1
                        ')' -> r + c to p - 1
                        else -> r + c to p
                    }
                }.takeWhile { (_, p) -> p > 0 }.last().first
                pTerm.evaluate() to pTerm.length + 2
            } else {
                val number = s.drop(index).takeWhile { it in '0'..'9' }
                number.toLongOrNull()!! to number.length
            }
            operand1 = when (operator) {
                '+' -> operand1 + operand2
                '*' -> operand1 * operand2
                else -> error("$operand1 $operator $operand2")
            }
            index += newIndex
            if (index < s.length)
                operator = s[index++]
        }
        return operand1
    }

    override fun part1(): Any? {
        return homework.map { it.evaluate() }.sum()
    }

    override fun part2(): Any? {
        //   return "1 + (2 * 3) + (4 * (5 + 6))".evaluate2()
        //   return "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2".evaluate2()
        return homework.map {
           // val vOld = it.evaluate2()
            val v = it.evaluate3()
//            if (v != vOld) {
//                println("$it = $v")
//                println("WRONG: $vOld")
//            }
            v
        }.reduce(BigInteger::plus)
    }

    fun String.evaluate3(): BigInteger {
        val s = this.trim().replace(" ", "")
        if (s.hasBalancedP())
            return s.drop(1).dropLast(1).evaluate3()

        val chain = mutableListOf<String>()
        var index = 0
        while (index < s.length) {
            val op = s.drop(index).parseNextNumber()
            index += op.length
            chain += op
            if (index < s.length) {
                chain += s.drop(index).take(1)
                index++
            }
        }

//        println(s)
//        println(chain)

        while (chain.contains("+")) {
            val indexOfPlus = chain.indexOf("+")
            val value = chain[indexOfPlus - 1].evaluate3() + chain[indexOfPlus + 1].evaluate3()
            chain[indexOfPlus - 1] = value.toString()
            chain.removeAt(indexOfPlus)
            chain.removeAt(indexOfPlus)
        }
        while (chain.contains("*")) {
            val indexOfPlus = chain.indexOf("*")
            val value = chain[indexOfPlus - 1].evaluate3() * chain[indexOfPlus + 1].evaluate3()
            chain[indexOfPlus - 1] = value.toString()
            chain.removeAt(indexOfPlus)
            chain.removeAt(indexOfPlus)
        }

        return chain.single().toBigInteger()
    }

    fun String.evaluate2(): BigInteger {
        val s = this.trim().replace(" ", "").let {
            if (it.firstOrNull() == '(' && it.lastOrNull() == ')')
                it.drop(1).dropLast(1)
            else
                it
        }

        //println("Ev: $s")
        s.toLongOrNull()?.let { return it.toBigInteger() }

        val op1 = s.parseNextNumber()
        if (op1.length == s.length) return op1.evaluate2()

        var result = op1.evaluate2()
        var index = op1.length
        var operator: Char? = s[index++]
        while (operator == '+') {
            val op2 = s.drop(index).parseNextNumber()
            index += op2.length
            result += op2.evaluate2()
            operator = s.getOrNull(index++)
        }
        if (operator == '*') {
            result *= s.drop(index).evaluate2()
        }
        return result
    }

    fun String.parseNextNumber(): String =
        if (startsWith("("))
            this.asSequence().runningFold("" to 0) { (r, p), c ->
                when (c) {
                    '(' -> r + c to p + 1
                    ')' -> r + c to p - 1
                    else -> r + c to p
                }
            }.drop(1).takeWhile { (_, p) -> p > 0 }.last().first + ")"
        else
            this.takeWhile { it in '0'..'9' }

}

private fun String.hasBalancedP(): Boolean {
    return if (firstOrNull() == '(' && lastOrNull() == ')') {
        var depth = 1
        drop(1).dropLast(1).forEach {
            when(it) {
                '(' -> depth++
                ')' -> depth--
            }
            if (depth<1)
                return false
        }
        return true
    } else false
}

fun main() {
    Day18().solve()
    globalTestData = """
        1 + (2 * 3) + (4 * (5 + 6))
        2 * 3 + (4 * 5)
        5 + (8 * 3 + 9 + 3 * 4 * 3)
        5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))
        ((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2
    """.trimIndent()
}