package dev.janku.katas.aoc2024.day3

import dev.janku.katas.aoc2024.utils.ResourcesUtils

class CorruptedMemoryMultiplier {
    /**
     * Operations defined in the instruction set
     */
    enum class ALGEBRA_OPS(val opName: String, val def: Regex, val fnc: (List<Int>) -> Int) {
        MUL(opName = "mul", def = Regex("mul\\((\\d+),(\\d+)\\)"), fnc = { args -> args[0] * args[1] });

        fun extractArguments(input: String) : List<Int> {
            // guard
            if (!this.def.matches(input)) {
                throw IllegalArgumentException("Input does not match the operation definition")
            }

            // this will never throw as the regex is defined to match the arguments and the guard handles the rest
            return this.def.matchEntire(input)?.groups?.drop(1)?.map {
                    match -> match?.value?.toInt()
            }!!.toList().filterNotNull()
        }
    }

    enum class CONDITIONAL_OPS(val opName: String, val def: Regex) {
        DO(opName = "do", def = Regex("do\\(\\)")),
        DONT(opName = "don't", def = Regex("don't\\(\\)"))
    }

    companion object {

        /**
         * Pattern to match all operations in the instruction set - challenge 1
         */
        private val OP_DSL_PARSER_PATTERN = ALGEBRA_OPS.entries.map { it.def.pattern }
            .joinToString("|")
            .toRegex()

        private val OP_DSL_PARSER_EXT_PATTERN = ALGEBRA_OPS.entries.map { it.def.pattern }
            .plus(CONDITIONAL_OPS.entries.map { it.def.pattern })
            .joinToString("|")
            .toRegex()

        fun matchOperation(input: String): ALGEBRA_OPS {
            return ALGEBRA_OPS.entries.find { it.def.matches(input) } ?: throw IllegalArgumentException("No operation found")
        }

        fun matchConditionalOperation(input: String): CONDITIONAL_OPS? {
            return CONDITIONAL_OPS.entries.find { it.def.matches(input) }
        }

        fun getExecutableInstructions(input: String): List<String> {
            return OP_DSL_PARSER_PATTERN.findAll(input).map { it.value }.toList()
        }

        fun execInstructionSet(input: String): Int {
            return getExecutableInstructions(input).map {
                val op = CorruptedMemoryMultiplier.matchOperation(it)
                val args = op.extractArguments(it)
                op.fnc(args)
            }.sum()
        }

        fun getExecutableInstructionsExt(input: String): List<String> {
            val instructions = OP_DSL_PARSER_EXT_PATTERN.findAll(input).map { it.value }.toList()
            var enabled = true
            val executableInstructions = mutableListOf<String>()
            instructions.forEach { instr ->
                val condOp = matchConditionalOperation(instr)
                when (condOp) {
                    CONDITIONAL_OPS.DO -> enabled = true
                    CONDITIONAL_OPS.DONT -> enabled = false
                    else -> {
                        if (enabled) {
                            executableInstructions.add(instr)
                        }
                    }
                }
            }
            return executableInstructions
        }

        fun execInstructionSetExt(input: String): Int {
            return getExecutableInstructionsExt(input).map {
                val op = CorruptedMemoryMultiplier.matchOperation(it)
                val args = op.extractArguments(it)
                op.fnc(args)
            }.sum()
        }
    }
}

fun main() {
    val memory = ResourcesUtils.getResourceAsLinesStream("day3-challenge-input.txt").reduce {
        acc, line -> "$acc\n$line"
    }.get()
    println("Basic: ${CorruptedMemoryMultiplier.execInstructionSet(memory)}")
    println("Extended: ${CorruptedMemoryMultiplier.execInstructionSetExt(memory)}")
}