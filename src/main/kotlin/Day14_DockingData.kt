import Day14.Command.Mask
import Day14.Command.MemWrite

class Day14 : Day(14, title = "Docking Data") {

    private val program = parsedInput {
        when (cols[0]) {
            "mask" -> Mask(cols[2])
            else -> MemWrite(longs[0], longs[1])
        }
    }

    sealed class Command {
        data class MemWrite(val address: Long, val value: Long) : Command()
        data class Mask(val mask: String) : Command() {
            val x = mask.replace('1', '0').replace('X', '1').toLong(2)
            val nonX = mask.replace('X', '0').toLong(2)
        }
    }

    override fun part1(): Long {
        var mask = program.first() as Mask
        val mem = mutableMapOf<Long, Long>()
        program.forEach { instruction ->
            when (instruction) {
                is Mask -> mask = instruction
                is MemWrite -> mem[instruction.address] = (instruction.value and mask.x) or mask.nonX
            }
        }
        return mem.values.sum()
    }

    override fun part2(): Long {
        var mask = program.first() as Mask
        val valueToAddresses = program.mapNotNull { instruction ->
            when (instruction) {
                is Mask -> null.also { mask = instruction }
                is MemWrite -> instruction.value to mask.decodeAddress(instruction.address)
            }
        }

        return valueToAddresses.mapIndexed { idx, (value, addresses) ->
            val upcomingInstructions = valueToAddresses.subList(idx + 1, valueToAddresses.size)
            val affectedAddresses = addresses.count { a -> upcomingInstructions.none { a in it.second } }
            affectedAddresses * value
        }.sum()
    }

    private fun Mask.decodeAddress(address: Long): Set<Long> =
        if (x == 0L) setOf(address or nonX) else {
            val baseAddress = address or nonX
            val floatingBits = x.countOneBits()
            (0 until (1 shl floatingBits)).map { it.distributeBits(baseAddress, x) }.toSet()
        }

    private fun Int.distributeBits(address: Long, floating: Long): Long {
        var p = this
        var a = address
        for (bit in 0..35) {
            val mask = 1L shl bit
            if (floating and mask != 0L) {
                a = if (p and 1 != 0)
                    a or mask
                else
                    a and mask.inv()
                p = p shr 1
            }
        }
        return a
    }

}

fun main() {
    Day14().solve()
}