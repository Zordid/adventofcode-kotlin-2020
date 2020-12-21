import java.math.BigInteger

class Day13 : Day(13, title = "Shuttle Search") {

    private val earliest = input[0].toInt().show("Earliest timestamp")
    private val busses = input[1].split(",").map { it.toIntOrNull() }

    override fun part1() =
        busses.filterNotNull().map { it to if (earliest % it == 0) 0 else (earliest / it + 1) * it - earliest }
            .minByOrNull { it.second }?.let { it.first * it.second }

    override fun part2(): BigInteger {
        val x =
            busses.withIndex().filter { it.value != null }.map { it.value!!.toBigInteger() to it.index.toBigInteger() }
                .sortedByDescending { it.first }

        val r = x.reduce { (aPeriod, aPhase), (period, phase) ->
            combinedPhasedRotations(aPeriod, aPhase, period, phase)
        }

        return r.first - r.second
    }
}

fun extendedGcd(a: BigInteger, b: BigInteger): Triple<BigInteger, BigInteger, BigInteger> {
    var oldR = a
    var r = b
    var oldS = BigInteger.ONE // 1L
    var s = BigInteger.ZERO // 0L
    var oldT = BigInteger.ZERO // 0L
    var t = BigInteger.ONE // 1L
    while (r != BigInteger.ZERO) {
        val (quotient, remainder) = oldR / r to oldR % r

        oldR = r
        r = remainder

        s = oldS.also { oldS = s } - quotient * s
        t = oldT.also { oldT = t } - quotient * t
    }
    return Triple(oldR, oldS, oldT)
}

fun combinedPhasedRotations(
    aPeriod: BigInteger,
    aPhase: BigInteger,
    bPeriod: BigInteger,
    bPhase: BigInteger
): Pair<BigInteger, BigInteger> {
    val (gcd, s, _) = extendedGcd(aPeriod, bPeriod)
    val phaseDifference = aPhase - bPhase
    val pdMult = phaseDifference / gcd
    val pdRemainder = phaseDifference % gcd
    if (pdRemainder != BigInteger.ZERO)
        error("No sync")
    val combinedPeriod = (aPeriod / gcd) * bPeriod
    val combinedPhase = (aPhase - s * pdMult * aPeriod) % combinedPeriod
    return combinedPeriod to combinedPhase
}


fun main() {
//    globalTestData = """
//        939
//        7,13,x,x,59,x,31,19
//    """.trimIndent()
    Day13().solve()
}