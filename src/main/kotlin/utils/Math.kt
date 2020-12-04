package utils

import kotlin.math.absoluteValue
import kotlin.math.pow

/**
 * Euclid's algorithm for finding the greatest common divisor of a and b.
 */
fun gcd(a: Int, b: Int): Int = if (b == 0) a.absoluteValue else gcd(b, a % b)
fun gcd(vararg n: Int): Int? = n.reduceOrNull(::gcd)
fun Iterable<Int>.gcd(): Int? = reduceOrNull(::gcd)

/**
 * Euclid's algorithm for finding the greatest common divisor of a and b.
 */
fun gcd(a: Long, b: Long): Long = if (b == 0L) a.absoluteValue else gcd(b, a % b)
fun gcd(vararg n: Long): Long? = n.reduceOrNull(::gcd)
fun Iterable<Long>.gcd(): Long? = reduceOrNull(::gcd)

/**
 * Find the least common multiple of a and b using the gcd of a and b.
 */
fun lcm(a: Long, b: Long) = a * b / gcd(a, b)
fun lcm(vararg n: Int): Int? = n.reduceOrNull(::lcm)
fun Iterable<Int>.lcm(): Int? = reduceOrNull(::lcm)

/**
 * Find the least common multiple of a and b using the gcd of a and b.
 */
fun lcm(a: Int, b: Int) = a * b / gcd(a, b)
fun lcm(vararg n: Long): Long? = n.reduceOrNull(::lcm)
fun Iterable<Long>.lcm(): Long? = reduceOrNull(::lcm)

/**
 * Simple algorithm to find the primes of the given Int.
 */
fun Int.primes(): Sequence<Int> = sequence {
    var n = this@primes
    var j = 2
    while (j * j <= n) {
        while (n % j == 0) {
            yield(j)
            n /= j
        }
        j++
    }
    if (n > 1)
        yield(n)
}

/**
 * Simple algorithm to find the primes of the given Long.
 */
fun Long.primes(): Sequence<Long> = sequence {
    var n = this@primes
    var j = 2L
    while (j * j <= n) {
        while (n % j == 0L) {
            yield(j)
            n /= j
        }
        j++
    }
    if (n > 1)
        yield(n)
}

infix fun Number.pow(power: Number): Double =
    this.toDouble().pow(power.toDouble())

infix fun Int.pow(power: Int): Int =
    this.toDouble().pow(power.toDouble()).toInt()
