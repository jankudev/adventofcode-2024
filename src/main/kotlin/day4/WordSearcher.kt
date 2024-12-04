package dev.janku.katas.day4

import dev.janku.katas.day3.CorruptedMemoryMultiplier
import dev.janku.katas.utils.ResourcesUtils

data class Coordinates(val row: Int, val column: Int)

class MatrixWords (val matrix: Array<CharArray>, val height: Int, val width: Int) {

    companion object {
        /**
         * Converts the input string to a matrix of characters
         * - not prematurely optimizing by representation in single array with modular arithmetic
         *   (for simplicity)
         */
        private fun convertInputToMatrix(input: String): Array<CharArray> {
            return input.split("\n")
                .map { it.toCharArray() }
                .toTypedArray()
        }

        /**
         * Factory method to create the MatrixWords from the input string
         */
        fun fromString(input: String): MatrixWords {
            val matrix = convertInputToMatrix(input)
            return MatrixWords(matrix, matrix.size, matrix[0].size)
        }
    }

    // ↖
    private fun checkTopLeft(word: String, coords: Coordinates) : Boolean {
        //guard
        if (coords.row - word.length + 1 < 0 || coords.column - word.length + 1 < 0) {
            return false
        }
        return (0 until word.length).all { i -> matrix[coords.row - i][coords.column - i] == word[i] }
    }

    // ↑
    private fun checkTop(word: String, coords: Coordinates) : Boolean {
        // guard
        if (coords.row - word.length + 1 < 0) {
            return false
        }
        return (0 until word.length).all { i -> matrix[coords.row - i][coords.column] == word[i] }
    }

    // ↗
    private fun checkTopRight(word: String, coords: Coordinates) : Boolean {
        // guard
        if (coords.row - word.length + 1 < 0 || coords.column + word.length > width) {
            return false
        }
        return (0 until word.length).all { i -> matrix[coords.row - i][coords.column + i] == word[i] }
    }

    // →
    private fun checkRight(word: String, coords: Coordinates) : Boolean {
        //guard
        if (coords.column + word.length > width) {
            return false
        }
        return (0 until word.length).all { i -> matrix[coords.row][coords.column + i] == word[i] }
    }

    // ↘
    private fun checkBottomRight(word: String, coords: Coordinates) : Boolean {
        // guard
        if (coords.row + word.length > height || coords.column + word.length > width) {
            return false
        }
        return (0 until word.length).all { i -> matrix[coords.row + i][coords.column + i] == word[i] }
    }

    // ↓
    private fun checkBottom(word: String, coords: Coordinates) : Boolean {
        //guard
        if (coords.row + word.length > height) {
            return false
        }
        return (0 until word.length).all { i -> matrix[coords.row + i][coords.column] == word[i] }
    }

    // ↙
    private fun checkBottomLeft(word: String, coords: Coordinates) : Boolean {
        // guard
        if (coords.row + word.length > height || coords.column - word.length + 1 < 0) {
            return false
        }
        return (0 until word.length).all { i -> matrix[coords.row + i][coords.column - i] == word[i] }
    }

    // ←
    private fun checkLeft(word: String, coords: Coordinates) : Boolean {
        //guard
        if (coords.column - word.length + 1 < 0) {
            return false
        }
        return (0 until word.length).all { i -> matrix[coords.row][coords.column - i] == word[i] }
    }

    /**
     * All directional word checking functions in the order of the compass
     */
    private val directionalCheckers = listOf(
        ::checkTopLeft,
        ::checkTop,
        ::checkTopRight,
        ::checkRight,
        ::checkBottomRight,
        ::checkBottom,
        ::checkBottomLeft,
        ::checkLeft
    )

    /**
     * Count of the word in all directions from the given coordinates
     */
    fun countWordsAt(word: String, coords: Coordinates) : Int {
        // guard
        if (word.isEmpty() || coords.row < 0 || coords.row >= height || coords.column < 0 || coords.column >= width) {
            return 0
        }
        // count true checks (a check for each direction)
        return when (word.length) {
            1 -> matrix[coords.row][coords.column].let { if (it == word[0]) 1 else 0 }
            else -> directionalCheckers.map { it(word, coords) }.count { it }
        }
    }
}

class WordSearcher {
    companion object {
        fun countWord(input: String, word: String): Int {
            val matrixWords = MatrixWords.fromString(input)
            return (0..matrixWords.height).flatMap {
                i -> (0..matrixWords.width).map { j -> i to j }
            }.map {
                (i, j) -> matrixWords.countWordsAt(word, Coordinates(i, j))
            }.sum()
        }
    }
}

fun main() {
    val input = ResourcesUtils.getResourceAsLinesStream("day4-challenge-input.txt").reduce {
            acc, line -> "$acc\n$line"
    }.get()
    val searchWord = "XMAS"
    println("XMAS found ${WordSearcher.countWord(input, searchWord)} times")

}