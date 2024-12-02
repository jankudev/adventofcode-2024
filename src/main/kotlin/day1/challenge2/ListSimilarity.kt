package dev.janku.katas.day1.challenge2

import dev.janku.katas.day1.Day1ChallengeUtils

class ListSimilarity {
    companion object {
        fun totalSimilarity(list1: List<Int>, list2: List<Int>): Int {

            val sortedList2 = list2.sorted()
            val countsInList2 = sortedList2.groupingBy { it }.eachCount()

            return list1.sumOf {
                countsInList2[it]?.let { count -> count * it } ?: 0
            }
        }
    }
}

fun main() {
    val (list1, list2) = Day1ChallengeUtils.readInputFile("day1-challenge1-input.txt")
    val result = ListSimilarity.totalSimilarity(list1, list2)
    println("Total similarity: $result")
}