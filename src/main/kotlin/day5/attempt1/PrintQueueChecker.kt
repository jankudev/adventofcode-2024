package dev.janku.katas.day5.attempt1

import dev.janku.katas.day5.attempt1.model.Queue
import dev.janku.katas.day5.attempt1.model.Queues
import dev.janku.katas.day5.attempt1.model.Rules
import dev.janku.katas.utils.ResourcesUtils

class PrintQueueChecker (val rules: Rules, val queues: Queues) {

    companion object {

        fun fromInput(input: String) : PrintQueueChecker {
            val (inputRules, inputQueues) = input.split("\n\n")
            val rules = Rules.fromInput(inputRules)
            val queues = Queues.fromInput(inputQueues)

            return PrintQueueChecker(rules, queues)
        }
    }

    fun checkQueue(q : Queue) : Boolean{
        return q.subList(0, q.size - 1).zip(q.drop(1))
            .all { (a, b) ->
                val is_B_after_A = rules.allBefore(b).let { it.isEmpty() || it.contains(a) }
                val is_B_notBefore_A = !rules.allBefore(a).contains(b)
                is_B_after_A && is_B_notBefore_A
            }
    }

    fun sumMiddleElemOfAllGoodQueues() : Int {
        return queues.list.filter(::checkQueue).map { it.get(it.size/2) }.sum()
    }

    private fun orderComparator(a: Int, b: Int) : Int {
        val is_B_after_A = rules.allBefore(b).let { it.isEmpty() || it.contains(a) }
        val is_B_notBefore_A = !rules.allBefore(a).contains(b)
        return when {
            is_B_after_A && is_B_notBefore_A -> -1
            a == b -> 0
            else -> 1
        }
    }

    fun sumMiddleElemOfAllBadQueuesAfterReordering() : Int {
        return queues.list.filterNot(::checkQueue).map { it.sortedWith(::orderComparator).get(it.size/2) }.sum()
    }
}

fun main() {
    val input = ResourcesUtils.getResourceAsLinesStream("day5-challenge-input.txt").reduce {
            acc, line -> "$acc\n$line"
    }.get()
    val checker = PrintQueueChecker.fromInput(input)
    println("Sum of mid-elem of checked queues: ${checker.sumMiddleElemOfAllGoodQueues()}")
    println("Sum of mid-elem of bad queues after reordering: ${checker.sumMiddleElemOfAllBadQueuesAfterReordering()}")
}
