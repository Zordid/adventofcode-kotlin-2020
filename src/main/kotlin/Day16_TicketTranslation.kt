import utils.productAsLong

class Day16 : Day(16, title = "Ticket Translation") {

    private val fields: Map<String, List<IntRange>> = input.filter { ':' in it.dropLast(1) }.associate {
        val n = it.extractAllIntegers()
        it.split(":")[0] to listOf(n[0]..-n[1], n[2]..-n[3])
    }.show("Fields")

    private val tickets: List<List<Int>> = input.filter { it.isNotEmpty() && ':' !in it }
        .map { it.extractAllIntegers() }.show("Tickets")

    override fun part1() = tickets.drop(1).sumOf { ticketValues ->
        ticketValues.filter { value -> fields.values.none { value fits it } }.sum()
    }

    override fun part2(): Long {
        val myTicket = tickets.first()
        val validTickets = tickets.drop(0).filter { ticketValues ->
            ticketValues.all { value -> fields.values.any { value fits it } }
        }

        val fieldFits = myTicket.indices.map { fieldNo ->
            fieldNo to fields.entries.filter { (_, ranges) ->
                validTickets.map { it[fieldNo] }.all { it fits ranges }
            }.map { it.key }
        }

        val fieldAssignments = fieldFits.sortedBy { it.second.size }
            .fold(emptyMap<String, Int>()) { acc, (fieldNo, possibleFields) ->
                acc + ((possibleFields - acc.keys).single() to fieldNo)
            }

        return fieldAssignments.entries
            .filter { it.key.startsWith("departure") }
            .map { myTicket[it.value] }
            .productAsLong()
    }

    private infix fun Int.fits(ranges: List<IntRange>) = ranges.any { this in it }

}

fun main() {
    Day16().solve()
}