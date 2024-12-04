package dev.janku.katas.day4

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

    private fun getDiagonalWordsAt(word: String, coords: Coordinates) : Pair<String, String> {
        // guard if the word with the center at coords is not possible
        if (word.isEmpty() ||
            coords.row - word.length/2 + 1 < 0 || coords.row + word.length/2 > height ||
            coords.column - word.length/2 + 1 < 0 || coords.column + word.length/2 > width) {
            return Pair("", "")
        }

        val diagonal1 = (0 until word.length).map { i -> matrix[coords.row - word.length/2 + i][coords.column - word.length/2 + i] }.joinToString("")
        val diagonal2 = (0 until word.length).map { i -> matrix[coords.row + word.length/2 - i][coords.column - word.length/2 + i] }.joinToString("")

        return Pair(diagonal1, diagonal2)
    }

    private lateinit var wordBidirectionalPatternCache : Regex
    /**
     * Returns a regex pattern that matches the word and its reverse
     */
    private fun getWordBidirectionalPatter(word: String) : Regex {
        if (! ::wordBidirectionalPatternCache.isInitialized) {
            wordBidirectionalPatternCache = Regex("(${word}|${word.reversed()})")
        }
        return wordBidirectionalPatternCache
    }

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
            else -> directionalCheckers.count { it(word, coords) }
        }
    }

    fun countCrossesOfWordsAt(word: String, coords: Coordinates) : Int {
        // guard
        if (word.isEmpty() || coords.row < 0 || coords.row >= height || coords.column < 0 || coords.column >= width) {
            return 0
        }
        // count true checks (a check for each direction)
        return when (word.length) {
            1 -> matrix[coords.row][coords.column].let { if (it == word[0]) 1 else 0 }
            else -> {
                val matchPattern = getWordBidirectionalPatter(word)
                val (diagonal1, diagonal2) = getDiagonalWordsAt(word, coords)
                return if (matchPattern.matches((diagonal1)) && matchPattern.matches(diagonal2)) 1 else 0
            }
        }
    }
}

class WordSearcher {
    companion object {
        fun countWord(input: String, word: String): Int {
            val matrixWords = MatrixWords.fromString(input)
            return (0..matrixWords.height - 1).flatMap {
                i -> (0..matrixWords.width - 1).map { j -> i to j }
            }.map {
                (i, j) -> matrixWords.countWordsAt(word, Coordinates(i, j))
            }.sum()
        }

        fun countCrossesOfWords(input: String, word: String): Int {
            //guard - to form a cross the word must be odd characters to have a middle one
            if (word.length % 2 == 0) {
                throw IllegalArgumentException("The supplied word '${word}' must have an odd number of characters")
            }

            // heuristically start from the middle of the word with the rest as boundary window
            // - find the center of the word
            // - extract 2 diagonal strings with the center character of length of word
            // - match the diagonal strings with the word in both directions (regular, reverse)

            val wordMiddleIdx = word.length / 2

            val matrixWords = MatrixWords.fromString(input)
            return (wordMiddleIdx..matrixWords.height - wordMiddleIdx - 1).flatMap {
                i -> (wordMiddleIdx..matrixWords.width - wordMiddleIdx - 1).map { j -> i to j }
            }.map {
                (i, j) -> matrixWords.countCrossesOfWordsAt(word, Coordinates(i, j))
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
    val searchCrossWord = "MAS"
    println("X-MAS crosses found ${WordSearcher.countCrossesOfWords(input, searchCrossWord)} times")

}