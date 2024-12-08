package day8

import dev.janku.katas.aoc2024.utils.ResourcesUtils

typealias Coords = Pair<Int, Int>
typealias Map = List<List<Char>>

const val EMPTY_MARK = '.'
const val ANTINODE_MARK = '#'

data class AntinodesResult (
    val antennaMap: Map,
    val antinodes: List<Coords>
) {
    val printableMap: String

    init {
        printableMap = antennaMap.mapIndexed { i, row ->
            row.mapIndexed { j, cell ->
                if (cell == '.' && antinodes.contains(Pair(i, j))) ANTINODE_MARK else cell
            }.joinToString("")
        }.joinToString("\n")
    }
}

class ResonantCollinearity {

    companion object {
        fun fromInput(input: String): Map {
            return input.split("\n").map { it.toList() }
        }

        /**
         * Find all frequencies in the map
         */
        fun findFrequencies(map: List<List<Char>>): List<Char> {
            return map.flatMap { it }.filter { it != EMPTY_MARK }.distinct()
        }

        /**
         * Coords distance between two antennas
         */
        fun getCoordsDistance(from: Coords, to: Coords): Coords {
            return Pair(to.first - from.first, to.second - from.second)
        }

        /**
         * Directed pairs A->B between same frequency antennas
         */
        fun getAntennaPairs(map: Map, freq: Char): List<Pair<Coords, Coords>> {
            val freqCoords = map.mapIndexed { i, row ->
                row.mapIndexed { j, cell ->
                    if (cell == freq) Pair(i, j) else null
                }
            }.flatten().filterNotNull()

            return freqCoords.flatMap { x ->
                freqCoords.map { y -> x to y }
            }.filter { (x, y) -> x != y }
        }

        /**
         * Between two antennas find the antinode A->B->#
         */
        fun findAntinode(antFrom: Coords, antTo: Coords) : Coords {
            val distance = getCoordsDistance(antFrom, antTo)
            return Pair(antTo.first + distance.first, antTo.second + distance.second)
        }

        /**
         * Between two antennas find the antinode A->B-># with resonance (sequence until end of map)
         * - count/include the antenas as antinodes as they're also in line
         */
        fun findAntinodeWithResonance(map: Map, antFrom: Coords, antTo: Coords) : List<Coords> {
            val distance = getCoordsDistance(antFrom, antTo)
            val antinodes = mutableListOf(antFrom, antTo)

            var step = 1
            var antinode = Pair(antTo.first + distance.first, antTo.second + distance.second)
            while (coordsInMap(map, antinode)) {
                antinodes.add(antinode)
                step += 1
                antinode = Pair(antTo.first + (step * distance.first), antTo.second + (step * distance.second))
            }
            return antinodes
        }

        /**
         * Check if the given coords are in the map bounds
         */
        fun coordsInMap(map: Map, coords: Coords): Boolean {
            return coords.first >= 0 && coords.second >= 0 && coords.first < map.size && coords.second < map[0].size
        }

        /**
         * Find all antinodes for the given frequency
         */
        fun findAntinodes(map: Map, freq: Char): List<Coords> {
            return getAntennaPairs(map, freq)
                .map { (antFrom, antTo) -> findAntinode(antFrom, antTo) }
                .filter { coordsInMap(map, it) }
        }

        /**
         * Find all antinodes for the given frequency with resonance, also account the antena itself
         */
        fun findAntinodesWithResonance(map: Map, freq: Char): List<Coords> {
            return getAntennaPairs(map, freq)
                .flatMap { (antFrom, antTo) -> findAntinodeWithResonance(map, antFrom, antTo) }
        }

        /**
         * Calculate the antinodes for the whole map
         */
        fun calcAntinodes(antennaMap: Map, withResonance: Boolean = false): AntinodesResult {
            val frequencies = findFrequencies(antennaMap)
            val antinodes = frequencies.flatMap {
                when (withResonance) {
                    false -> findAntinodes(antennaMap, it)
                    true  -> findAntinodesWithResonance(antennaMap, it)
                }
            }.distinct()
            return AntinodesResult(antennaMap, antinodes)
        }
    }
}

fun main() {
    val input = ResourcesUtils.getResourceAsLinesStream("day8-challenge-input.txt").reduce {
            acc, line -> "$acc\n$line"
    }.get()

    val map = ResonantCollinearity.fromInput(input)
    val result = ResonantCollinearity.calcAntinodes(map, withResonance = true)
    println("Total antinodes: ${result.antinodes.size}")
    println(result.printableMap)
}