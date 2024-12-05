package dev.janku.katas.day5.attempt1.model

typealias Rule = Pair<Int, Int>

class Rules (val list: List<Rule>,
             private val _dynProgCache: MutableMap<Int, List<Int>> = mutableMapOf()
) {

    companion object {

        fun fromInput(input : String) : Rules {
            return Rules(
                input.split("\n")
                .map { line -> line.split("|") }
                .map { (a, b) -> Rule(a.toInt(), b.toInt()) }
                .sortedBy { it.second }
            )
        }
    }

    init {
        calcBeforeForAllRules()
    }

    /**
     * Recursive function to check for all reachable elements with supportive rules
     * @param skip stack history of numbers in the recursion to detect cycles
     */
    private fun allBeforeInternal(num: Int, skip: List<Int>) : List<Int> {
        // guard against cycles
        if (skip.contains(num)) {
            return emptyList()
        }

        // dynamic programming guard to not calculate already calculated values
        _dynProgCache.get(num)?.let { return it }

        val directlyReachable = list.filter { it.second == num }.map { it.first }.sorted().distinct()
        if (directlyReachable.isEmpty()) {
            _dynProgCache[num] = directlyReachable
            return directlyReachable
        }
        val result = (directlyReachable + directlyReachable.flatMap { allBeforeInternal(it, directlyReachable + num) }).sorted().distinct()
        _dynProgCache[num] = result
        return result
    }

    /**
     * Recursively find all numbers that are before the given number
     * - leverage dynamic programming
     */
    fun allBefore(num: Int) : List<Int> {
        return allBeforeInternal(num, emptyList())
    }

    private fun calcBeforeForAllRules() {
        list.flatMap { it.toList() }
            .sorted()
            .forEach { allBefore(it) }
    }
}