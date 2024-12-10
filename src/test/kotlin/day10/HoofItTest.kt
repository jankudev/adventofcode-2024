package day10

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

/**
 * Determining a path on a topological map (map of heights 0-9) with the following rules:
 * - starts at 0, ends at 9
 * - movement in 4 basic directions only ↑↓←→
 * - path step of size 1
 * - path always goes from lower to higher height (0 -> 1 -> 2 -> ... -> 9)
 *
 * Trailhead - every position of height 0
 * Peak - every position of height 9
 *
 * Trailhead score - number of peaks distinct reachable from the trailhead by a path
 *
 * Approach ideas:
 * - each path has at most 9 steps (choose 'depth-first search' over recursion)
 * - simulate the paths as flows from a source (trailhead) to a sing (peak)
 * - coords of trailheads as flow identifiers (we're not interested in the path itself, but the reachable peaks)
 */
class HoofItTest {

    @Test
    fun `create topo map`() {
        val input = """
            012
            543
            678
        """.trimIndent()

        val topoMap = HoofIt.createTopoMap(input)
        assertEquals(listOf(
            listOf(0, 1, 2),
            listOf(5, 4, 3),
            listOf(6, 7, 8)
        ), topoMap)

    }

    @Test
    fun `find all possible trailheads`() {
        val input = """
            012
            509
            970
        """.trimIndent()

        val topoMap = HoofIt.createTopoMap(input)
        val possibleTrailheads = HoofIt.findPossibleTrailheads(topoMap)

        assertEquals(3, possibleTrailheads.size)
        assertContentEquals(listOf(0 to Coords(0, 0), 0 to Coords(1, 1), 0 to Coords(2, 2)), possibleTrailheads)
    }

    @Test
    fun `find all peaks`() {
        val input = """
            012
            509
            970
        """.trimIndent()

        val topoMap = HoofIt.createTopoMap(input)
        val peaks = HoofIt.findPeaks(topoMap)

        assertEquals(2, peaks.size)
        assertContentEquals(listOf(9 to Coords(1, 2), 9 to Coords(2, 0)), peaks)
    }


    @Test
    fun `unique flow identifier from trailhead coords`() {
        val randomMap = (0 until 1000).map {
            (0 until 1000).map {
                (0..9).random()
            }.joinToString("")
        }.joinToString("\n")

        val topoMap = HoofIt.createTopoMap(randomMap)
        val points = HoofIt.topoMapToPoints(topoMap)
        val pointIds = HoofIt.createPointIdMappings(points)

        assertEquals(points.size, pointIds.values.toSet().size)
    }

    @Test
    fun `example - with 1 trailhead and 1 reachable peak (by multiple paths)`() {
        val input = """
            0123
            1234
            8765
            9876
        """.trimIndent()

        val topoMap = HoofIt.createTopoMap(input)
        val points = HoofIt.topoMapToPoints(topoMap)
        val pointIds = HoofIt.createPointIdMappings(points)

        assertEquals(1, HoofIt.calculatePaths(topoMap, pointIds, HoofIt.findPossibleTrailheads(topoMap), HoofIt.findPeaks(topoMap)))

    }

    @Test
    fun `example - advanced with 9 trailheads with total score 36`() {
        val input = """
            89010123
            78121874
            87430965
            96549874
            45678903
            32019012
            01329801
            10456732
        """.trimIndent()

        val topoMap = HoofIt.createTopoMap(input)
        val points = HoofIt.topoMapToPoints(topoMap)
        val pointIds = HoofIt.createPointIdMappings(points)

        // scores 5,6,5,3,1,3,5,3 = total score 36
        assertEquals(36, HoofIt.calculatePaths(topoMap, pointIds, HoofIt.findPossibleTrailheads(topoMap), HoofIt.findPeaks(topoMap)))
    }

    @Test
    fun `part two - rating paths by num of distinct paths`() {
        val topoMap = """
            .....0.
            ..4321.
            ..5..2.
            ..6543.
            ..7..4.
            ..8765.
            ..9....
        """.trimIndent()
            .testingTopoMapPrepare()

        val points = HoofIt.topoMapToPoints(topoMap)

        // scores 5,6,5,3,1,3,5,3 = total score 36
        assertEquals(3, HoofIt.calculatePaths2(topoMap,HoofIt.findPossibleTrailheads(topoMap)))

    }

    /**
     * Modifies the testing topological map with placeholders to be ignored '.' by converting them to -1
     */
    private fun String.testingTopoMapPrepare() : TopoMap {
        return this.split("\n")
            .map { line -> line.map { c -> if (c == '.') -1 else c.digitToInt() } }
    }
}