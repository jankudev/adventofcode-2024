package day14

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Robot guarding restroom
 * - predictable movement - straight line
 * - robot position p=(x,y) as x distance from left, y distance from top
 * - robot direction velocities v=(x, y) as tiles per second (positive x is right, positive y is down)
 * - movement once hit edge continue from other side (grid overflow)
 *
 * World size 101 wide (max x), 103 tall (max y)
 */
class RestroomRedoubtTest {

    @Test
    fun `robot - test basic robot movement on smaller world 11x7`() {
        val worldSize = Coords(11, 7)
        val robot = "p=2,4 v=2,-3"

        val robots = listOf(Robot.fromString(robot))

        // initial state
        assertEquals(Coords(2, 4), RestroomRedoubt.moveRobots(robots, worldSize, 0)[0].position)
        assertEquals(Coords(4, 1), RestroomRedoubt.moveRobots(robots, worldSize, 1)[0].position)
        assertEquals(Coords(6, 5), RestroomRedoubt.moveRobots(robots, worldSize, 2)[0].position)
        assertEquals(Coords(8, 2), RestroomRedoubt.moveRobots(robots, worldSize, 3)[0].position)
        assertEquals(Coords(10, 6), RestroomRedoubt.moveRobots(robots, worldSize, 4)[0].position)
    }

    @Test
    fun `example - smaller world 11x7`() {
        val input = """
            p=0,4 v=3,-3
            p=6,3 v=-1,-3
            p=10,3 v=-1,2
            p=2,0 v=2,-1
            p=0,0 v=1,3
            p=3,0 v=-2,-2
            p=7,6 v=-1,-3
            p=3,0 v=-1,-2
            p=9,3 v=2,3
            p=7,3 v=-1,2
            p=2,4 v=2,-3
            p=9,5 v=-3,-3
        """.trimIndent()

        val worldSize = Coords(11, 7)
        val robots = input.split("\n").map { Robot.fromString(it) }

        // initial state check
        assertEquals("""
            1.12.......
            ...........
            ...........
            ......11.11
            1.1........
            .........1.
            .......1...
        """.trimIndent(), RestroomRedoubt.robotsToString(robots, worldSize))

        // after 100 steps
        val robotsEnd = RestroomRedoubt.moveRobots(robots, worldSize, 100)
        assertEquals("""
            ......2..1.
            ...........
            1..........
            .11........
            .....1.....
            ...12......
            .1....1....
        """.trimIndent(), RestroomRedoubt.robotsToString(robotsEnd, worldSize))

        // check number of robots in each quadrant

        println(RestroomRedoubt.robotsToString(robotsEnd.filterNot {
            (worldSize.x) % 2 == 1 && it.position.x == (worldSize.x / 2)
        }.filterNot {
            (worldSize.y) % 2 == 1 && it.position.y == (worldSize.y / 2)
        }, worldSize))

        val robotsInQuadrants = RestroomRedoubt.numOfRobotsInEachQuadrant(robotsEnd, worldSize)
        assertEquals(RobotsInQuadrants(1, 3, 4, 1), robotsInQuadrants)

        // check safety factor
        assertEquals(12, RestroomRedoubt.safetyFactor(robotsInQuadrants))
    }
}