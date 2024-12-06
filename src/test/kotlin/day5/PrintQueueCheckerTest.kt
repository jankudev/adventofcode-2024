package dev.janku.katas.aoc2024.day5

import dev.janku.katas.aoc2024.day5.attempt1.PrintQueueChecker
import dev.janku.katas.aoc2024.day5.attempt1.model.Rules
import dev.janku.katas.aoc2024.day5.attempt2.PrintQueueChecker2
import dev.janku.katas.aoc2024.day5.attempt2.RulesParser
import dev.janku.katas.aoc2024.day5.attempt3.PrintQueueChecker3
import dev.janku.katas.aoc2024.day5.attemptBellmanFord.PrintQueueCheckerFloydWarshall
import org.junit.jupiter.api.assertTimeoutPreemptively
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PrintQueueCheckerTest {

    @Test
    fun `process the input - separate rules from queues`() {
        val input = """
            47|53
            97|13
            97|61

            75,47,61,53,29
            97,61,53,29,13
        """.trimIndent()

        val checker: PrintQueueChecker = PrintQueueChecker.fromInput(input)
        assertEquals(3, checker.rules.list.size)
        assertEquals(2, checker.queues.list.size)
    }

    @Test
    fun `check simple queue against rules`() {
        val input = """
            1|2
            2|3
            3|4
            10|3
            
            1,10,3,4
        """.trimIndent()
        assertTrue(PrintQueueChecker.fromInput(input).checkQueue(listOf(1, 10, 3, 4)))
    }

    @Test
    fun `check resilience against cycles in the rules`() {
        val input = """
            1|2
            2|3
            3|1
        """.trimIndent()
        assertTimeoutPreemptively(Duration.ofSeconds(1)){
            Rules.fromInput(input)
        }
    }

    @Test
    fun `example input - checks`() {
        val input = """
            47|53
            97|13
            97|61
            97|47
            75|29
            61|13
            75|53
            29|13
            97|29
            53|29
            61|53
            97|53
            61|29
            47|13
            75|47
            97|75
            47|61
            75|61
            47|29
            75|13
            53|13

            75,47,61,53,29
            97,61,53,29,13
            75,29,13
            75,97,47,61,53
            61,13,29
            97,13,75,29,47
        """.trimIndent()

        val checker = PrintQueueChecker.fromInput(input)
        assertTrue(checker.checkQueue(checker.queues.list[0]), "First queue should be valid")
        assertTrue(checker.checkQueue(checker.queues.list[1]), "Second queue should be valid")
        assertTrue(checker.checkQueue(checker.queues.list[2]), "Third queue should be valid")
        assertFalse(checker.checkQueue(checker.queues.list[3]), "Fourth queue should be invalid")
        assertFalse(checker.checkQueue(checker.queues.list[4]), "Fifth queue should be invalid")
        assertFalse(checker.checkQueue(checker.queues.list[5]), "Sixth queue should be invalid")

        assertEquals(143, checker.sumMiddleElemOfAllGoodQueues())
        assertEquals(123, checker.sumMiddleElemOfAllBadQueuesAfterReordering())
    }

    @Test
    fun `example input - checks with alternative solution`() {
        val input = """
            47|53
            97|13
            97|61
            97|47
            75|29
            61|13
            75|53
            29|13
            97|29
            53|29
            61|53
            97|53
            61|29
            47|13
            75|47
            97|75
            47|61
            75|61
            47|29
            75|13
            53|13

            75,47,61,53,29
            97,61,53,29,13
            75,29,13
            75,97,47,61,53
            61,13,29
            97,13,75,29,47
        """.trimIndent()

        val checker = PrintQueueChecker2.fromInput(input)
        assertTrue(checker.isOrdered(checker.printQueues[0]), "First queue should be valid")
        assertTrue(checker.isOrdered(checker.printQueues[1]), "Second queue should be valid")
        assertTrue(checker.isOrdered(checker.printQueues[2]), "Third queue should be valid")
        assertFalse(checker.isOrdered(checker.printQueues[3]), "Fourth queue should be invalid")
        assertFalse(checker.isOrdered(checker.printQueues[4]), "Fifth queue should be invalid")
        assertFalse(checker.isOrdered(checker.printQueues[5]), "Sixth queue should be invalid")

        assertEquals(143, checker.sumMiddleElemOfAllGoodQueues())
        assertEquals(123, checker.sumMiddleElemOfAllBadQueuesAfterReordering())
    }

    @Test
    fun `based on rules what all elements appear before 4`() {
        val input = """
            1|2
            2|3
            3|4
            10|3
            
            1,10,3,4
        """.trimIndent()
        val checker = PrintQueueChecker2.fromInput(input)
        val rParser = RulesParser(checker.rulesForQueue(checker.printQueues[0]))
        assertEquals(listOf(1, 2, 3, 10), rParser._cache[4]?.sorted())
    }

    /* Attempt 3 */
    @Test
    fun `example input - checks with alternative alternative`() {
        val input = """
            47|53
            97|13
            97|61
            97|47
            75|29
            61|13
            75|53
            29|13
            97|29
            53|29
            61|53
            97|53
            61|29
            47|13
            75|47
            97|75
            47|61
            75|61
            47|29
            75|13
            53|13

            75,47,61,53,29
            97,61,53,29,13
            75,29,13
            75,97,47,61,53
            61,13,29
            97,13,75,29,47
        """.trimIndent()

        val checker = PrintQueueChecker3.fromInput(input)
        assertTrue(checker.isOrdered(checker.printQueues[0]), "First queue should be valid")
        assertTrue(checker.isOrdered(checker.printQueues[1]), "Second queue should be valid")
        assertTrue(checker.isOrdered(checker.printQueues[2]), "Third queue should be valid")
        assertFalse(checker.isOrdered(checker.printQueues[3]), "Fourth queue should be invalid")
        assertFalse(checker.isOrdered(checker.printQueues[4]), "Fifth queue should be invalid")
        assertFalse(checker.isOrdered(checker.printQueues[5]), "Sixth queue should be invalid")

        assertEquals(listOf(97,75,47,61,53), checker.repairQueue(checker.printQueues[3]))
        assertEquals(listOf(61,29,13), checker.repairQueue(checker.printQueues[4]))
        assertEquals(listOf(97,75,47,29,13), checker.repairQueue(checker.printQueues[5]))

        assertEquals(143, checker.sumMiddleElemOfAllGoodQueues())
        assertEquals(123, checker.sumMiddleElemOfAllBadQueuesAfterReordering())
    }

    /* Attempt 4 - Floyd Warshall */
    @Test
    fun `example input - checks with alternative with Floyd-Warshall`() {
        val input = """
            47|53
            97|13
            97|61
            97|47
            75|29
            61|13
            75|53
            29|13
            97|29
            53|29
            61|53
            97|53
            61|29
            47|13
            75|47
            97|75
            47|61
            75|61
            47|29
            75|13
            53|13

            75,47,61,53,29
            97,61,53,29,13
            75,29,13
            75,97,47,61,53
            61,13,29
            97,13,75,29,47
        """.trimIndent()

        val checker = PrintQueueCheckerFloydWarshall.fromInput(input)
        assertTrue(checker.isOrdered(checker.printQueues[0]), "First queue should be valid")
        assertTrue(checker.isOrdered(checker.printQueues[1]), "Second queue should be valid")
        assertTrue(checker.isOrdered(checker.printQueues[2]), "Third queue should be valid")
        assertFalse(checker.isOrdered(checker.printQueues[3]), "Fourth queue should be invalid")
        assertFalse(checker.isOrdered(checker.printQueues[4]), "Fifth queue should be invalid")
        assertFalse(checker.isOrdered(checker.printQueues[5]), "Sixth queue should be invalid")

        assertEquals(143, checker.sumMiddleElemOfAllGoodQueues())
    }
}