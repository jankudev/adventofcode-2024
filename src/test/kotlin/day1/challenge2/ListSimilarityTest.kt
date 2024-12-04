package dev.janku.katas.day1.challenge2

import dev.janku.katas.day1.challenge2.ListSimilarity
import kotlin.test.Test
import kotlin.test.assertEquals

class ListSimilarityTest {
    @Test
    fun `similarity of two lists`() {
        val list1 = listOf(3, 4, 2, 1, 3, 3)
        val list2 = listOf(4, 3, 5, 3, 9, 3)

        assertEquals(31, ListSimilarity.totalSimilarity(list1, list2))
    }
}