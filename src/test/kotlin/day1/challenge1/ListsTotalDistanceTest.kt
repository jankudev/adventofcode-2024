package dev.janku.katas.aoc2024.day1.challenge1

import dev.janku.katas.aoc2024.day1.Day1ChallengeUtils
import dev.janku.katas.aoc2024.day1.challenge1.ListsTotalDistance
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ListsTotalDistanceTest {
    @Test
    fun `example provided - single distance`() {
        // given (a,b) expecting distance c
        val testParameters = listOf(
            Triple(1,3,2),
            Triple(3,1,2),
            Triple(2,3,1),
            Triple(2,3,1),
            Triple(3,3,0)
        )

        testParameters.forEach { (a, b, c) ->
            val result = ListsTotalDistance.distance(a, b)
            assertEquals(c, result)
        }
    }

    @Test
    fun `example provided - total distance`() {
        val list1 = listOf(3, 4, 2, 1, 3, 3)
        val list2 = listOf(4, 3, 5, 3, 9, 3)

        val result = ListsTotalDistance.totalDistance(list1, list2)
        assertEquals(11, result)
    }

    @Test
    fun `read input from file into lists`() {
        val expectedList1 = listOf(77442, 71181, 49755)
        val expectedList2 = listOf(88154, 76363, 69158)

        val result : Pair<List<Int>, List<Int>> = Day1ChallengeUtils.readInputFile("day1-challenge1-input-test.txt")
        assertEquals(expectedList1, result.first)
        assertEquals(expectedList2, result.second)
    }
}