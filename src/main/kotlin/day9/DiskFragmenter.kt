package day9

import dev.janku.katas.aoc2024.utils.ResourcesUtils
import java.math.BigDecimal

const val FREE = -1

enum class DefragmentationStrategy {
    PARTITION_FILES,
    KEEP_FILES_WHOLE
}

data class Block(val fileId: Int, val startDiskIdx: Int, val endDiskIdx: Int) {
    fun size(): Int {
        return endDiskIdx - startDiskIdx
    }
}

data class DefragmentedDisk(val defragmented: IntArray) {
    fun checksum(): BigDecimal {
        return defragmented
            .mapIndexed {
                idx, fileId -> when (fileId) {
                    FREE -> BigDecimal.ZERO
                    else -> BigDecimal(idx).multiply(BigDecimal(fileId))

            }
        }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }
    }
}

class DiskFragmenter {
    companion object {

        private fun swapBytesOnDisk(disk: IntArray, i: Int, j: Int) {
            val x = disk[j]
            disk[j] = disk[i]
            disk[i] = x
        }

        fun defragment(input: String, strategy: DefragmentationStrategy = DefragmentationStrategy.PARTITION_FILES): DefragmentedDisk {
            return when (strategy) {
                DefragmentationStrategy.PARTITION_FILES -> defragmentedDisk(structureExpand(input))
                DefragmentationStrategy.KEEP_FILES_WHOLE -> defragmentedDiskWholeFiles(structureExpand(input))
            }
        }

        /**
         * Defragment the disk
         * 1. take first free space = '.', take last non-free space
         * 2. exchange
         * @param diskExpanded disk to defragment in expanded format
         */
        private fun defragmentedDisk(diskExpanded: IntArray): DefragmentedDisk {
            var i = 0
            var j = diskExpanded.size - 1
            val disk = diskExpanded.copyOf()
            while (i < j) {
                while (i < j && disk[i] != FREE) i += 1
                while (i < j && disk[j] == FREE) j -= 1
                swapBytesOnDisk(disk, i, j)
            }
            return DefragmentedDisk(disk)
        }

        /**
         * Get blocks from the disk with indices [start,end)
         *
         * @info: would make private but need it exposed for unit tests using TDD
         */
        fun getBlocks(diskExpanded: IntArray): List<Block> {
            val blocks: MutableList<Block> = mutableListOf()
            var lastElem = diskExpanded.first()
            var startBlockIdx = 0
            var endBlockIdx = 0
            var idx = 0
            while (idx < diskExpanded.size) {
                if (diskExpanded[idx] == lastElem) {
                    endBlockIdx = idx
                } else {
                    blocks.add(Block(lastElem, startBlockIdx, endBlockIdx+1))
                    startBlockIdx = idx
                    endBlockIdx = idx
                    lastElem = diskExpanded[idx]
                }
                idx += 1
            }
            blocks.add(Block(lastElem, startBlockIdx, endBlockIdx+1))
            return blocks
        }

        /**
         * Moves a file block to a free block. Split free block if file is bigger.
         * @return the remaining free block or null
         */
        private fun moveFileBlockToFreeBlock(disk: IntArray, fileBlock: Block, freeBlock: Block): Block? {
            // guard clause
            if (freeBlock.size() < fileBlock.size()) {
                return null
            }
            // moving
            for (i in 0 until fileBlock.size()) {
                disk[freeBlock.startDiskIdx + i] = fileBlock.fileId
                disk[fileBlock.startDiskIdx + i] = FREE
            }
            // potential splitting
            return if (freeBlock.size() > fileBlock.size()) {
                Block(FREE, freeBlock.startDiskIdx + fileBlock.size(), freeBlock.endDiskIdx)
            } else {
                null
            }
        }

        /**
         * Defragment the disk but move whole files only (free space fits a whole file)
         * @param diskExpanded disk to defragment in expanded format
         */
        private fun defragmentedDiskWholeFiles(diskExpanded: IntArray): DefragmentedDisk {
            val blocks = getBlocks(diskExpanded)
            val freeBlocks = blocks.filter { it.fileId == FREE }.toMutableList()
            val fileBlocks = blocks.filter { it.fileId != FREE }.toMutableList()

            val disk = diskExpanded.copyOf()
            fileBlocks.reversed().forEach { fileBlock ->
                freeBlocks.firstOrNull { freeBlock ->
                    freeBlock.size() >= fileBlock.size() && freeBlock.endDiskIdx <= fileBlock.startDiskIdx
                }?.let { freeBlock ->
                    moveFileBlockToFreeBlock(disk, fileBlock, freeBlock)?.let { remainingBlock ->
                        freeBlocks[freeBlocks.indexOf(freeBlock)] = remainingBlock
                    } ?: freeBlocks.remove(freeBlock)
                }
            }
            return DefragmentedDisk(disk)
        }

        @Suppress("unused")
        private fun printDebug(disk: IntArray) {
            println(
                "Disk: ${
                    disk.map {
                        when (it) {
                            FREE -> '.'
                            else -> it.toString()
                        }
                    }.joinToString("")
                }"
            )
        }

        /**
         * Expand the dense format to a structure format
         */
        fun structureExpand(denseFormat: String): IntArray {
            return denseFormat.flatMapIndexed { idx, c: Char ->
                when ( idx % 2 ) {
                    0 -> (1..c.digitToInt()).map { (idx / 2) }
                    1 -> (1..c.digitToInt()).map { FREE }
                    else -> throw IllegalArgumentException("Invalid input")
                }
            }.toIntArray()
        }
    }
}

fun main() {
    val input = ResourcesUtils.getResourceAsLinesStream("day9-challenge-input.txt").reduce {
            acc, line -> "$acc\n$line"
    }.get()

    val result = DiskFragmenter.defragment(input)
    println("Checksum: ${result.checksum()}")

    val result2 = DiskFragmenter.defragment(input, DefragmentationStrategy.KEEP_FILES_WHOLE)
    println("Checksum (keeping whole files): ${result2.checksum()}")

}