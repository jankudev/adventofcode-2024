package dev.janku.katas.aoc2024.day1.challenge1

import dev.janku.katas.aoc2024.day1.Day1ChallengeUtils
import kotlin.math.abs

class ListsTotalDistance {

    companion object Utils {
        fun distance(a: Int, b: Int): Int {
            return abs(a - b)
        }

        fun totalDistance(list1: List<Int>, list2: List<Int>): Int {
            val sortedList1 = list1.sorted()
            val sortedList2 = list2.sorted()
            return sortedList1.zip(sortedList2).map { (a, b) -> distance(a, b) }.sum()
        }
    }
}

fun main() {
    val (list1, list2) = Day1ChallengeUtils.readInputFile("day1-challenge1-input.txt")
    val result = ListsTotalDistance.totalDistance(list1, list2)
    println("Total distance: $result")
}