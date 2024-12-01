package dev.janku.katas.day1

import dev.janku.katas.utils.ResourcesUtils

class CommonCompanion {
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