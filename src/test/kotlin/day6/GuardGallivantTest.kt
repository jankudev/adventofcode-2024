package dev.janku.katas.aoc2024.day6

import org.junit.jupiter.api.assertTimeoutPreemptively
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals

class GuardGallivantTest {

    companion object {
        val EXAMPLE_INPUT = """
            ....#.....
            .........#
            ..........
            ..#.......
            .......#..
            ..........
            .#..^.....
            ........#.
            #.........
            ......#...
        """.trimIndent()

        val EXAMPLE_WALKPATH = """
            ....#.....
            ....XXXXX#
            ....X...X.
            ..#.X...X.
            ..XXXXX#X.
            ..X.X.X.X.
            .#XXXXXXX.
            .XXXXXXX#.
            #XXXXXXX..
            ......#X..
        """.trimIndent()
    }

    @Test
    fun `example walk - provided testing input+output and control`() {
        val guardGallivant: GuardGallivant = GuardGallivant.fromInput(EXAMPLE_INPUT)
        val patrol: Patrol = guardGallivant.patrolTheLab()

        assertEquals(PatrolStatus.PATROL_DONE, patrol.status)
        assertEquals(EXAMPLE_WALKPATH, patrol.printableMap())
        assertEquals(41, patrol.getTilesSteppedOn())
    }

    @Test
    fun `test initial state`() {
        val guardGallivant: GuardGallivant = GuardGallivant.fromInput(EXAMPLE_INPUT)
        val initialState: Patrol = guardGallivant.state

        assertEquals(PatrolStatus.PATROL_NOT_STARTED, initialState.status)
        assertEquals(EXAMPLE_INPUT, initialState.printableMap())
        assertEquals(0, initialState.getTilesSteppedOn())
        assertEquals(Pair(6, 4), initialState.guardPosition)
    }

    @Test
    fun `test single simulation step - direct movement up`() {
        val input = """
            ...
            .^.
        """.trimIndent()
        val expectedState = """
            .^.
            .X.
        """.trimIndent()

        val guardGallivant = GuardGallivant.fromInput(input)
        guardGallivant.step()
        val state: Patrol = guardGallivant.state

        assertEquals(PatrolStatus.PATROL_IN_PROGRESS, state.status)
        assertEquals(expectedState, state.printableMap())
        assertEquals(1, state.getTilesSteppedOn())
    }

    @Test
    fun `test single simulation step - direct movement right`() {
        val input = """
            ...
            .>.
        """.trimIndent()
        val expectedState = """
            ...
            .X>
        """.trimIndent()

        val guardGallivant = GuardGallivant.fromInput(input)
        guardGallivant.step()
        val state: Patrol = guardGallivant.state

        assertEquals(PatrolStatus.PATROL_IN_PROGRESS, state.status)
        assertEquals(expectedState, state.printableMap())
        assertEquals(1, state.getTilesSteppedOn())
    }

    @Test
    fun `test single simulation step - direct movement down`() {
        val input = """
            .v.
            ...
        """.trimIndent()
        val expectedState = """
            .X.
            .v.
        """.trimIndent()

        val guardGallivant = GuardGallivant.fromInput(input)
        guardGallivant.step()
        val state: Patrol = guardGallivant.state

        assertEquals(PatrolStatus.PATROL_IN_PROGRESS, state.status)
        assertEquals(expectedState, state.printableMap())
        assertEquals(1, state.getTilesSteppedOn())
    }

    @Test
    fun `test single simulation step - direct movement left`() {
        val input = """
            .<.
            ...
        """.trimIndent()
        val expectedState = """
            <X.
            ...
        """.trimIndent()

        val guardGallivant = GuardGallivant.fromInput(input)
        guardGallivant.step()
        val state: Patrol = guardGallivant.state

        assertEquals(PatrolStatus.PATROL_IN_PROGRESS, state.status)
        assertEquals(expectedState, state.printableMap())
        assertEquals(1, state.getTilesSteppedOn())
    }

    @Test
    fun `test end conditions`() {
        val input = """
            .v.
            ...
        """.trimIndent()

        val expectedState = """
            .X.
            .X.
        """.trimIndent()

        val guardGallivant = GuardGallivant.fromInput(input)
        (0..2).forEach { guardGallivant.step() }
        val state: Patrol = guardGallivant.state

        assertEquals(PatrolStatus.PATROL_DONE, state.status)
        assertEquals(expectedState, state.printableMap())
        assertEquals(2, state.getTilesSteppedOn())

        // guard against further movement
        guardGallivant.step()
        assertEquals(PatrolStatus.PATROL_DONE, state.status)
        assertEquals(expectedState, state.printableMap())
        assertEquals(2, state.getTilesSteppedOn())
    }

    @Test
    fun `test movement with blockages`() {
        val input = """
            #..
            ...
            ^..
        """.trimIndent()

        val expectedState = """
            #..
            XX>
            X..
        """.trimIndent()

        val guardGallivant = GuardGallivant.fromInput(input)
        (0..3).forEach { guardGallivant.step() }
        val state: Patrol = guardGallivant.state

        assertEquals(PatrolStatus.PATROL_IN_PROGRESS, state.status)
        assertEquals(expectedState, state.printableMap())
        assertEquals(3, state.getTilesSteppedOn())
    }

    @Test
    fun `detect cycle - size 0`() {
        val input = """
            .#.
            #^#
            .#.
        """.trimIndent()
        val guardGallivant: GuardGallivant = GuardGallivant.fromInput(input)
        var patrol: Patrol? = null

        assertTimeoutPreemptively(Duration.ofSeconds(1), { patrol = guardGallivant.patrolTheLab()})
        assertEquals(PatrolStatus.PATROL_INFINITE, patrol?.status)
    }

    @Test
    fun `detect cycle - size 1`() {
        val input = """
            .#..
            ...#
            #^..
            ..#.
        """.trimIndent()
        val guardGallivant: GuardGallivant = GuardGallivant.fromInput(input)
        var patrol: Patrol? = null

        assertTimeoutPreemptively(Duration.ofSeconds(1), { patrol = guardGallivant.patrolTheLab()})
        assertEquals(PatrolStatus.PATROL_INFINITE, patrol?.status)
    }

    @Test
    fun `detect cycle - size 2`() {
        val input = """
            .#...
            ....#
            .....
            #^...
            ...#.
        """.trimIndent()
        val guardGallivant: GuardGallivant = GuardGallivant.fromInput(input)
        var patrol: Patrol? = null

        assertTimeoutPreemptively(Duration.ofSeconds(1), { patrol = guardGallivant.patrolTheLab()})
        assertEquals(PatrolStatus.PATROL_INFINITE, patrol?.status)
    }

    @Test
    fun `example input - find the possible placements of obstacle to create a cycle`() {
        val input = """
            ....#.....
            .........#
            ..........
            ..#.......
            .......#..
            ..........
            .#..^.....
            ........#.
            #.........
            ......#...
        """.trimIndent()

        val guardGallivant: GuardGallivant = GuardGallivant.fromInput(input)
        val initialState = guardGallivant.state
        val originalPath = guardGallivant.patrolTheLab()
        val count = guardGallivant.countAllObstaclePlacementsCausingCycle(initialState, originalPath)
        assertEquals(6, count)
    }
}