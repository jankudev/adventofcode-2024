package day11

import java.math.BigDecimal

typealias Counter = Map<String, Long>

fun String.stringIntAsString(): String = this.toInt().toString()

class PlutonianPebbles {
    companion object {

        fun evolvePebble(pebble: String): String {
            return when {
                pebble == "0" -> "1"
                pebble.length % 2 == 0 -> {
                    val left = pebble.substring(0, pebble.length / 2).stringIntAsString()
                    val right = pebble.substring(pebble.length / 2).stringIntAsString()
                    "$left $right"
                }

                else -> (BigDecimal(pebble).multiply(BigDecimal(2024))).toString()
                //else -> throw IllegalStateException("Unexpected state, can't apply rules for pebble: $pebble")
            }
        }

        /**
         * Trying with recursion
         */
        fun evolvePebbles(pebbles: String, step: Int): String {
            return when {
                step == 0 -> pebbles
                else -> evolvePebbles(pebbles, step - 1).split(" ")
                    .map { evolvePebble(it) }
                    .joinToString(" ")
            }
        }

        /**
         * Trying without recursion
         */
        fun evolvePebblesNoRecursion(pebbles: String, steps: Int): String {
            return generateSequence(pebbles) { current -> current.split(" ").map(::evolvePebble).joinToString(" ") }
                .take(steps + 1)
                .toList().last()
        }

        fun countStonesAfterBlinks(pebbles: String, blinks: Int): Int {
//            return evolvePebbles(pebbles, blinks).split(" ").size
            return evolvePebblesNoRecursion(pebbles, blinks).split(" ").size
        }

        /**
         * Trying with a counter of stones with recursion - based on a hint from Reddit
         * (couldn't figure this one out :-/ and also having some overflow hidden issues on large numbers)
         */
        fun evolve(pebbles: Counter): Counter {
            val newPebbles = mutableMapOf<String, Long>()
            pebbles.keys.forEach { pebble ->
                when {
                    pebble == "0" -> {
                        newPebbles["1"] = newPebbles.getOrDefault("1", 0) + pebbles[pebble]!!
                    }

                    pebble.length % 2 == 0 -> {
                        val left = pebble.substring(0, pebble.length / 2).stringIntAsString()
                        val right = pebble.substring(pebble.length / 2).stringIntAsString()
                        newPebbles[left] = newPebbles.getOrDefault(left, 0) + pebbles[pebble]!!
                        newPebbles[right] = newPebbles.getOrDefault(right, 0) + pebbles[pebble]!!
                    }

                    else -> {
                        val evolved = (BigDecimal(pebble).multiply(BigDecimal(2024))).toString()
                        newPebbles[evolved] = newPebbles.getOrDefault(evolved, 0) + pebbles[pebble]!!
                    }
                }
            }
            return newPebbles
        }

        fun countStonesAfterBlinksWithCounter(pebbles: String, blinks: Int): BigDecimal {
            var pebblesCounter = pebbles.split(" ").map { it to 1.toLong() }.toMap()
            (1..blinks).forEach {
                pebblesCounter = evolve(pebblesCounter)
            }
            return pebblesCounter.values.map { BigDecimal(it)}.reduce(BigDecimal::add)
        }
    }
}

fun main() {
    val input = "6571 0 5851763 526746 23 69822 9 989"

    println("Stones after 25 blinks: ${PlutonianPebbles.countStonesAfterBlinks(input, 25)}")
    println("Stones after 75 blinks: ${PlutonianPebbles.countStonesAfterBlinksWithCounter(input, 75)}")
}