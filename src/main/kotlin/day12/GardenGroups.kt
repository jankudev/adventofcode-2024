package day12

import dev.janku.katas.aoc2024.utils.ResourcesUtils

typealias Map<T> = List<List<T>>
typealias MutableMap<T> = MutableList<MutableList<T>>
typealias Coords = Pair<Int, Int>

enum class DIR(val mod: Coords) {
    TOP_LEFT(-1 to -1),
    TOP(-1 to 0),
    TOP_RIGHT(-1 to 1),
    RIGHT(0 to 1),
    BOTTOM_RIGHT(1 to 1),
    BOTTOM(1 to 0),
    BOTTOM_LEFT(1 to -1),
    LEFT(0 to -1)
}


class GardenGroups {
    companion object {
        // left, top, right, bottom
        private val neighbourMods = listOf(DIR.LEFT.mod, DIR.TOP.mod, DIR.RIGHT.mod, DIR.BOTTOM.mod)
        // top-left, top-right, bottom-right, bottom-left
        private val diagonalMods = listOf(DIR.TOP_LEFT.mod, DIR.TOP_RIGHT.mod, DIR.BOTTOM_RIGHT.mod, DIR.BOTTOM_LEFT.mod)

        private fun createMapFromInput(input: String): Map<Char> {
            return input.split("\n").map { it.toList() }
        }

        private fun areSame(map: Map<Char>, a: Pair<Int, Int>, b: Pair<Int, Int>): Boolean {
            return a.first >= 0 && a.first < map.size && a.second >= 0 && a.second < map[0].size
                    && b.first >= 0 && b.first < map.size && b.second >= 0 && b.second < map[0].size
                    && map[a.first][a.second] == map[b.first][b.second]
        }

        private fun areDifferent(map: Map<Char>, a: Pair<Int, Int>, b: Pair<Int, Int>): Boolean {
            return !areSame(map, a, b)
        }

        /**
         * Count fences for a square as the number of different neighbours
         */
        private fun countRequiredFencesForSquare(map: Map<Char>, coords: Coords): Int {
            val (x, y) = coords
            return neighbourMods.map { (xMod, yMod) ->
                areDifferent(map, x to y, x + xMod to y + yMod)
            }.count { it }
        }

        /**
         * Count the edges this square adds to the area by the mapping by counting corners
         * - outer corners
         * - inner corners
         */
        private fun countEdgesForSquare(map: Map<Char>, coords: Coords): Int {
            val (x, y) = coords
            val nDiff = neighbourMods.map { (xMod, yMod) ->
                areDifferent(map, x to y, x + xMod to y + yMod)
            }
            val dDiff = diagonalMods.map { (xMod, yMod) ->
                areDifferent(map, x to y, x + xMod to y + yMod)
            }

            var cornerCounter = 0
            if (dDiff[0] && ((nDiff[0] && nDiff[1]) || !(nDiff[0] || nDiff[1]))) cornerCounter++
            if (dDiff[1] && ((nDiff[1] && nDiff[2]) || !(nDiff[1] || nDiff[2]))) cornerCounter++
            if (dDiff[2] && ((nDiff[2] && nDiff[3]) || !(nDiff[2] || nDiff[3]))) cornerCounter++
            if (dDiff[3] && ((nDiff[3] && nDiff[0]) || !(nDiff[3] || nDiff[0]))) cornerCounter++

            return cornerCounter
        }

        /* Next attempt */
        fun fencePrice(input: String, useEdgesForPrice: Boolean = false): Int {
            val map = createMapFromInput(input)

            // transform map into one identifying components by ID - to distinguish between different gardens with same symbol

            // identify fences - map of fences for each square (count or edge type)
            val fences: Map<Int> = when {
                useEdgesForPrice -> countEdges(map)
                else -> countFences(map)
            }

            // parse and find components
            val processedMap: MutableMap<Boolean> = map.mapIndexed { x, row ->
                row.mapIndexed { y, _ -> false }.toMutableList()
            }.toMutableList()

            return processedMap.flatMapIndexed { x, row ->
                row.mapIndexed { y, processed ->
                    when (processed) {
                        true -> null
                        false -> countPriceByFenceCount(findComponent(map, processedMap, Coords(x, y)), fences)
                    }
                }
            }.filterNotNull().sum()
        }

        private fun countFences(map: Map<Char>) = map.mapIndexed { x, row ->
            row.mapIndexed { y, _ -> countRequiredFencesForSquare(map, x to y) }
        }

        private fun countEdges(map: Map<Char>) = map.mapIndexed { x, row ->
            row.mapIndexed { y, _ -> countEdgesForSquare(map, x to y) }
        }

        private fun findComponent(map: Map<Char>, processedMap: MutableMap<Boolean>, coords: Coords): List<Coords> {
            // guard clause for recursion
            val (x, y) = coords
            if (x < 0 || x >= map.size || y < 0 || y >= map[0].size || processedMap[x][y]) return emptyList()

            processedMap[x][y] = true

            val component: MutableList<Coords> = mutableListOf(coords)
            neighbourMods.forEach { (xMod, yMod) ->
                if (areSame(map, coords, (x + xMod) to (y + yMod)))
                    component.addAll(findComponent(map, processedMap, (x + xMod) to (y + yMod)))
            }

            return component
        }

        private fun countPriceByFenceCount(gardenCoords: List<Coords>, fences: Map<Int>): Int {
            return gardenCoords.map { (x, y) -> fences[x][y] }.sum() * gardenCoords.size
        }
    }
}

fun main() {
    val input = ResourcesUtils.getResourceAsLinesStream("day12-challenge-input.txt").reduce {
            acc, line -> "$acc\n$line"
    }.get()
    println("Total fence price: ${GardenGroups.fencePrice(input)}")
    println("Total fence price (by edges): ${GardenGroups.fencePrice(input, useEdgesForPrice = true)}")
}