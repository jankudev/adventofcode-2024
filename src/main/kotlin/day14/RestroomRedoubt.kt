package day14

import dev.janku.katas.aoc2024.utils.ResourcesUtils
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

data class Coords(val x: Int, val y: Int)

data class RobotsInQuadrants(
    val q1: Int,
    val q2: Int,
    val q3: Int,
    val q4: Int
)

data class Robot (
    val position: Coords,
    val velocity: Coords
) {
    companion object {
        fun fromString(input: String): Robot {
            val parts = input.split(" ")
            val position = parts[0].substring(2, parts[0].length).split(",").map { it.toInt() }
            val velocity = parts[1].substring(2, parts[1].length).split(",").map { it.toInt() }
            return Robot(Coords(position[0], position[1]), Coords(velocity[0], velocity[1]))
        }
    }
}

class RestroomRedoubt {
    companion object {

        infix fun Int.mod(n: Int): Int {
            val result = this % n
            return if (result < 0) result + n else result
        }

        fun moveRobots(robots: List<Robot>, worldSize: Coords, i: Int): List<Robot> {
            return robots.map {
                robot -> Robot(
                    Coords((robot.position.x + robot.velocity.x * i) mod worldSize.x,
                         (robot.position.y + robot.velocity.y * i) mod worldSize.y),
                    robot.velocity
                )
            }
        }

        /**
         * Remove the middle row/column on odd numbers and map to quadrants
         */
        fun numOfRobotsInEachQuadrant(robots: List<Robot>, worldSize: Coords): RobotsInQuadrants {

            return robots.filterNot {
                (worldSize.x) % 2 == 1 && it.position.x == (worldSize.x / 2)
            }.filterNot {
                (worldSize.y) % 2 == 1 && it.position.y == (worldSize.y / 2)
            }.groupBy {
                when {
                    it.position.x<worldSize.x/2  && it.position.y<worldSize.y/2 -> 1
                    it.position.x>=worldSize.x/2 && it.position.y<worldSize.y/2 -> 2
                    it.position.x<worldSize.x/2  && it.position.y>=worldSize.y/2 -> 3
                    else -> 4
                }
            }.let {
                RobotsInQuadrants(
                    it[1]?.size ?: 0,
                    it[2]?.size ?: 0,
                    it[3]?.size ?: 0,
                    it[4]?.size ?: 0
                )
            }
        }

        fun safetyFactor(robotsDistribution: RobotsInQuadrants): Int {
            return robotsDistribution.q1 * robotsDistribution.q2 * robotsDistribution.q3 * robotsDistribution.q4
        }

        fun moveRobotsAndPrint(robots: List<Robot>, worldSize: Coords, maxSteps: Int) {
            val file = File("output.txt")
            val writer = BufferedWriter(FileWriter(file, true))

            // pattern of a multi-line chrismass tree made of '*' with empty spaces as '.'
            val TREE_REGEXP = Regex("#####################")

            writer.use {
                (0..maxSteps).forEach {
                    val robotsEnd = moveRobots(robots, worldSize, it)
                    //val robotsDistribution = numOfRobotsInEachQuadrant(robotsEnd, worldSize)
                    //val safetyFactor = safetyFactor(robotsDistribution)

                    val robotAsString = robotsToString(robotsEnd, worldSize, noCount = true)
                    if (TREE_REGEXP.containsMatchIn(robotAsString)) {
                        writer.appendLine("Step $it:")
                        writer.appendLine(robotAsString)
                        writer.appendLine()
                        writer.appendLine()
                    }
                }
            }
        }

        /** Debugging helper to match the provided examples with our state */
        fun robotsToString(robots: List<Robot>, worldSize: Coords, noCount: Boolean = false): String {
            val world = Array(worldSize.y) { Array(worldSize.x) { '.' } }
            robots.forEach { robot ->
                world[robot.position.y][robot.position.x] = if (noCount) '#' else
                    when (world[robot.position.y][robot.position.x]) {
                        '.' -> '1'
                        else -> world[robot.position.y][robot.position.x].digitToInt().inc().toString()[0]
                    }
            }
            return world.joinToString("\n") { it.joinToString("") }
        }
    }
}

fun main() {
    val input = ResourcesUtils.getResourceAsLinesStream("day14-challenge-input.txt").reduce {
            acc, line -> "$acc\n$line"
    }.get()

    val worldSize = Coords(101, 103)
    val robots = input.split("\n").map { Robot.fromString(it) }

    val robotsEnd = RestroomRedoubt.moveRobots(robots, worldSize, 100)
    val robotsDistribution = RestroomRedoubt.numOfRobotsInEachQuadrant(robotsEnd, worldSize)
    val safetyFactor = RestroomRedoubt.safetyFactor(robotsDistribution)

    println("Safety factor after 100 steps: $safetyFactor")

    // attempt to see a tree of robots
    RestroomRedoubt.moveRobotsAndPrint(robots, worldSize, 10000)
}