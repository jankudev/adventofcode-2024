package day2.challenge

import dev.janku.katas.day2.ReactorSafetyLevelsChecker
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReactorSafetyLevelsCheckerTest {

    @Test
    fun `example safety level checks`() {
        val testRecordsSafety = listOf(
            listOf(7, 6, 4, 2, 1) to true,
            listOf(1, 2, 7, 8, 9) to false,
            listOf(9, 7, 6, 2, 1) to false,
            listOf(1, 3, 2, 4, 5) to false,
            listOf(8, 6, 4, 4, 1) to false,
            listOf(1, 3, 6, 7, 9) to true
        )

        testRecordsSafety.forEach { (record, expected) ->
            val result = ReactorSafetyLevelsChecker.isSafe(record)
            assertEquals(expected, result)
        }
    }

    @Test
    fun `safe - levels increase by 1`() {
        assertTrue(ReactorSafetyLevelsChecker.isSafe(listOf(1, 2, 3, 4, 5)));
    }

    @Test
    fun `safe - levels increase by 1-3`() {
        assertTrue(ReactorSafetyLevelsChecker.isSafe(listOf(1, 3, 6, 8, 9)))
    }

    @Test
    fun `safe - levels decrease by 1-3`() {
        assertTrue(ReactorSafetyLevelsChecker.isSafe(listOf(9, 7, 4, 2, 1)))
    }

    @Test
    fun `unsafe - same level in sequence`() {
        assertFalse(ReactorSafetyLevelsChecker.isSafe(listOf(1, 3, 5, 5, 8)))
    }

    @Test
    fun `with problem dampener - example safety level checks`() {
        val testRecordsSafety = listOf(
            listOf(7, 6, 4, 2, 1) to true,
            listOf(1, 2, 7, 8, 9) to false,
            listOf(9, 7, 6, 2, 1) to false,
            listOf(1, 3, 2, 4, 5) to true,
            listOf(8, 6, 4, 4, 1) to true,
            listOf(1, 3, 6, 7, 9) to true
        )

        testRecordsSafety.forEach { (record, expected) ->
            val result = ReactorSafetyLevelsChecker.isSafeWithDampener(record)
            try {
                assertEquals(expected, result)
            } catch (e: AssertionError) {
                println("Record: $record, expected: $expected, result: $result")
                throw e
            }
        }
    }

    @Test
    fun `with problem dampener - safe after removing 2nd element`() {
        assertTrue(ReactorSafetyLevelsChecker.isSafeWithDampener(listOf(1, 3, 2, 4, 5)))
    }

    @Test
    fun `with problem dampener - safe after removing 1st element`() {
        assertTrue(ReactorSafetyLevelsChecker.isSafeWithDampener(listOf(3, 1, 2, 4, 5)))
    }
}