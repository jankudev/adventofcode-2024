package day7

import kotlin.test.*

data class Example(
    val result: Int,
    val numbers: List<Int>,
    val solutions: List<String>
)

class BridgeRepairTest {

    @Test
    fun `generateOperations - size 0`() {
        val operations = BridgeRepair.generateOpCombinations(0)

        println(operations)
        assertTrue(operations.isEmpty())
    }


    @Test
    fun `generateOperations - size 1`() {
        val operations = BridgeRepair.generateOpCombinations(1)

        assertEquals(3, operations.size)
        assertContentEquals(listOf(listOf(Op.PLUS), listOf(Op.TIMES), listOf(Op.CONCAT)), operations)
    }

    @Test
    fun `generateOperations - size 2`() {
        val operations = BridgeRepair.generateOpCombinations(2)

        assertEquals(9, operations.size)
        assertContentEquals(listOf(
            listOf(Op.PLUS, Op.PLUS), listOf(Op.PLUS, Op.TIMES), listOf(Op.PLUS, Op.CONCAT),
            listOf(Op.TIMES, Op.PLUS), listOf(Op.TIMES, Op.TIMES), listOf(Op.TIMES, Op.CONCAT),
            listOf(Op.CONCAT, Op.PLUS), listOf(Op.CONCAT, Op.TIMES), listOf(Op.CONCAT, Op.CONCAT)
        ), operations)
    }

    @Test
    fun `generate all possibilities for 1, 2`() {
        val solutions = BridgeRepair.findAllPossibilities(listOf(1, 2))

        assertEquals(3, solutions.size)
        assertContentEquals(listOf("3: 1 + 2", "2: 1 * 2", "12: 1 || 2"), solutions.map { it.toString() }.toList())
    }

    @Test
    fun `example input - part one - 190 (10 19)`() {
        val solutions = BridgeRepair.findSolutions(190, listOf(10, 19))

        println(solutions)

        assertEquals(1, solutions.size)
        assertEquals("190: 10 * 19", solutions[0].toString())
    }

    @Test
    fun `example data for part one`() {
        val examples = listOf(
            Example(190, listOf(10, 19), listOf("190: 10 * 19")),
            Example(3267, listOf(81, 40, 27), listOf("3267: 81 + 40 * 27", "3267: 81 * 40 + 27")),
            Example(292, listOf(11, 6, 16, 20), listOf("292: 11 + 6 * 16 + 20")),
            Example(156, listOf(15, 6), listOf("156: 15 || 6")),
            Example(7290, listOf(6, 8, 6, 15), listOf("7290: 6 * 8 || 6 * 15")),
            Example(192, listOf(17, 8, 14), listOf("192: 17 || 8 + 14"))
        )

        examples.forEach {
            val solutions = BridgeRepair.findSolutions(it.result.toLong(), it.numbers)

            assertEquals(it.solutions.size, solutions.size)
            assertContentEquals(it.solutions, solutions.map { it.toString() }.toList())
        }
    }
}