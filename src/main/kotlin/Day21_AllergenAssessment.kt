typealias Ingredient = String
typealias Allergen = String

class Day21 : Day(21, title = "Allergen Assessment") {

    private val foods: List<Pair<List<Ingredient>, List<Allergen>>> =
        matchedInput(Regex("(.*) \\(contains (.*)\\)")) {
            it[1].split(" ") to it[2].split(", ")
        }

    private val ingredients = foods.flatMap { it.first }.toSet()
    private val allergens = foods.flatMap { it.second }.toSet()

    private val ingredientMightContain = ingredients.associateWith { i ->
        val foodsWithI = foods.filter { i in it.first }.flatMap { it.second }.toSet()
        foodsWithI
    }

    private val allergenCouldBeIn = allergens.associateWith { a ->
        val ingredientsContainingA = foods.filter { a in it.second }.map { it.first.toSet() }
        ingredientsContainingA
    }

    override fun part1(): Int {
        return cantContainAnything()
            .sumOf { i -> foods.count { i in it.first } }
    }

    override fun part2(): String {
        val startSetup: Map<Ingredient, Allergen?> = cantContainAnything().associateWith { null }
        return findAssociations(startSetup)!!.entries.filter { it.value != null }.sortedBy { it.value }
            .joinToString(",") { it.key }
    }

    private fun cantContainAnything() = ingredients.filter {
        allergens.none { allergen -> it canContain allergen }
    }

    private infix fun Ingredient.canContain(allergen: Allergen): Boolean =
        foods.filter { this !in it.first }.none { allergen in it.second }

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

        val associationsSet = associations.values.toSet()
        val (ingredient, possibleAllergen) = notAssignedIngredients
            .map { it to ingredientMightContain[it]!! - associationsSet }
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