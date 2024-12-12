package day11

import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.test.assertEquals

/**
 * Approach notes
 *
 * Pebble line evolves with every 'blink'.
 * Blink = step in transformation (single transformation of whole pebble line).
 *
 * Evolution has 2 possible outcomes:
 * a) change number engraved on pebble
 * b) pebble splits into two shifting pebbles to the right (order is preserved)
 *
 * Evolution rules (first applies):
 * - engraving 0 -> 1
 * - number has even digits -> split into 2 pebbles with left digits to left, right digits to right (no trailing zeros
 * - engraving x -> x * 2024
 */
class PlutonianPebblesTest {

    @Test
    fun `rules - engraving on 0 to 1`() {
        val pebble: String = "0"
        val pebbleEvolved = PlutonianPebbles.evolvePebble(pebble)
        assertEquals("1", pebbleEvolved)
    }

    @Test
    fun `rules - engraving on odd digits to x * 2024`() {
        assertEquals("2024", PlutonianPebbles.evolvePebble("1"))
        assertEquals("4048", PlutonianPebbles.evolvePebble("2"))
        assertEquals((111*2024).toString(), PlutonianPebbles.evolvePebble("111"))
    }

    @Test
    fun `rules - pebble split`() {
        assertEquals("1 0", PlutonianPebbles.evolvePebble("10"))
        assertEquals("12 34", PlutonianPebbles.evolvePebble("1234"))
    }

    @Test
    fun `example data part 1 - step 0`() {
        val input = "125 17"
        assertEquals(input, PlutonianPebbles.evolvePebbles(input, 0))
    }

    @Test
    fun `example data part 1 - step 1`() {
        val input = "125 17"
        assertEquals("253000 1 7", PlutonianPebbles.evolvePebbles(input, 1))
    }

    @Test
    fun `example data part 1 - step 2`() {
        val input = "125 17"
        assertEquals("253 0 2024 14168", PlutonianPebbles.evolvePebbles(input, 2))
    }

    @Test
    fun `example data part 1 - step 3`() {
        val input = "125 17"
        assertEquals("512072 1 20 24 28676032", PlutonianPebbles.evolvePebbles(input, 3))
    }

    @Test
    fun `example data part 1 - step 4`() {
        val input = "125 17"
        assertEquals("512 72 2024 2 0 2 4 2867 6032", PlutonianPebbles.evolvePebbles(input, 4))
    }

    @Test
    fun `example data part 1 - step 5`() {
        val input = "125 17"
        assertEquals("1036288 7 2 20 24 4048 1 4048 8096 28 67 60 32", PlutonianPebbles.evolvePebbles(input, 5))
    }

    @Test
    fun `example data part 1 - step 6`() {
        val input = "125 17"
        assertEquals("2097446912 14168 4048 2 0 2 4 40 48 2024 40 48 80 96 2 8 6 7 6 0 3 2", PlutonianPebbles.evolvePebbles(input, 6))
    }

    @Test
    fun `example data part 1 - count stones after 25 blinks`() {
        val input = "125 17"
        assertEquals(55312, PlutonianPebbles.countStonesAfterBlinks(input, 25))
        assertEquals(55312, PlutonianPebbles.countStonesAfterBlinksWithCounter(input, 25).toInt())
    }
}