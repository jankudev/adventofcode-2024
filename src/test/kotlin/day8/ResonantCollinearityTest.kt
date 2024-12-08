package day8

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResonantCollinearityTest {

    @Test
    fun `freq - identify frequency in grid`() {
        val input = """
            ...
            .A.
            ...
        """.trimIndent()
        val map = ResonantCollinearity.fromInput(input)
        val freqs : List<Char> = ResonantCollinearity.findFrequencies(map)

        assertEquals(listOf('A'), freqs)
    }

    @Test
    fun `freq - get list of all frequencies in grid`() {
        val input = """
            ...
            .A.
            a..
        """.trimIndent()
        val map = ResonantCollinearity.fromInput(input)
        val freqs : List<Char> = ResonantCollinearity.findFrequencies(map)

        assertEquals(listOf('A', 'a'), freqs.sorted())
    }

    @Test
    fun `antenna - get antenna coords distance - horizontal`() {
        val antA = Pair(18, 5)
        val antB = Pair(18, 12)
        val distance : Coords = ResonantCollinearity.getCoordsDistance(antA, antB)
        assertEquals(Pair(0, 7), distance)
    }

    @Test
    fun `antenna - get antenna coords distance - vertical`() {
        val antA = Pair(1, 5)
        val antB = Pair(8, 5)
        val distance : Coords = ResonantCollinearity.getCoordsDistance(antA, antB)
        assertEquals(Pair(7, 0), distance)
    }

    @Test
    fun `antenna - get antenna coords distance - relativity`() {
        val antA_1 = Pair(1, 1)
        val antA_2 = Pair(2, 2)
        val distance_1to2 : Coords = ResonantCollinearity.getCoordsDistance(antA_1, antA_2)
        val distance_2to1 : Coords = ResonantCollinearity.getCoordsDistance(antA_2, antA_1)

        assertEquals(Pair(1, 1), distance_1to2)
        assertEquals(Pair(-1, -1), distance_2to1)
    }

    @Test
    fun `antinode - get antenna pairs`() {
        val input = """
            .....
            .A...
            ...A.
            ..A..
            .....
        """.trimIndent()
        val map = ResonantCollinearity.fromInput(input)
        val antennaPairs : List<Pair<Coords, Coords>> = ResonantCollinearity.getAntennaPairs(map, 'A')
        assertEquals(6, antennaPairs.size)

        // A1 = (1,1), A2 = (2,3), A3 = (3,2)
        listOf(
            Pair(Pair(1,1), Pair(2,3)), Pair(Pair(2,3), Pair(1,1)),
            Pair(Pair(2,3), Pair(3,2)), Pair(Pair(3,2), Pair(2,3)),
            Pair(Pair(3,2), Pair(1,1)), Pair(Pair(1,1), Pair(3,2))
        ).forEach {
            assertTrue(antennaPairs.contains(it))
        }
    }

    @Test
    fun `antinode - identify antinodes, single antenna - none exist`() {
        val input = """
            ...
            .A.
            ...
        """.trimIndent()
        val map = ResonantCollinearity.fromInput(input)
        val antinodes = ResonantCollinearity.findAntinodes(map, 'A')
        assertTrue(antinodes.isEmpty())
    }

    @Test
    fun `antinode - identify antinodes, two antennas - none exist all out-of-bounds`() {
        val input = """
            A.
            .A
        """.trimIndent()
        val map = ResonantCollinearity.fromInput(input)
        val antinodes = ResonantCollinearity.findAntinodes(map, 'A')

        assertTrue(antinodes.isEmpty())
    }

    @Test
    fun `antinode - identify antinodes, two antennas - 1 exist 1 out-of-bounds`() {
        @Test
        fun `antinode - identify antinodes, two antennas - none exist all out-of-bounds`() {
            val input = """
            ....
            .A..
            ..A.
        """.trimIndent()
            val map = ResonantCollinearity.fromInput(input)
            val antinodes = ResonantCollinearity.findAntinodes(map, 'A')

            assertEquals(1, antinodes.size)
            assertTrue(antinodes.contains(Pair(0, 0)))
        }
    }

    @Test
    fun `antinode - identify antinodes, two antennas - 2 exist`() {
        val input = """
            ....
            .A..
            ..A.
            ....
        """.trimIndent()
        val map = ResonantCollinearity.fromInput(input)
        val antinodes = ResonantCollinearity.findAntinodes(map, 'A')

        assertEquals(2, antinodes.size)
        assertTrue(antinodes.contains(Pair(0, 0)))
        assertTrue(antinodes.contains(Pair(3, 3)))
    }

    @Test
    fun `example data (part one) - calculate antinodes`() {
        val input = """
            ............
            ........0...
            .....0......
            .......0....
            ....0.......
            ......A.....
            ............
            ............
            ........A...
            .........A..
            ............
            ............
        """.trimIndent()

        val outputAntennaMap = """
            ......#....#
            ...#....0...
            ....#0....#.
            ..#....0....
            ....0....#..
            .#....A.....
            ...#........
            #......#....
            ........A...
            .........A..
            ..........#.
            ..........#.
        """.trimIndent()

        val antennaMap = ResonantCollinearity.fromInput(input)
        val result = ResonantCollinearity.calcAntinodes(antennaMap)
        assertEquals(14, result.antinodes.size)
        assertEquals(outputAntennaMap, result.printableMap)
    }

    @Test
    fun `example data (part two) - calculate antinodes with resonance`() {
        val input = """
            T.........
            ...T......
            .T........
            ..........
            ..........
            ..........
            ..........
            ..........
            ..........
            ..........
        """.trimIndent()
        val outputAntennaMap = """
            T....#....
            ...T......
            .T....#...
            .........#
            ..#.......
            ..........
            ...#......
            ..........
            ....#.....
            ..........
        """.trimIndent()
        val antennaMap = ResonantCollinearity.fromInput(input)
        val result = ResonantCollinearity.calcAntinodes(antennaMap, false)
        val resultWithResonance = ResonantCollinearity.calcAntinodes(antennaMap, true)
        assertEquals(3, result.antinodes.size)
        assertEquals(9, resultWithResonance.antinodes.size)
        assertEquals(outputAntennaMap, resultWithResonance.printableMap)
    }

    @Test
    fun `example data (part two) - calculate antinodes with resonance - big example`() {
        val input = """
            ............
            ........0...
            .....0......
            .......0....
            ....0.......
            ......A.....
            ............
            ............
            ........A...
            .........A..
            ............
            ............
        """.trimIndent()
        val outputAntennaMap = """
            ##....#....#
            .#.#....0...
            ..#.#0....#.
            ..##...0....
            ....0....#..
            .#...#A....#
            ...#..#.....
            #....#.#....
            ..#.....A...
            ....#....A..
            .#........#.
            ...#......##
        """.trimIndent()
        val antennaMap = ResonantCollinearity.fromInput(input)
        val resultWithResonance = ResonantCollinearity.calcAntinodes(antennaMap, true)
        assertEquals(34, resultWithResonance.antinodes.size)
        assertEquals(outputAntennaMap, resultWithResonance.printableMap)
    }
}