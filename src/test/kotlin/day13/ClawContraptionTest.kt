package day13

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Claw machine, 2 buttons - A and B.
 * - push A -> cost 3 tokens
 * - push B -> cost 1 token
 *
 * Each button moves right (x) and forward (y) by specific amounts - input configuration.
 *
 * Win prize = (x,y) == prizeCoords
 *
 * Find for each claw-machine cheapest movement to win prize
 * - movement might not be possible to find !
 * - top button presses 100 each
 */

class ClawContraptionTest {

    @Test
    fun `example data - claw machine 1`() {
        val input = """
            Button A: X+94, Y+34
            Button B: X+22, Y+67
            Prize: X=8400, Y=5400
        """.trimIndent()

        val movements = ClawContraption.findCheapestMovements(input)
        val price = ClawContraption.calculatePrice(movements)

        assertEquals(Pair(80, 40), movements)
        assertEquals(280, price)
    }

    @Test
    fun `example data - claw machine 2 - no viable movement`() {
        val input = """
            Button A: X+26, Y+66
            Button B: X+67, Y+21
            Prize: X=12748, Y=12176
        """.trimIndent()

        val movements = ClawContraption.findCheapestMovements(input)

        assertEquals(ClawContraption.NO_VIABLE_MOVEMENT, movements)
    }

    @Test
    fun `example data - claw machine 3`() {
        val input = """
            Button A: X+17, Y+86
            Button B: X+84, Y+37
            Prize: X=7870, Y=6450
        """.trimIndent()

        val movements = ClawContraption.findCheapestMovements(input)
        val price = ClawContraption.calculatePrice(movements)

        assertEquals(Pair(38, 86), movements)
        assertEquals(200, price)
    }

    @Test
    fun `example data - claw machine 4 - no viable movement`() {
        val input = """
            Button A: X+69, Y+23
            Button B: X+27, Y+71
            Prize: X=18641, Y=10279
        """.trimIndent()

        val movements = ClawContraption.findCheapestMovements(input)

        assertEquals(ClawContraption.NO_VIABLE_MOVEMENT, movements)
    }
}