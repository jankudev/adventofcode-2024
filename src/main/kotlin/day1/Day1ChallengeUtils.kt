package dev.janku.katas.aoc2024.day1

import dev.janku.katas.aoc2024.utils.ResourcesUtils

class Day1ChallengeUtils {
    companion object {
        fun readInputFile(fileName: String): Pair<List<Int>, List<Int>> {
            return ResourcesUtils.getResourceAsLinesStream(fileName).use {
                    stream -> stream.map {
                    line -> line.trim().split("\\s+".toRegex())
            }.filter {
                it.size == 2
            }.map {
                    (first, second) -> first.toInt() to second.toInt()
            }.toList().unzip()
            }
        }
    }
}