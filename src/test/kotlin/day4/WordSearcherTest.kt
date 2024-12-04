package dev.janku.katas.day4

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WordSearcherTest {
    @Test
    fun `example input - challenge provided small`() {
        val input = """
            ..X...
            .SAMX.
            .A..A.
            XMAS.S
            .X....
        """.trimIndent()

        assertEquals(4, WordSearcher.countWord(input, "XMAS"))
    }

    @Test
    fun `example input - challenge provided big`() {
        val input = """
            ....XXMAS.
            .SAMXMS...
            ...S..A...
            ..A.A.MS.X
            XMASAMX.MM
            X.....XA.A
            S.S.S.S.SS
            .A.A.A.A.A
            ..M.M.M.MM
            .X.X.XMASX
        """.trimIndent()

        assertEquals(18, WordSearcher.countWord(input, "XMAS"))
    }

    @Test
    fun `example input - challenge provided big with letters`() {
        val input = """
            MMMSXXMASM
            MSAMXMSMSA
            AMXSXMAAMM
            MSAMASMSMX
            XMASAMXAMM
            XXAMMXXAMA
            SMSMSASXSS
            SAXAMASAAA
            MAMMMXMMMM
            MXMXAXMASX
        """.trimIndent()

        assertEquals(18, WordSearcher.countWord(input, "XMAS"))
    }

    @Test
    fun `conversion of input into matrix`() {
        val input = """
            ABC
            DEF
            HIJ
        """.trimIndent()
        val expected = arrayOf(
            charArrayOf('A', 'B', 'C'),
            charArrayOf('D', 'E', 'F'),
            charArrayOf('H', 'I', 'J')
        )

        val result = MatrixWords.fromString(input).matrix

        (0..2).zip(0..2).forEach { (i, j) ->
            assertEquals(expected[i][j], result[i][j])
        }
    }

    @Test
    fun `find a 1 letter word`() {
        val input = """
            ABC
            DEA
            AAA
        """.trimIndent()

        assertEquals(5, WordSearcher.countWord(input, "A"))
    }

    @Test
    fun `find words in direction - →`() {
        val input = """
            XMAXX
            XXMAS
            XXXXX
            XMASX
        """.trimIndent()

        assertEquals(2, WordSearcher.countWord(input, "XMAS"))
    }

    @Test
    fun `find words in direction - ↓`() {
        val input = """
            XXXXX
            MXXMX
            AXXAX
            SXXSM
        """.trimIndent()

        assertEquals(2, WordSearcher.countWord(input, "XMAS"))
    }

    @Test
    fun `find words in direction - ↑`() {
        val input = """
            XXXSX
            AXXAX
            MXXMX
            XXXXX
        """.trimIndent()

        assertEquals(1, WordSearcher.countWord(input, "XMAS"))
    }

    @Test
    fun `find words in direction - ←`() {
        val input = """
            XSAMX
            AMXXX
            SAMXX
            XXXMX
        """.trimIndent()

        assertEquals(2, WordSearcher.countWord(input, "XMAS"))
    }

    @Test
    fun `find words in direction - ↖`() {
        val input = """
            XSXXX
            XXAXX
            XXXMX
            XXXXX
        """.trimIndent()

        assertEquals(1, WordSearcher.countWord(input, "XMAS"))
    }

    @Test
    fun `find words in direction - ↗`() {
        val input = """
            XXXSS
            XXAAX
            XMMXX
            XXXXX
        """.trimIndent()

        assertEquals(2, WordSearcher.countWord(input, "XMAS"))
    }

    @Test
    fun `find words in direction - ↘`() {
        val input = """
            XXXXX
            XMXXX
            XXAXX
            XXXSX
        """.trimIndent()

        assertEquals(1, WordSearcher.countWord(input, "XMAS"))
    }

    @Test
    fun `find words in direction - ↙`() {
        val input = """
            XXXXX
            XXMXX
            XAXXX
            SXXXX
        """.trimIndent()

        assertEquals(1, WordSearcher.countWord(input, "XMAS"))
    }

    @Test
    fun `attempt to check for off-by-one error`() {
        val input = """
            .........................................................
            ........XMAS...............X.............................
            ............................M.........................XMA
            .............................A.......................XMAS
            ..............................S.........................X
        """.trimIndent()
        assertEquals(3, WordSearcher.countWord(input, "XMAS"))
    }
}