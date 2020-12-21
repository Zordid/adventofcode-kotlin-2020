class Day21 : Day(21, title = "Allergen Assessment") {

    private val foods = matchedInput(Regex("(.*) \\(contains (.*)\\)")) {
        it[1].split(" ") to it[2].split(", ")
    }

    private val ingredients = foods.flatMap { it.first }.toSet()
    private val allergens = foods.flatMap { it.second }.toSet()

    private val ingredientMightContain = ingredients.map { i ->
        val foodsWithI = foods.filter { i in it.first }.flatMap { it.second }.toSet()
        i to foodsWithI
    }.toMap()

    private val allergenCouldBeIn = allergens.map { a ->
        val ingredientsContainingA = foods.filter { a in it.second }.map { it.first.toSet() }
        a to ingredientsContainingA
    }.toMap()

    private val associations: Map<String, String?> by lazy { findAssociations()!! }

    override fun part1() =
        associations.filter { it.value == null }.map { it.key }
            .sumBy { i -> foods.count { i in it.first } }

    override fun part2() =
        associations.entries.filter { it.value != null }.sortedBy { it.value }.joinToString(",") { it.key }

    private fun findAssociations(associations: Map<String, String?> = emptyMap()): Map<String, String?>? {
        //println("Done with ${associations.size} associations!")
        if (associations.size == ingredients.size) {
            if (associations.values.filterNotNull().toSet() != allergens) {
                //println("Does not work!")
                return null
            }
            return associations
        }

        val notAssignedIngredients = ingredients - associations.keys

        val (ingredient, possibleAllergen) = notAssignedIngredients
            .map { it to ingredientMightContain[it]!! - associations.values }
            .minByOrNull { it.second.size }!!

        val solution = possibleAllergen.filter { a -> allergenCouldBeIn[a]!!.all { ingredient in it } } + null
        //println("Possibilities for $ingredient are $solution")
        return solution.mapNotNull { association ->
            //println("$ingredient contains $association?")
            findAssociations(associations + (ingredient to association))
        }.singleOrNull()
    }
}

fun main() {
    Day21().solve()
}