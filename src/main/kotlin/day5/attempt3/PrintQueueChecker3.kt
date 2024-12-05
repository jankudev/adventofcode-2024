package dev.janku.katas.day5.attempt3

import dev.janku.katas.utils.ResourcesUtils

typealias Rule = Pair<Int, Int>
typealias PrintQueue = List<Int>


class PrintQueueChecker3(val rules: List<Rule>, val printQueues: List<PrintQueue>) {

    companion object {
        fun fromInput(input: String) : PrintQueueChecker3 {
            val (inputRules, inputQueues) = input.split("\n\n")

            val rules = inputRules.split("\n")
                .map { line -> line.split("|") }
                .map { (a, b) -> Rule(a.toInt(), b.toInt()) }

            val queues = inputQueues.split("\n")
                .map { line -> line.split(",") }
                .map { list -> list.map { it.toInt() } }

            return PrintQueueChecker3(rules, queues)
        }
    }

    fun isOrdered(queue: List<Int>) : Boolean {
        val posToCheck = queue.subList(0, queue.size - 1).zip(queue.drop(1))
        return posToCheck.all {
            (a, b) -> rules.filter { it.second == b }.map { it.first }.contains(a)
        }
    }

    fun repairQueue(queue: List<Int>) : List<Int> {
        val mutableList : MutableList<Int> = queue.toMutableList()
        var changed : Boolean = false
        do {
            changed = false
            for (i in 0 until mutableList.size - 1) {
                val a = mutableList[i]
                val b = mutableList[i + 1]
                if (rules.filter { it.second == a && it.first == b }.isNotEmpty()) {
                    mutableList[i] = b
                    mutableList[i + 1] = a
                    changed = true
                    break
                }
            }
        } while (changed)
        return mutableList.toList()
    }

    fun bubbleSort_lowerThan(a: Int, b: Int) : Boolean {
        return rules.filter { it.second == b }.map { it.first }.contains(a)
                ||
                !rules.filter { it.second == a }.map { it.first }.contains(b)
    }

    fun bubbleSort_comparator(a: Int, b: Int) :  Int {
        return when {
                a == b -> 0
                bubbleSort_lowerThan(a, b) -> -1
                else -> 1
        }
    }

    fun bubbleSort(queue: List<Int>) : List<Int> {
        val ml = queue.toMutableList()
        val n = ml.size
        for (i in 0 until n - 1) {
            for (j in 0 until n - i - 1) {
                if (bubbleSort_lowerThan(ml[j], ml[j + 1])) {
                    // Swap elements
                    val temp = ml[j]
                    ml[j] = ml[j + 1]
                    ml[j + 1] = temp
                }
            }
        }
        return ml.toList()
    }

    fun sumMiddleElemOfAllGoodQueues() : Int {
        return printQueues.filter(::isOrdered)
            .map { it.get(it.size/2) }.sum()
    }

    fun sumMiddleElemOfAllBadQueuesAfterReordering() : Int {
        return printQueues.filterNot(::isOrdered)
//            .map { repairQueue(it) }
//            .map { bubbleSort(it) }
            .map { it.sortedWith(::bubbleSort_comparator) }
            .map { it.get(it.size/2) }.sum()
    }
}

fun main() {
    val input = ResourcesUtils.getResourceAsLinesStream("day5-challenge-input.txt").reduce {
            acc, line -> "$acc\n$line"
    }.get()
    val checker = PrintQueueChecker3.fromInput(input)
    println("Sum of mid-elem of checked queues: ${checker.sumMiddleElemOfAllGoodQueues()}")
    println("Sum of mid-elem of bad queues after reordering: ${checker.sumMiddleElemOfAllBadQueuesAfterReordering()}")
}
