package dev.janku.katas.aoc2024.day5.attempt2

import dev.janku.katas.aoc2024.utils.ResourcesUtils

typealias Rule = Pair<Int, Int>
typealias PrintQueue = List<Int>

class RulesParser (val rules : List<Rule>) {
    val _cache : MutableMap<Int, List<Int>> = mutableMapOf()

    init {
        rules.flatMap { it.toList() }
            .sorted()
            .forEach { allBefore(it) }
    }

    private fun allBefore(num: Int) : List<Int> {
        if (_cache.containsKey(num)) {
            return _cache[num]!!
        }
        val precedingNumsByRules = rules.filter { it.second == num }
            .map { it.first }
        val transitivePrecedingNumsByRules = precedingNumsByRules.map { allBefore(it) }.flatMap { it }
            .sorted()
        _cache[num] = precedingNumsByRules + transitivePrecedingNumsByRules
        return precedingNumsByRules
    }

    fun getOrderingComparator() : (a: Int, b: Int) -> Int {
        return { a, b ->
            val aLessThanB = _cache[a]?.contains(b) ?: false
            val bLessThanA = _cache[b]?.contains(a) ?: false
            when {
                a == b -> 0
                aLessThanB && !bLessThanA -> 1
                else -> -1
            }
        }
    }
}

class PrintQueueChecker2(val rules: List<Rule>, val printQueues: List<PrintQueue>) {

    companion object {
        fun fromInput(input: String) : PrintQueueChecker2 {
            val (inputRules, inputQueues) = input.split("\n\n")

            val rules = inputRules.split("\n")
                .map { line -> line.split("|") }
                .map { (a, b) -> Rule(a.toInt(), b.toInt()) }

            val queues = inputQueues.split("\n")
                .map { line -> line.split(",") }
                .map { list -> list.map { it.toInt() } }

            return PrintQueueChecker2(rules, queues)
        }
    }

    fun rulesForQueue(q: List<Int>) : List<Rule> {
        return rules.filter { rule -> q.contains(rule.first) || q.contains(rule.second) }
    }

    fun isOrdered(q: List<Int>) : Boolean {
        val rulesParser = RulesParser(rulesForQueue(q))
        return q.sortedWith(rulesParser.getOrderingComparator()).equals(q)
    }

    fun sumMiddleElemOfAllGoodQueues() : Int {
        return printQueues.filter(::isOrdered).map { it.get(it.size/2) }.sum()
    }

    fun sumMiddleElemOfAllBadQueuesAfterReordering() : Int {
        return printQueues.filterNot(::isOrdered)
            .map { it.sortedWith(RulesParser(rulesForQueue(it)).getOrderingComparator()) }
            .map { it.get(it.size/2) }.sum()
    }
}

fun main() {
    val input = ResourcesUtils.getResourceAsLinesStream("day5-challenge-input.txt").reduce {
            acc, line -> "$acc\n$line"
    }.get()
    val checker = PrintQueueChecker2.fromInput(input)
    println("Sum of mid-elem of checked queues: ${checker.sumMiddleElemOfAllGoodQueues()}")
    println("Sum of mid-elem of bad queues after reordering: ${checker.sumMiddleElemOfAllBadQueuesAfterReordering()}")
}
