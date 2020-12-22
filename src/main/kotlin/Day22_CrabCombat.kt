class Day22 : Day(22, title = "Crab Combat") {

    private val startDecks =
        chunkedInput().map { it.drop(1).map(String::toInt) }.show("Players")

    override fun part1(): Int {
        val decks = startDecks.map { it.toMutableList() }
        while (decks.all { it.isNotEmpty() }) {
            val cards = decks.map { it.removeFirst() }
            val winner = if (cards[0] > cards[1]) 0 else 1
            decks[winner].addAll(cards.sortedDescending())
        }
        return decks.flatten().asReversed().mapIndexed { idx, value -> (idx + 1) * value }.sum()
    }

    override fun part2(): Int = playRecursiveCombat(startDecks).let { winnerScore }

    private var winnerScore = 0
    private val verbose = false

    private fun playRecursiveCombat(pStart: List<List<Int>>, game: Int = 1): Int {
        if (verbose)
            println("\n=== Game $game ===")
        val decks = pStart.map { it.toMutableList() }
        val mem: List<MutableSet<List<Int>>> = listOf(mutableSetOf(), mutableSetOf())
        var round = 0
        while (decks.all { it.isNotEmpty() }) {
            round++
            if (verbose) {
                println("\n-- Round $round (Game $game) --")
                println("Player 1's deck: ${decks[0]}")
                println("Player 2's deck: ${decks[1]}")
            }
            // safety first!
            if (decks[0] in mem[0] || decks[1] in mem[1]) {
                if (verbose)
                    println("We have seen this constellation before! => Player 1 wins game $game!")
                return 0
            }
            mem[0].add(decks[0].toList())
            mem[1].add(decks[1].toList())

            val card0 = decks[0].removeFirst()
            val card1 = decks[1].removeFirst()
            if (verbose) {
                println("Play 1 plays: $card0")
                println("Play 2 plays: $card1")
            }
            val roundWinner = if (decks[0].size >= card0 && decks[1].size >= card1) {
                if (verbose)
                    println("Playing a sub-game to determine the winner...")
                val w = playRecursiveCombat(listOf(decks[0].subList(0, card0), decks[1].subList(0, card1)), game + 1)
                if (verbose)
                    println("\n...anyway, back to game $game.")
                w
            } else {
                if (card0 > card1) 0 else 1
            }
            if (verbose)
                println("Player ${roundWinner + 1} wins round $round of game $game!")
            if (roundWinner == 0) {
                decks[0].add(card0)
                decks[0].add(card1)
            } else {
                decks[1].add(card1)
                decks[1].add(card0)
            }
        }
        val winner = decks.indexOfFirst { it.isNotEmpty() }
        if (verbose)
            println("The winner of game $game is player ${winner + 1}!")
        if (game == 1) {
            winnerScore = decks.flatten().asReversed().mapIndexed { idx, value -> (idx + 1) * value }.sum()
            if (verbose) {
                println("\n\n== Post-game results ==")
                println("Player 1's deck: ${decks[0]}")
                println("Player 2's deck: ${decks[1]}")
            }
        }
        return winner
    }
}

fun main() {
    Day22().solve()
}