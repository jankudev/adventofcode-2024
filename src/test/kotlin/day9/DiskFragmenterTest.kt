package day9

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

fun String.asDiskStructure(): IntArray {
    return this.map {
        when (it) {
            '.' -> -1
            else -> it.digitToInt()
        }
    }.toIntArray()
}

class DiskFragmenterTest {

    /* expansion of input */
    @Test
    fun `expand input to disk structure - empty`() {
        assertContentEquals("".asDiskStructure(), DiskFragmenter.structureExpand(""))
    }

    @Test
    fun `expand input to disk structure - single block single file`() {
        assertContentEquals("0".asDiskStructure(), DiskFragmenter.structureExpand("1"))
    }

    @Test
    fun `expand input to disk structure - multiple block single file`() {
        assertContentEquals("00".asDiskStructure(), DiskFragmenter.structureExpand("2"))
    }

    @Test
    fun `expand input to disk structure - multiple block single file followed by single space`() {
        assertContentEquals("00.".asDiskStructure(), DiskFragmenter.structureExpand("21"))
    }

    @Test
    fun `expand input to disk structure - multiple block single file followed by multiple space`() {
        assertContentEquals("00...".asDiskStructure(), DiskFragmenter.structureExpand("23"))
    }

    @Test
    fun `expand input to disk structure - simple example`() {
        assertContentEquals("0..111".asDiskStructure(), DiskFragmenter.structureExpand("123"))
    }

    @Test
    fun `expand input to disk structure - example 1`() {
        assertContentEquals(
            "0..111....22222".asDiskStructure(),
            DiskFragmenter.structureExpand("12345"))
    }

    @Test
    fun `expand input to disk structure - example 2`() {
        assertContentEquals(
            "00...111...2...333.44.5555.6666.777.888899".asDiskStructure(),
            DiskFragmenter.structureExpand("2333133121414131402")
        )
    }

    /* full functionality examples */
    @Test
    fun `example - simple disk map`() {
        assertContentEquals("022111222......".asDiskStructure(), DiskFragmenter.defragment("12345").defragmented)
    }

    @Test
    fun `example - disk map`() {
        val input = "2333133121414131402"

        val result = DiskFragmenter.defragment(input)

        assertContentEquals("0099811188827773336446555566..............".asDiskStructure(), result.defragmented)
        assertEquals(BigDecimal(1928), result.checksum())
    }

    /* part 2 */
    @Test
    fun `converting to blocks - simple`() {
        val disk = listOf(0,0,0,-1,-1,-1,1,1,2).toIntArray()
        val blocks = DiskFragmenter.getBlocks(disk)
        assertContentEquals(
            listOf(
                Block(0, 0, 3),
                Block(-1, 3, 6),
                Block(1, 6, 8),
                Block(2, 8, 9)
            ),
            blocks
        )
        assertEquals(3, blocks[0].size())
        assertEquals(3, blocks[1].size())
        assertEquals(2, blocks[2].size())
        assertEquals(1, blocks[3].size())
    }

    @Test
    fun `example - disk map - part 2`() {
        val input = "2333133121414131402"

        val result = DiskFragmenter.defragment(input, DefragmentationStrategy.KEEP_FILES_WHOLE)

        assertContentEquals("00992111777.44.333....5555.6666.....8888..".asDiskStructure(), result.defragmented)
        assertEquals(BigDecimal(2858), result.checksum())
    }
}