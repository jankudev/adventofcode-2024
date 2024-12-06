package dev.janku.katas.aoc2024.day6

import dev.janku.katas.aoc2024.utils.ResourcesUtils

typealias Grid = List<List<Tile>>   // inner is a single row
typealias MutableGrid = MutableList<MutableList<Tile>>   // inner is a single row
typealias Coords = Pair<Int, Int>

/**
 * Symbolic representation of the lab tile
 */
enum class Tile (val symbol : Char) {
    // guard positions with direction
    GUARD_UP('^'),
    GUARD_RIGHT('>'),
    GUARD_DOWN('v'),
    GUARD_LEFT('<'),
    // lab tiles
    OBSTRUCTION('#'),
    ADDED_OBSTRUCTION('O'),
    TILE('.'),
    // marked lab tiles
    STEPPED_TILE('1'),
    STEPPED_TILE_TWICE('2'),
    STEPPED_TILE_TRICE('3'),
    STEPPED_TILE_QUATER('4'),
    // special stepped markers
    CYCLE_DETECTED('@'),        // like stepping 5 times on same tile (from all directions => we will cycle from now on)
    MARK_OUT_OF_BOUNDS('!');    // value object to pseudo-mark out of bounds and detect end of patrol

    companion object {
        fun charToTile(c: Char): Tile {
            return Tile.entries.first { it.symbol == c }
        }
    }

    override fun toString(): String {
        return symbol.toString()
    }
}

/**
 * Status of the patrol to determine if it's in progress or done
 */
enum class PatrolStatus {
    PATROL_NOT_STARTED,
    PATROL_IN_PROGRESS,
    PATROL_DONE,
    PATROL_INFINITE
}

/**
 * Representation of the guard's patrol (walk simulation step)
 */
class Patrol (
    val status : PatrolStatus,
    val grid : Grid,
    val guardPosition : Pair<Int, Int>,
    val symbolOnGuardPosition : Tile
) {
    fun getTilesSteppedOn(): Int {
        return printableMap().count { it == 'X' }
    }

    fun printableMap(): String {
        return grid.joinToString("\n") { row -> row.joinToString("") }
            .replace("\\d".toRegex(), "X")
    }
}

/**
 * Guard Gallivant - the guard's patrol simulation
 * @param state - current state of the patrol (so we can start from anywhere)
 */
class GuardGallivant (var state : Patrol) {

    /**
     * Factory method to create GuardGallivant from input string
     */
    companion object {
        fun fromInput(input: String): GuardGallivant {

            val grid = createInitialGrid(input)
            val initialGuardPosition = findInitialGuardPosition(grid)

            return GuardGallivant(
                Patrol(PatrolStatus.PATROL_NOT_STARTED, grid, initialGuardPosition, Tile.TILE)
            )
        }

        private fun createInitialGrid(input: String): List<List<Tile>> {
            val lines = input.split("\n")
            val grid = lines.mapIndexed { row, line ->
                line.mapIndexed { col, c ->
                    Tile.charToTile(c)
                }
            }
            return grid
        }

        private fun findInitialGuardPosition(grid: Grid): Pair<Int, Int> {
            val width = grid[0].size
            val height = grid.size
            (0 until height).forEach { row ->
                (0 until width).forEach { col ->
                    if (grid[row][col] in setOf(Tile.GUARD_UP, Tile.GUARD_RIGHT, Tile.GUARD_DOWN, Tile.GUARD_LEFT)) {
                        return Pair(row, col)
                    }
                }
            }
            throw IllegalStateException("Guard not found in the grid")
        }
    }

    private fun convertToMutableGrid(grid: Grid) : MutableGrid {
        return grid.map { it.toMutableList() }.toMutableList()
    }

    private fun symbolToMarkSteppedTile(oldTile : Tile) : Tile {
        return when (oldTile) {
            Tile.TILE -> Tile.STEPPED_TILE
            Tile.STEPPED_TILE -> Tile.STEPPED_TILE_TWICE
            Tile.STEPPED_TILE_TWICE -> Tile.STEPPED_TILE_TRICE
            Tile.STEPPED_TILE_TRICE -> Tile.STEPPED_TILE_QUATER
            Tile.STEPPED_TILE_QUATER -> Tile.CYCLE_DETECTED // no change, cycle
            else -> throw IllegalStateException("Invalid tile to mark as stepped")
        }
    }

    private fun guardMovesUp(state: Patrol): Patrol {
        val newGrid = convertToMutableGrid(state.grid)
        val (row, col) = state.guardPosition

        newGrid[row][col] = symbolToMarkSteppedTile(state.symbolOnGuardPosition)

        if (newGrid[row][col] == Tile.CYCLE_DETECTED) {
            return Patrol(PatrolStatus.PATROL_INFINITE, newGrid, Pair(row, col), Tile.CYCLE_DETECTED)
        }

        if (row - 1 >= 0) {
            val tileToBeSteppedOn = newGrid[row - 1][col]
            newGrid[row - 1][col] = Tile.GUARD_UP
            return Patrol(PatrolStatus.PATROL_IN_PROGRESS, newGrid, Pair(row - 1, col), tileToBeSteppedOn)
        }
        return Patrol(PatrolStatus.PATROL_DONE, newGrid, Pair(row, col), Tile.MARK_OUT_OF_BOUNDS)
    }

    private fun guardMovesRight(state: Patrol) : Patrol {
        val newGrid = convertToMutableGrid(state.grid)
        val (row, col) = state.guardPosition

        newGrid[row][col] = symbolToMarkSteppedTile(state.symbolOnGuardPosition)

        if (newGrid[row][col] == Tile.CYCLE_DETECTED) {
            return Patrol(PatrolStatus.PATROL_INFINITE, newGrid, Pair(row, col), Tile.CYCLE_DETECTED)
        }

        if (col + 1 < newGrid[row].size) {
            val tileToBeSteppedOn = newGrid[row][col + 1]
            newGrid[row][col + 1] = Tile.GUARD_RIGHT
            return Patrol(PatrolStatus.PATROL_IN_PROGRESS, newGrid, Pair(row, col + 1), tileToBeSteppedOn)
        }
        return Patrol(PatrolStatus.PATROL_DONE, newGrid, Pair(row, col), Tile.MARK_OUT_OF_BOUNDS)
    }

    private fun guardMovesDown(state: Patrol) : Patrol {
        val newGrid = convertToMutableGrid(state.grid)
        val (row, col) = state.guardPosition

        newGrid[row][col] = symbolToMarkSteppedTile(state.symbolOnGuardPosition)

        if (newGrid[row][col] == Tile.CYCLE_DETECTED) {
            return Patrol(PatrolStatus.PATROL_INFINITE, newGrid, Pair(row, col), Tile.CYCLE_DETECTED)
        }

        if (row + 1 < newGrid.size) {
            val tileToBeSteppedOn = newGrid[row + 1][col]
            newGrid[row + 1][col] = Tile.GUARD_DOWN
            return Patrol(PatrolStatus.PATROL_IN_PROGRESS, newGrid, Pair(row + 1, col), tileToBeSteppedOn)
        }
        return Patrol(PatrolStatus.PATROL_DONE, newGrid, Pair(row, col), Tile.MARK_OUT_OF_BOUNDS)
    }

    private fun guardMovesLeft(state: Patrol) : Patrol {
        val newGrid = convertToMutableGrid(state.grid)
        val (row, col) = state.guardPosition

        newGrid[row][col] = symbolToMarkSteppedTile(state.symbolOnGuardPosition)

        if (newGrid[row][col] == Tile.CYCLE_DETECTED) {
            return Patrol(PatrolStatus.PATROL_INFINITE, newGrid, Pair(row, col), Tile.CYCLE_DETECTED)
        }

        if (col - 1 >= 0) {
            val tileToBeSteppedOn = newGrid[row][col - 1]
            newGrid[row][col - 1] = Tile.GUARD_LEFT
            return Patrol(PatrolStatus.PATROL_IN_PROGRESS, newGrid, Pair(row, col - 1), tileToBeSteppedOn)
        }
        return Patrol(PatrolStatus.PATROL_DONE, newGrid, Pair(row, col), Tile.MARK_OUT_OF_BOUNDS)
    }

    private fun guardTurns(curState: Patrol): Patrol {
        val newGrid = convertToMutableGrid(state.grid)
        val (row, col) = state.guardPosition

        newGrid[row][col] = when (curState.grid[row][col]) {
            Tile.GUARD_UP    -> Tile.GUARD_RIGHT
            Tile.GUARD_RIGHT -> Tile.GUARD_DOWN
            Tile.GUARD_DOWN  -> Tile.GUARD_LEFT
            Tile.GUARD_LEFT  -> Tile.GUARD_UP
            else -> throw IllegalStateException("Invalid guard position - no guard found on given tile")
        }

        return Patrol(PatrolStatus.PATROL_IN_PROGRESS, newGrid, Pair(row, col), curState.symbolOnGuardPosition)
    }

    private fun isMovementBlocked(curState : Patrol) : Boolean {
        val (guardPosRow, guardPosCol) = curState.guardPosition
        return when (curState.grid[guardPosRow][guardPosCol]) {
            Tile.GUARD_UP    -> (guardPosRow - 1 >= 0) && (curState.grid[guardPosRow - 1][guardPosCol] in listOf(Tile.OBSTRUCTION, Tile.ADDED_OBSTRUCTION))
            Tile.GUARD_RIGHT -> (guardPosCol + 1 < curState.grid[0].size) && (curState.grid[guardPosRow][guardPosCol + 1] in listOf(Tile.OBSTRUCTION, Tile.ADDED_OBSTRUCTION))
            Tile.GUARD_DOWN  -> (guardPosRow + 1 < curState.grid.size) && (curState.grid[guardPosRow + 1][guardPosCol] in listOf(Tile.OBSTRUCTION, Tile.ADDED_OBSTRUCTION))
            Tile.GUARD_LEFT  -> (guardPosCol - 1 >= 0) && (curState.grid[guardPosRow][guardPosCol - 1] in listOf(Tile.OBSTRUCTION, Tile.ADDED_OBSTRUCTION))
            else -> throw IllegalStateException("Invalid guard position - no guard found on given tile")
        }
    }

    fun turnUntilUnblocked(curState: Patrol): Patrol {
        var nextState = curState
        for (turnTry in 0..3) {
            if (isMovementBlocked(nextState)) {
                nextState = guardTurns(nextState)
            } else {
                return nextState
            }
        }
        return Patrol(PatrolStatus.PATROL_INFINITE, nextState.grid, nextState.guardPosition, Tile.CYCLE_DETECTED)
    }

    /**
     * Movement based on the position and the rules
     * - if the guard is not blocked in the given direction, move forward
     * - if the guard is blocked, turn right
     */
    fun nextState(curState: Patrol): Patrol {
        // guard for already finished movement
        if (curState.status == PatrolStatus.PATROL_DONE) {
            return curState
        }

        val (guardPosRow, guardPosCol) = curState.guardPosition
        val guardFacing = curState.grid[guardPosRow][guardPosCol]
        return when {
            isMovementBlocked(curState)     -> turnUntilUnblocked(curState)
            guardFacing == Tile.GUARD_UP    -> guardMovesUp(curState)
            guardFacing == Tile.GUARD_RIGHT -> guardMovesRight(curState)
            guardFacing == Tile.GUARD_DOWN  -> guardMovesDown(curState)
            guardFacing == Tile.GUARD_LEFT  -> guardMovesLeft(curState)
            else -> throw IllegalStateException("Invalid guard position - no guard found on given tile")
        }
    }


    /**
     * Simulate a single step of the patrol
     */
    fun step() {
        state = nextState(state)
    }

    /**
     * Simulate the patrol of the lab completely
     * - safety check for infinite loops
     */
    fun patrolTheLab(): Patrol {
        while (! (state.status in listOf(PatrolStatus.PATROL_DONE, PatrolStatus.PATROL_INFINITE))) {
            step()
        }
        return state
    }

    /* part two */
    private fun checkPatrolIsInfiniteWithAddedObstacleAt(initialState: Patrol, row: Int, col: Int): Boolean {
        val newGrid = convertToMutableGrid(initialState.grid)
        newGrid[row][col] = Tile.ADDED_OBSTRUCTION
        val newState = Patrol(PatrolStatus.PATROL_NOT_STARTED, newGrid, initialState.guardPosition, Tile.TILE)
        val patrol = GuardGallivant(newState).patrolTheLab()

        return patrol.status == PatrolStatus.PATROL_INFINITE
    }

    fun countAllObstaclePlacementsCausingCycle(initialState : Patrol, originalPath : Patrol) : Int {
        val possiblePlacements : List<Coords> = (0 until originalPath.grid.size).flatMap {
            x -> (0 until originalPath.grid[0].size).map { y -> Pair(x, y) }
        }.filter {
            (row, col) -> originalPath.grid[row][col] in setOf(Tile.STEPPED_TILE, Tile.STEPPED_TILE_TWICE, Tile.STEPPED_TILE_TRICE, Tile.STEPPED_TILE_QUATER)
        }.filter {
            initialState.guardPosition != it
        }

        return possiblePlacements.parallelStream().filter {
            (row, col) -> checkPatrolIsInfiniteWithAddedObstacleAt(initialState, row, col)
        }.count().toInt()
    }
}

fun main() {
    val input = ResourcesUtils.getResourceAsLinesStream("day6-challenge-input.txt").reduce {
            acc, line -> "$acc\n$line"
    }.get()

    val guardGallivant = GuardGallivant.fromInput(input)
    val initialState = guardGallivant.state
    val patrol = guardGallivant.patrolTheLab()

    // Part 1
    //println("Final map:")
    //println(patrol.printableMap())
    //println("------------------------------------")
    println("Tiles stepped on: ${patrol.getTilesSteppedOn()}")

    // Part 2
    println("All obstacle placements causing cycle: ${guardGallivant.countAllObstaclePlacementsCausingCycle(initialState, patrol)}")
}