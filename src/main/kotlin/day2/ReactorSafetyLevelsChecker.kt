package dev.janku.katas.aoc2024.day2

import dev.janku.katas.aoc2024.utils.ResourcesUtils
import kotlin.math.abs

class ReactorSafetyLevelsChecker {
    companion object {

        fun lessThan(a: Int, b: Int): Boolean = a < b
        fun greaterThan(a: Int, b: Int): Boolean = a > b

        /**
         * The records are a list of levels in the reactor, adjacent levels are transitions.
         * We'll check the transitions by pairing the adjacent levels and checking if the transition is safe.
         * - check by a comparator set by the first pair (apply the same for all the records)
         * - check if the difference between the levels is in the range 1-3
         */
        fun isSafe(levelsRecord: List<Int>): Boolean {
            val pairedSeqList = levelsRecord.subList(0, levelsRecord.size - 1)
                .zip(levelsRecord.subList(1, levelsRecord.size))

            val comparator = pairedSeqList.first().let { (a, b) ->
                if (a < b) ::lessThan else ::greaterThan
            }

            return pairedSeqList.all { (a, b) ->
                comparator(a, b) && abs(a - b) >= 1 && abs(a - b) <= 3
            }
        }

        fun countTotalSafe(levelRecords: List<List<Int>>): Int {
            return levelRecords.count { isSafe(it) }
        }

        fun readInputFile(fileName: String): List<List<Int>> {
            return ResourcesUtils.getResourceAsLinesStream(fileName).use { stream ->
                stream.map { line ->
                    line.trim().split("\\s+".toRegex()).map { it.toInt() }
                }.toList()
            }
        }

        /**
         * Tolerates single error - try to find 1st error, remove from sequence and try again.
         *
         * Can't figure out a bulletproof simple heuristic to localize the error,
         * so we'll try all the possibilities (removing a single element and check).
         */
        fun isSafeWithDampener(levelsRecord: List<Int>): Boolean {
            // guard clause - if the record is already safe
            if (isSafe(levelsRecord)) {
                return true
            }

            return levelsRecord.indices.any { i ->
                val recordWithoutError = levelsRecord.toMutableList().apply { removeAt(i) }
                isSafe(recordWithoutError)
            }
        }

        fun countTotalSafeWithDampener(levelRecords: List<List<Int>>): Int {
            return levelRecords.count { isSafeWithDampener(it) }
        }
    }
}

fun main() {
    val levelRecords = ReactorSafetyLevelsChecker.readInputFile("day2-challenge-input.txt")
    val totalSafe = ReactorSafetyLevelsChecker.countTotalSafe(levelRecords)
    val totalSafeWithDampener = ReactorSafetyLevelsChecker.countTotalSafeWithDampener(levelRecords)
    println("Total safe: $totalSafe")
    println("Total safe with reactor dampener: $totalSafeWithDampener")

}