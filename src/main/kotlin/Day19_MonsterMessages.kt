import utils.minMaxByOrNull

class Day19 : Day(19, title = "Monster Messages") {

    sealed class Rule {

        abstract fun expand(r: RuleSet): Collection<String>

        data class Direct(val d: String) : Rule() {
            override fun expand(r: RuleSet) = listOf(d)
        }

        data class Ref(val a: List<Int>) : Rule() {
            override fun expand(r: RuleSet) =
                when (a.size) {
                    1 -> r.expand(a[0])
                    2 -> r.expand(a[0]).flatMap { p ->
                        r.expand(a[1]).map { "$p$it" }
                    }
                    3 -> r.expand(a[0]).flatMap { p ->
                        r.expand(a[1]).flatMap { p2 ->
                            r.expand(a[2]).map { "$p$p2$it" }
                        }
                    }
                    else -> error(a)
                }
        }

        data class RefOr(val a: List<Int>, val b: List<Int>) : Rule() {

            private fun expandX(a: List<Int>, r: RuleSet) =
                when (a.size) {
                    1 -> r.expand(a[0])
                    2 -> r.expand(a[0]).flatMap { p ->
                        r.expand(a[1]).map { "$p$it" }
                    }
                    else -> error(a)
                }

            override fun expand(r: RuleSet) =
                expandX(a, r) + expandX(b, r)

        }
    }

    class RuleSet(private val rules: Map<Int, Rule>) {
        private val cache = mutableMapOf<Int, Set<String>>()
        fun expand(n: Int): Set<String> = cache.getOrPut(n) {
            rules[n]!!.expand(this).toSet()
        }
    }

    val rules = parsedInput(predicate = { it.contains(':') }) {
        if (cols[1].startsWith('"') && cols[1].endsWith('"'))
            ints[0] to Rule.Direct(cols[1].trim('"'))
        else if (cols.indexOf("|") > 0)
            ints[0] to Rule.RefOr(ints.subList(1, cols.indexOf("|")), ints.subList(cols.indexOf("|"), ints.size))
        else
            ints[0] to Rule.Ref(ints.subList(1, ints.size))
    }.toMap().show("Rules")

    val data = input.filter { it.isNotBlank() && ' ' !in it }.show("Data")

    val rulesSet = RuleSet(rules)

    override fun part1(): Any? {
        val r31 = rulesSet.expand(31)
        val r42 = rulesSet.expand(42)
        val c = data.map {
            it.windowed(8, 8).map { chunk ->
                when (chunk) {
                    in r31 -> 31
                    in r42 -> 42
                    else -> 0
                }
            }
        }.count { it == listOf(42, 42, 31) }
        return c // data.filter { it in rulesSet.expand(0) }.size
    }

    override fun part2(): Any? {
        val r31 = rulesSet.expand(31)
        val r42 = rulesSet.expand(42)

        val c = data.map {
            it.windowed(8, 8).map { chunk ->
                when (chunk) {
                    in r31 -> 31
                    in r42 -> 42
                    else -> 0
                }
            }
        }
            //.onEach { println(it) }
            .filter {
                val c42 = it.takeWhile { it == 42 }.count()
                val c31 = it.reversed().takeWhile { it == 31 }.count()
                c42 + c31 == it.size && c31 > 0 && c42 > c31
            }
           // .onEach { println(it) }
            .count()

        return c
    }

}

fun main() {
    Day19().solve()
    globalTestData = """
0: 4 1 5
1: 2 3 | 3 2
2: 4 4 | 5 5
3: 4 5 | 5 4
4: "a"
5: "b"

ababbb
bababa
abbbab
aaabbb
aaaabbb
    """.trimIndent()
}