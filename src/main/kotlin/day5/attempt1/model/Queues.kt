package dev.janku.katas.day5.attempt1.model

typealias Queue = List<Int>

class Queues (val list : List<Queue>) {
    companion object {
        fun fromInput(input : String) : Queues {
            return Queues(
                input.split("\n")
                .map { line -> line.split(",") }
                .map { list -> list.map { it.toInt() } }
            )
        }
    }
}