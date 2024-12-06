package dev.janku.katas.aoc2024.day3

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CorruptedMemoryMultiplierTest {

    @Test
    fun `get only the executable instructions`() {
        val input = "xmul(2,3)y"
        assertEquals(listOf("mul(2,3)"), CorruptedMemoryMultiplier.getExecutableInstructions(input))
    }

    @Test
    fun `executing a single instruction`() {
        val input = "mul(2,3)"
        assertEquals(listOf(2,3), CorruptedMemoryMultiplier.ALGEBRA_OPS.MUL.extractArguments(input))
    }

    @Test
    fun `simple multiplication instruction`() {
        val input = "xmul(2,3)y"
        assertEquals(6, CorruptedMemoryMultiplier.execInstructionSet(input))
    }

    @Test
    fun `example challenge instruction sequence`() {
        val input = "xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))"
        assertEquals(4, CorruptedMemoryMultiplier.getExecutableInstructions(input).size)
        assertEquals(161, CorruptedMemoryMultiplier.execInstructionSet(input))
    }

    @Test
    fun `example challenge instruction sequence - with do and dont instructions`() {
        val input = "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"
        assertEquals(2, CorruptedMemoryMultiplier.getExecutableInstructionsExt(input).size)
        assertEquals(48, CorruptedMemoryMultiplier.execInstructionSetExt(input))
    }
}