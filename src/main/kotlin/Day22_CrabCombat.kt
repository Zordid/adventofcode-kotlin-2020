class Day22 : Day(22, title = "Crab Combat") {

    private val startDecks =
        chunkedInput().map { it.drop(1).map(String::toInt) }.show("Players")

    override fun part1(): Int {
        val decks = startDecks.map { it.toMutableList() }
        while (decks.all { it.isNotEmpty() }) {
            val cards = decks.map { it.removeFirst() }
            val winner = cards.indexOf(cards.maxOrNull()!!)
            decks[winner].addAll(cards.sortedDescending())
        }
        return decks.flatten().asReversed().mapIndexed { idx, value -> (idx + 1) * value }.sum()
    }

    override fun part2(): Int = playRecursiveCombat(startDecks[0], startDecks[1]).let { winnerScore }

    private var winnerScore = 0
    private val verbose = false

    private fun playRecursiveCombat(startDeck0: List<Int>, startDeck1: List<Int>, game: Int = 1): Int {
        if (verbose)
            println("\n=== Game $game ===")
        val deck0 = startDeck0.toMutableList()
        val deck1 = startDeck1.toMutableList()
        val mem: List<MutableSet<List<Int>>> = listOf(mutableSetOf(), mutableSetOf())
        var round = 0
        var winner = -1
        while (winner < 0) {
            round++
            if (verbose) {
                println("\n-- Round $round (Game $game) --")
                println("Player 1's deck: $deck0")
                println("Player 2's deck: $deck1")
            }
            // safety first!
            if (!(mem[0].add(deck0.toList()) && mem[1].add(deck1.toList()))) {
                if (verbose)
                    println("We have seen this constellation before! => Player 1 wins game $game!")
                return 0
            }

            val card0 = deck0.removeFirst()
            val card1 = deck1.removeFirst()
            if (verbose) {
                println("Play 1 plays: $card0")
                println("Play 2 plays: $card1")
            }
            val roundWinner = if (deck0.size >= card0 && deck1.size >= card1) {
                if (verbose)
                    println("Playing a sub-game to determine the winner...")
                val rW = playRecursiveCombat(deck0.subList(0, card0), deck1.subList(0, card1), game + 1)
                if (verbose)
                    println("\n...anyway, back to game $game.")
                rW
            } else {
                if (card0 > card1) 0 else 1
            }
            if (verbose)
                println("Player ${roundWinner + 1} wins round $round of game $game!")
            if (roundWinner == 0) {
                deck0.add(card0)
                deck0.add(card1)
            } else {
                deck1.add(card1)
                deck1.add(card0)
            }
            when {
                deck0.isEmpty() -> winner = 1
                deck1.isEmpty() -> winner = 0
            }
        }
        if (verbose)
            println("The winner of game $game is player ${winner + 1}!")
        if (game == 1) {
            winnerScore = (deck0 + deck1).asReversed().mapIndexed { idx, value -> (idx + 1) * value }.sum()
            if (verbose) {
                println("\n\n== Post-game results ==")
                println("Player 1's deck: $deck0")
                println("Player 2's deck: $deck1")
            }
        }
        return winner
    }
}

fun main() {
    Day22().solve()
}