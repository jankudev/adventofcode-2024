package day12

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GardenGroupsTest {

    @Test
    fun `tdd - single garden`() {
        val input = """
            A
        """.trimIndent()
        assertEquals(4, GardenGroups.fencePrice(input))
    }

    @Test
    fun `tdd - 2 single gardens`() {
        val input = """
            AB
        """.trimIndent()
        assertEquals(8, GardenGroups.fencePrice(input))
    }

    @Test
    fun `tdd - 2x 2 single cross-gardens`() {
        val input = """
            AB
            BA
        """.trimIndent()
        assertEquals(16, GardenGroups.fencePrice(input))
    }

    @Test
    fun `tdd - 2x 2 square gardens`() {
        val input = """
            AA
            BB
        """.trimIndent()
        assertEquals(24, GardenGroups.fencePrice(input))
    }


    @Test
    fun `tdd - 2x 1-square, 1x 1-square gardens`() {
        val input = """
            AA
            BC
        """.trimIndent()
        assertEquals(12+4+4, GardenGroups.fencePrice(input))
    }

    @Test
    fun `tdd - 3x3 - single gardens`() {
        val input = """
            AAA
            AAA
            AAA
        """.trimIndent()
        assertEquals(9*4*3, GardenGroups.fencePrice(input))
    }

    @Test
    fun `tdd - 3x 3-square gardens`() {
        val input = """
            AAA
            BBB
            CCC
        """.trimIndent()
        assertEquals(72, GardenGroups.fencePrice(input))
    }

    @Test
    fun `tdd - in the middle garden`() {
        val input = """
            AAA
            ABA
            AAA
        """.trimIndent()
        assertEquals(8*16 + 4, GardenGroups.fencePrice(input))
    }

    @Test
    fun `example data - adjacent gardens`() {
        val input = """
        AAAA
        BBCD
        BBCC
        EEEC
    """.trimIndent()
        assertEquals(140, GardenGroups.fencePrice(input))
    }

    @Test
    fun `example data - embedded gardens`() {
        val input = """
            OOOOO
            OXOXO
            OOOOO
            OXOXO
            OOOOO
        """.trimIndent()
        assertEquals(772, GardenGroups.fencePrice(input))
    }

    @Test
    fun `example data - larger example`() {
        val input = """
            RRRRIICCFF
            RRRRIICCCF
            VVRRRCCFFF
            VVRCCCJFFF
            VVVVCJJCFE
            VVIVCCJJEE
            VVIIICJJEE
            MIIIIIJJEE
            MIIISIJEEE
            MMMISSJEEE
        """.trimIndent()
        assertEquals(1930, GardenGroups.fencePrice(input))
    }

    @Test
    fun `example data - part2 example 1`() {
        val input = """
            AAAA
            BBCD
            BBCC
            EEEC
        """.trimIndent()
        assertEquals(80, GardenGroups.fencePrice(input, true))
    }

    @Test
    fun `example data - part2 example 2`() {
        val input = """
            EEEEE
            EXXXX
            EEEEE
            EXXXX
            EEEEE
        """.trimIndent()
        assertEquals(236, GardenGroups.fencePrice(input, true))
    }

    @Test
    fun `example data - part2 example 3`() {
        val input = """
            AAAAAA
            AAABBA
            AAABBA
            ABBAAA
            ABBAAA
            AAAAAA
        """.trimIndent()
        assertEquals(368, GardenGroups.fencePrice(input, true))
    }
}