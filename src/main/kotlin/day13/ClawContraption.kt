package day13

import dev.janku.katas.aoc2024.utils.ResourcesUtils
import java.math.BigDecimal

/**
 * Use arithmetic to find the cheapest movements to win the most prizes
 * (movements for both A and B buttons sum to get to the prize
 *
 * Simple arithmetic equation with 2 variables and 2 equations:
 * A: x+94, y+34
 * B: x+22, y+67
 * Prize: x=8400, y=5400
 *
 * 94a + 22b = 8400
 * 34a + 67b = 5400
 *
 */
class ClawContraption {

    companion object {

        val BTN_REGEX = Regex("Button [AB]: X\\+(\\d+), Y\\+(\\d+)")
        val PRIZE_REGEX = Regex("Prize: X=(\\d+), Y=(\\d+)")

        val NO_VIABLE_MOVEMENT = Pair(-1L, -1L)
        val PART_TWO_DIST_MODIFIER = BigDecimal(10000000000000)

        fun extractCoeficientsBtn(btn: String): Pair<BigDecimal, BigDecimal> {
            return BTN_REGEX.matchEntire(btn)!!.destructured.let { (x, y) -> Pair(BigDecimal(x), BigDecimal(y)) }
        }

        fun extractPrize(prize: String): Pair<BigDecimal, BigDecimal> {
            return PRIZE_REGEX.matchEntire(prize)!!.destructured.let { (x, y) -> Pair(BigDecimal(x), BigDecimal(y)) }
        }

        fun findCheapestMovements(btnA: String, btnB: String, prize: String, prize10e13: Boolean) : Pair<Long, Long> {
            val (x1, y1) = extractCoeficientsBtn(btnA)
            val (x2, y2) = extractCoeficientsBtn(btnB)
            val (x, y) = extractPrize(prize).let { (x, y) -> if (prize10e13) Pair(x.plus(PART_TWO_DIST_MODIFIER), y.plus(PART_TWO_DIST_MODIFIER)) else Pair(x, y) }

            try {
                val b = ((x1.multiply(y)).minus(y1.multiply(x))).divide((x1.multiply(y2)).minus(y1.multiply(x2)))
                val a = (x.minus(x2.multiply(b))).divide(x1)

                if (BigDecimal(a.toLong()).equals(a) && BigDecimal(b.toLong()).equals(b) && a.toLong() >= 0 && b.toLong() >= 0) {
                    return Pair(a.toLong(), b.toLong())
                }

                return NO_VIABLE_MOVEMENT
            } catch (ArithmeticException: Exception) {
                return NO_VIABLE_MOVEMENT
            }
        }

        fun calculatePrice(movements: Pair<Long, Long>) : Long {
            return movements.first * 3 + movements.second
        }

        fun calculateForAllClawContraptions(lines: List<String>, partTwo: Boolean = false): Long {
            return lines.chunked(4).map {
                (btnA, btnB, prize, _) -> findCheapestMovements(btnA, btnB, prize, partTwo)
            }.filter {
                it != NO_VIABLE_MOVEMENT
            }.map {
                calculatePrice(it)
            }.sum()
        }
    }
}

fun main() {
    val input = ResourcesUtils.getResourceAsLinesStream("day13-challenge-input.txt").reduce {
            acc, line -> "$acc\n$line"
    }.get()

    println("Min tokens to win most prizes: ${ClawContraption.calculateForAllClawContraptions(input.split("\n"))}")
    println("Min tokens to win most prizes: ${ClawContraption.calculateForAllClawContraptions(input.split("\n"), partTwo = true)}")
}