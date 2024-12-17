package day15

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WarehouseWoesTest {

    @Test
    fun `init - representation of Warehouse`() {
        val input = """
            ########
            #..O.O.#
            ##@.O..#
            #...O..#
            #.#.O..#
            #...O..#
            #......#
            ########
        """.trimIndent()

        val warehouse = WarehouseWoes.readWarehouseFromInput(input)

        assertEquals(8, warehouse.width())
        assertEquals(8, warehouse.height())
        assertEquals(MAP_SYMBOLS.ROBOT, warehouse[2][2])
        assertEquals(MAP_SYMBOLS.BOX, warehouse[1][3])

        // Check debug toString()
        assertEquals(input, warehouse.toString())
    }

    @Test
    fun `init - list of moves`() {
        val input = "<^^>>>vv<v>>v<<"

        val moves = WarehouseWoes.readMovesFromInput(input)
        assertEquals(15, moves.size)
        assertEquals(input, moves.map {it.char}.joinToString(""))
    }

    @Test
    fun `init - config string split into warehouse and moves`() {
        val input = """
            ########
            #..O.O.#
            ##@.O..#
            #...O..#
            #.#.O..#
            #...O..#
            #......#
            ########

            <^^>>>vv<v>>v<<
        """.trimIndent()

        val state = WarehouseWoes.initialStateFromInput(input)

        assertEquals(8, state.warehouse.width())
        assertEquals(8, state.warehouse.height())
        assertEquals(15, state.moves.size)
    }

    @Test
    fun `move - simple moves`() {
        val input = """
            ####
            #..#
            #@.#
            ####

            ^>v<
        """.trimIndent()

        // initial state
        val state = WarehouseWoes.initialStateFromInput(input)
        assertEquals("""
            ####
            #..#
            #@.#
            ####
            
            ^>v<
        """.trimIndent(), state.toString())

        // move
        val state2 = WarehouseWoes.nextState(state)
        assertEquals("""
            ####
            #@.#
            #..#
            ####
            
            >v<
        """.trimIndent(), state2.toString())

        // move
        val state3 = WarehouseWoes.nextState(state2)
        assertEquals("""
            ####
            #.@#
            #..#
            ####
            
            v<
        """.trimIndent(), state3.toString())

        // move
        val state4 = WarehouseWoes.nextState(state3)
        assertEquals("""
            ####
            #..#
            #.@#
            ####
            
            <
        """.trimIndent(), state4.toString())

        // move
        val state5 = WarehouseWoes.nextState(state4)
        assertEquals("""
            ####
            #..#
            #@.#
            ####                   
        """.trimIndent().trim(), state5.toString().trim())
    }

    @Test
    fun `move - blocked by wall`() {
        val input = """
            ####
            #..#
            ##@#
            ####

            <^^
        """.trimIndent()

        var state = WarehouseWoes.initialStateFromInput(input)
        (0..2).forEach {
            state = WarehouseWoes.nextState(state)
        }

        assertEquals("""
            ####
            #.@#
            ##.#
            ####
        """.trimIndent().trim(), state.toString().trim())
    }

    @Test
    fun `example - small`() {
        val input = """
            ########
            #..O.O.#
            ##@.O..#
            #...O..#
            #.#.O..#
            #...O..#
            #......#
            ########

            <^^>>>vv<v>>v<<
        """.trimIndent()

        var state = WarehouseWoes.initialStateFromInput(input)
        while (!state.moves.isEmpty()) {
            state = WarehouseWoes.nextState(state)
        }

        assertEquals("""
            ########
            #....OO#
            ##.....#
            #.....O#
            #.#O@..#
            #...O..#
            #...O..#
            ########
        """.trimIndent().trim(), state.warehouse.toString().trim())
        assertEquals(2028L, WarehouseWoes.sumOfAllGPS(state))
    }

    @Test
    fun `example - large`() {
        val input = """
            ##########
            #..O..O.O#
            #......O.#
            #.OO..O.O#
            #..O@..O.#
            #O#..O...#
            #O..O..O.#
            #.OO.O.OO#
            #....O...#
            ##########

            <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
            vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
            ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
            <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
            ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
            ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
            >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
            <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
            ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
            v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
        """.trimIndent()

        var state = WarehouseWoes.initialStateFromInput(input)
        while (!state.moves.isEmpty()) {
            state = WarehouseWoes.nextState(state)
        }

        assertEquals("""
            ##########
            #.O.O.OOO#
            #........#
            #OO......#
            #OO@.....#
            #O#.....O#
            #O.....OO#
            #O.....OO#
            #OO....OO#
            ##########
        """.trimIndent().trim(), state.warehouse.toString().trim())

        assertEquals(10092L, WarehouseWoes.sumOfAllGPS(state))
    }
}