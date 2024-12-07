package day7

import dev.janku.katas.aoc2024.utils.ResourcesUtils

enum class Op (val str: String, val fnc: (Long, Long) -> Long) {
    PLUS("+", { a,b -> a+b } ),
    TIMES("*", { a,b -> a*b } ),
    CONCAT("||", { a,b -> (a.toString() + b.toString()).toLong() } );

    override fun toString() : String {
        return str
    }
}

data class Solution (
    val result: Long,
    val numbers: List<Int>,
    val operators: List<Op>
) {

    override fun toString() : String {
        return "${result}: " + numbers.zip(operators).joinToString(" ") { (num, op) -> "$num $op" } + " ${numbers.last()}"
    }
}

class BridgeRepair {

        companion object {

            fun generateOpCombinations(length: Int): List<List<Op>> {
                if (length < 1) return emptyList()

                return (1..length)
                    .fold(listOf(listOf())) { acc, _ ->
                        acc.flatMap { ops -> listOf(ops + Op.PLUS, ops + Op.TIMES, ops + Op.CONCAT) }
                    }
            }

            fun findAllPossibilities(numbers: List<Int>) : List<Solution> {
                val opCombinations = generateOpCombinations(numbers.size - 1)

                return opCombinations.map {
                    ops -> Solution(ops.foldIndexed(numbers[0].toLong()) {
                        idx, acc, op -> op.fnc(acc, numbers[idx + 1].toLong())
                    }, numbers, ops)
                }
            }

            fun findSolutions(result: Long, numbers: List<Int>) : List<Solution> {
                return findAllPossibilities(numbers).filter { it.result == result }
            }
        }
}

fun main() {
    val input = ResourcesUtils.getResourceAsLinesStream("day7-challenge-input.txt").reduce {
            acc, line -> "$acc\n$line"
    }.get()

    val partOneSolution = input.split("\n").map {
        val (result, numbersStr) = it.split(": ")
        BridgeRepair.findSolutions(result.toLong(), numbersStr.split(" ").map { it.toInt() })
    }.filter {
        it.isNotEmpty()
    }.map {
        it.first().result
    }.sum()

    println("Part one - Total Calibration Result: $partOneSolution")
}