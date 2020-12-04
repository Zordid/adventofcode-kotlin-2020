class Day04 : Day(4, title = "Password Processing") {

    val passports = input.joinToString(" ").split("  ")
        .map { it.toMap(" ", ":") }
        .show("Passports")

    val validators = mapOf<String, (String) -> Boolean>(
        "byr" to { it.toIntOrNull() in 1920..2002 },
        "iyr" to { it.toIntOrNull() in 2010..2020 },
        "eyr" to { it.toIntOrNull() in 2020..2030 },
        "hgt" to {
            (it.endsWith("in") && it.dropLast(2).toIntOrNull() in 59..76) ||
                    (it.endsWith("cm") && it.dropLast(2).toIntOrNull() in 150..193)
        },
        "hcl" to { it.matches("#[0-9a-f]{6}".toRegex()) },
        "ecl" to { it in "amb blu brn gry grn hzl oth".split(" ") },
        "pid" to { it.matches("[0-9]{9}".toRegex()) })

    override fun part1() = passports.count { it.keys.containsAll(validators.keys) }

    override fun part2() = passports.count { passport ->
        validators.all { (field, validator) -> validator(passport[field] ?: "") }
    }

    private fun String.toMap(kvpDelimiter: String, kvDelimiter: String) =
        split(kvpDelimiter).map { it.split(kvDelimiter, limit = 2).let { (k, v) -> k to v } }.toMap()

}

fun main() {
    Day04().solve()
}