package day15

import dev.janku.katas.aoc2024.utils.ResourcesUtils

data class Coords(val x: Int, val y: Int)
data class DxCoords(val dx: Int, val dy: Int)

enum class MAP_SYMBOLS(val char: Char) {
    WALL('#'),
    EMPTY('.'),
    BOX('O'),
    ROBOT('@')
}

enum class MOVE(val char: Char, val mod: DxCoords) {
    UP('^', DxCoords(0, -1)),
    DOWN('v', DxCoords(0, 1)),
    LEFT('<', DxCoords(-1, 0)),
    RIGHT('>', DxCoords(1, 0))
}

/**
 * Represents the warehouse with the map of symbols
 * (hint: extends List<List<MAP_SYMBOLS>> with delegation - a neat Kotlin extension functionality)
 */
class Warehouse(private val data: List<List<MAP_SYMBOLS>>) : List<List<MAP_SYMBOLS>> by data {
    fun width() = this.data[0].size
    fun height() = this.data.size

    override fun toString() : String {
        return this.joinToString("\n") { row -> row.joinToString("") { it.char.toString() } }
    }
}

data class State(val warehouse: Warehouse, val moves: List<MOVE>) {
    override fun toString() : String {
        return "${warehouse.toString()}\n\n${moves.joinToString("") { it.char.toString() }}"
    }
}

val NO_EMPTY_TILE = Coords(-1, -1)

class WarehouseWoes {
    companion object {
        /**
         * Create the representation of the Warehouse (initial state) from a string input
         */
        fun readWarehouseFromInput(input: String) : Warehouse {
            return Warehouse(input.split("\n").map { line ->
                line.map { char ->
                    MAP_SYMBOLS.entries.find { it.char == char }
                        ?: throw IllegalArgumentException("Unknown char: $char")
                }
            })
        }

        /**
         * Create the representation of moves sequence (initial state) from a string input
         */
        fun readMovesFromInput(input: String) : List<MOVE> {
            return input.replace("\n","").map { char ->
                MOVE.entries.find { it.char == char } ?: throw IllegalArgumentException("Unknown char: $char")
            }
        }

        /**
         * Create the initial state
         */
        fun initialStateFromInput(input: String) : State {
            val (warehouse, moves) = input.split("\n\n")
            return State(readWarehouseFromInput(warehouse), readMovesFromInput(moves))
        }

        /**
         * Moving to the next state of the warehouse and the moves by applying the first move on stack
         */
        fun nextState(state: State): State {
            return State(
                moveRobotInWarehouse(state.warehouse, state.moves.first()),
                state.moves.subList(1, state.moves.size)
            )
        }

        /**
         * Perform a single move of the robot in the warehouse
         */
        private fun moveRobotInWarehouse(warehouse: Warehouse, move: MOVE): Warehouse {
            val newWarehouse = warehouse.toMutableList().map { it.toMutableList() }
            val robotCoords = findRobot(warehouse)

            val newRobotCoords = Coords(robotCoords.x + move.mod.dx, robotCoords.y + move.mod.dy)

            return when (warehouse[newRobotCoords.y][newRobotCoords.x]) {
                // movement blocked, move forfeited
                MAP_SYMBOLS.WALL -> warehouse

                // movement pushing boxes if possible
                MAP_SYMBOLS.BOX -> moveShiftingBoxes(warehouse, move, robotCoords, newRobotCoords)

                // movement to an empty tile
                MAP_SYMBOLS.EMPTY -> {
                    newWarehouse[robotCoords.y][robotCoords.x] = MAP_SYMBOLS.EMPTY
                    newWarehouse[newRobotCoords.y][newRobotCoords.x] = MAP_SYMBOLS.ROBOT
                    return Warehouse(newWarehouse)
                }

                // guard against a state that can't happen
                MAP_SYMBOLS.ROBOT -> throw IllegalStateException("There is only a single robot so it can't be on the next tile!")
            }
        }

        private fun moveShiftingBoxes(warehouse: Warehouse, move: MOVE, robotCoords: Coords, newRobotCoords: Coords): Warehouse {
            val newWarehouse = warehouse.toMutableList().map { it.toMutableList() }
            val firstEmptyTileInDirection = findFirstEmptyTileInDirection(warehouse, newRobotCoords, move)

            return when (firstEmptyTileInDirection) {
                NO_EMPTY_TILE -> warehouse
                else -> {
                    newWarehouse[robotCoords.y][robotCoords.x] = MAP_SYMBOLS.EMPTY
                    newWarehouse[firstEmptyTileInDirection.y][firstEmptyTileInDirection.x] = MAP_SYMBOLS.BOX
                    newWarehouse[newRobotCoords.y][newRobotCoords.x] = MAP_SYMBOLS.ROBOT
                    Warehouse(newWarehouse)
                }
            }
        }

        private fun findFirstEmptyTileInDirection(warehouse: Warehouse, newRobotCoords: Coords, move: MOVE): Coords {
            var currentCoords = newRobotCoords
            while (warehouse[currentCoords.y][currentCoords.x] != MAP_SYMBOLS.WALL) {
                if (warehouse[currentCoords.y][currentCoords.x] == MAP_SYMBOLS.EMPTY) return currentCoords
                currentCoords = Coords(currentCoords.x + move.mod.dx, currentCoords.y + move.mod.dy)
            }
            return NO_EMPTY_TILE
        }

        private fun findRobot(warehouse: Warehouse): Coords {
            warehouse.forEachIndexed { y, row ->
                row.forEachIndexed { x, symbol ->
                    if (symbol == MAP_SYMBOLS.ROBOT) return Coords(x, y)
                }
            }
            throw IllegalStateException("Robot not found in the warehouse")
        }

        /**
         * Coords are 100x distance from top + distance from left
         */
        private fun coordsToGPS(coords: Coords): Long {
            return coords.y.toLong() * 100 + coords.x
        }

        /**
         * Sum of all GPS coordinates of boxes in the warehouse
         */
        fun sumOfAllGPS(state: State): Long {
            return state.warehouse.foldIndexed(0L) { y, acc, row ->
                acc + row.foldIndexed(0L) { x, accRow, symbol ->
                    accRow + when (symbol) {
                        MAP_SYMBOLS.BOX -> coordsToGPS(Coords(x, y))
                        else -> 0
                    }
                }
            }
        }
    }
}

fun main() {
    val input = ResourcesUtils.getResourceAsLinesStream("day15-challenge-input.txt").reduce {
            acc, line -> "$acc\n$line"
    }.get()

    var state = WarehouseWoes.initialStateFromInput(input)
    while (!state.moves.isEmpty()) {
        state = WarehouseWoes.nextState(state)
    }

    println("Sum of all GPS coordinates of boxes: ${WarehouseWoes.sumOfAllGPS(state)}")
}