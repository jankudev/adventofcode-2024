package day10

import dev.janku.katas.aoc2024.utils.ResourcesUtils

typealias Coords = Pair<Int, Int>
typealias Point = Pair<Int, Coords>
typealias TopoMap = List<List<Int>>
typealias ReachedByPaths = List<MutableList<MutableList<Int>>>

class HoofIt {
    companion object {
        fun createTopoMap(input: String): List<List<Int>> {
            return input.split("\n")
                .map { line -> line.map { c -> c.digitToInt() } }
        }

        fun topoMapToPoints(topoMap: TopoMap): List<Point> {
            return topoMap.flatMapIndexed { rIdx, row ->
                row.mapIndexed { cIdx, cell -> cell to Coords(rIdx, cIdx) }
            }
        }

        fun findPossibleTrailheads(topoMap: TopoMap): List<Point> {
            return topoMapToPoints(topoMap).filter { (height, _) -> height == 0 }
        }

        fun findPeaks(topoMap: TopoMap): List<Point> {
            return topoMapToPoints(topoMap).filter { (height, _) -> height == 9 }
        }

        /**
         * Create a mapping of points to unique identifiers (flow IDs starting from Point)
         * - can't use hashCode on Point as it was shown that on 1000x1000 points it generates only 32000+ unique values
         */
        fun createPointIdMappings(points: Iterable<Point>): Map<Point, Int> {
            return points.mapIndexed { idx, it -> it to idx+1 }.toMap()
        }

        /**
         * Simulate the flow of water from points to it's neighbours (a single step)
         */
        fun step(n: Int, topoMap: TopoMap, points: List<Point>, pointIdMappings: Map<Point, Int>, state: ReachedByPaths) : List<Point> {
            // guard against invalid input
            if (points.any { n-1 != it.first} ) throw IllegalStateException("All points in step N must be of height N-1 as they're from previous steps")

            val nextPoints = points.flatMap { (_, coords) ->
                val (r, c) = coords
                val nextCoordsOnPath = listOf(
                    r to c-1,
                    r to c+1,
                    r-1 to c,
                    r+1 to c
                ).filter { (nr, nc) -> nr >= 0 && nr < topoMap.size && nc >= 0 && nc < topoMap[0].size && topoMap[nr][nc] == n }

                nextCoordsOnPath.forEach { (row, col) ->
                    state[row][col].addAll(state[r][c])
                    state[row][col] = state[row][col].distinct().toMutableList()
                }

                nextCoordsOnPath.map { Point(n, it) }
            }

            return nextPoints
        }

        /* part one - depth-first search like a flow */
        fun calculatePaths(topoMap: TopoMap, pointIdMappings: Map<Point, Int>, trailheads: List<Point>, peaks: List<Point>) : Int {
            val state = topoMap.map { row -> row.map { mutableListOf<Int>() }.toMutableList() }
            trailheads.forEach { (height, coords) -> state[coords.first][coords.second].add(pointIdMappings[height to coords]!!) }

            var pointsToProcess = trailheads
            (1..9).forEach { n -> pointsToProcess = step(n, topoMap, pointsToProcess, pointIdMappings, state) }

            // calc score
            return peaks.map {
                peak -> state[peak.second.first][peak.second.second].distinct().size
            }.sum()
        }

        /* part two - alternative approach with recursion */
        fun numPathsReachingPeakInSteps(n: Int, topoMap: TopoMap, pos: Coords) : Int {
            if (n == 9 && topoMap[pos.first][pos.second] == 9) return 1
            if (n == 9 && topoMap[pos.first][pos.second] != 9) return 0

            val (r, c) = pos
            return listOf(
                r to c-1,
                r to c+1,
                r-1 to c,
                r+1 to c
            ).filter { (nr, nc) -> nr >= 0 && nr < topoMap.size && nc >= 0 && nc < topoMap[0].size && topoMap[nr][nc] == n+1 }
                .map { numPathsReachingPeakInSteps(n+1, topoMap, it) }
                .sum()
        }

        fun calculatePaths2(topoMap: TopoMap, trailheads: List<Point>) : Int {
            return trailheads.map { (height, coords) -> numPathsReachingPeakInSteps(height, topoMap, coords) }.sum()
        }

    }
}

fun main() {
    val input = ResourcesUtils.getResourceAsLinesStream("day10-challenge-input.txt").reduce {
            acc, line -> "$acc\n$line"
    }.get()

    val topoMap = HoofIt.createTopoMap(input)
    val points = HoofIt.topoMapToPoints(topoMap)
    val pointIds = HoofIt.createPointIdMappings(points)

    val score = HoofIt.calculatePaths(topoMap, pointIds, HoofIt.findPossibleTrailheads(topoMap), HoofIt.findPeaks(topoMap))
    println("Score: $score")

    val score2 = HoofIt.calculatePaths2(topoMap, HoofIt.findPossibleTrailheads(topoMap))
    println("Score (ranked): $score2")
}