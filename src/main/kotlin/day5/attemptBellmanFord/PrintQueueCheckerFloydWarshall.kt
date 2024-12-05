package dev.janku.katas.day5.attemptBellmanFord

import dev.janku.katas.utils.ResourcesUtils

/* generated by Claude AI - modified */
class Graph(relations: List<Rule>) {
    private val vertices: List<Int>
    private val distances: Array<Array<Int>>
    private val next: Array<Array<Int?>>

    companion object {
        const val INF = Int.MAX_VALUE
    }

    init {
        // Extract unique vertices maintaining order of appearance
        vertices = relations
            .flatMap { rel -> rel.toList() }
            .distinct()

        val size = vertices.size

        // Initialize distance matrix with INF
        distances = Array(size) { Array(size) { INF } }

        // Initialize next matrix with nulls
        next = Array(size) { Array(size) { null } }

        // Set diagonal to 0
        for (i in 0 until size) {
            distances[i][i] = 0
        }

        // Fill matrices based on relations
        relations.forEach { rel ->
            val (to, from) = rel
            val fromIdx = vertices.indexOf(from)
            val toIdx = vertices.indexOf(to)
            distances[fromIdx][toIdx] = 1  // weight is 1
            next[fromIdx][toIdx] = to
        }
    }

    fun floydWarshall() {
        val size = vertices.size

        for (k in 0 until size) {
            for (i in 0 until size) {
                for (j in 0 until size) {
                    if (distances[i][k] != INF && distances[k][j] != INF) {
                        val newDist = distances[i][k] + distances[k][j]
                        if (newDist < distances[i][j]) {
                            distances[i][j] = newDist
                            next[i][j] = next[i][k]
                        }
                    }
                }
            }
        }
    }

    fun getPath(from: Int, to: Int): List<Int> {
        val fromIdx = vertices.indexOf(from)
        val toIdx = vertices.indexOf(to)

        if (fromIdx == -1 || toIdx == -1) return emptyList()
        if (next[fromIdx][toIdx] == null) return emptyList()

        val path = mutableListOf(from)
        var current = fromIdx

        while (current != toIdx) {
            val nextVertex = next[current][toIdx] ?: return emptyList()
            path.add(nextVertex)
            current = vertices.indexOf(nextVertex)
        }

        return path
    }

    fun getDistance(from: Int, to: Int): Int {
        val fromIdx = vertices.indexOf(from)
        val toIdx = vertices.indexOf(to)

        if (fromIdx == -1 || toIdx == -1) return INF
        return distances[fromIdx][toIdx]
    }

    fun printDistanceMatrix() {
        println("\nDistance matrix:")
        print("    ")
        vertices.forEach { print("${it.toString().padEnd(4)}") }
        println()

        for (i in vertices.indices) {
            print("${vertices[i].toString().padEnd(4)}")
            for (j in vertices.indices) {
                val dist = if (distances[i][j] == INF) "INF" else distances[i][j].toString()
                print("${dist.padEnd(4)}")
            }
            println()
        }
    }
}

typealias Rule = Pair<Int, Int>
typealias PrintQueue = List<Int>

class PrintQueueCheckerFloydWarshall (
    val rules: List<Rule>,
    val printQueues: List<PrintQueue>,
    val graph: Graph
) {
    companion object {
        fun fromInput(input: String): PrintQueueCheckerFloydWarshall {
            val (inputRules, inputQueues) = input.split("\n\n")

            val rules = inputRules.split("\n")
                .map { line -> line.split("|") }
                .map { (a, b) -> Rule(a.toInt(), b.toInt()) }

            val queues = inputQueues.split("\n")
                .map { line -> line.split(",") }
                .map { list -> list.map { it.toInt() } }

            // calculate distances by Bellman-Ford algorithm
            val graph = Graph(rules)
            graph.floydWarshall()

            return PrintQueueCheckerFloydWarshall(rules, queues, graph)
        }
    }

    fun isOrdered(queue: List<Int>): Boolean {
        return queue.subList(0, queue.size - 1).zip(queue.drop(1)).all { (a, b) ->
            graph.getDistance(b, a) != Graph.INF && graph.getDistance(b, a) > 0
        }
    }

    fun sumMiddleElemOfAllGoodQueues() : Int {
        return printQueues.filter(::isOrdered)
            .map { it.get(it.size/2) }.sum()
    }
}

fun main() {
    val input = ResourcesUtils.getResourceAsLinesStream("day5-challenge-input.txt").reduce {
            acc, line -> "$acc\n$line"
    }.get()
    val checker = PrintQueueCheckerFloydWarshall.fromInput(input)
    checker.graph.printDistanceMatrix()
    println("Sum of mid-elem of checked queues: ${checker.sumMiddleElemOfAllGoodQueues()}")
}

