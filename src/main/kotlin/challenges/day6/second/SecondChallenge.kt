package challenges.day6.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun main() {
    val file = File("src/inputs/input-day-6.txt")
    val input = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
    }
    val result = Race.fromInput(input).calculatePossibilities().calculateValidCombinations()

    println(result)
}

data class Race(
    val duration: Long,
    val distanceObjective: Long,
    val possibilities: MutableList<Possibility> = mutableListOf()
) {
    fun calculatePossibilities(): Race {
        for (secondsHold in 0 .. duration) {
            val distance = (duration - secondsHold) * secondsHold
            possibilities.add(Possibility(holdingButton = secondsHold, distance = distance))
        }

        return this
    }

    fun calculateValidCombinations(): Int {
        val validCombinations = this.possibilities.filter { it.distance > distanceObjective }.toList()

        return validCombinations.size
    }
    companion object {
        fun fromInput(input: List<String>): Race {
            val firstLine = 0
            val secondLine = 1

            return Race(
                duration = input[firstLine].filter(Char::isDigit).toLong(),
                distanceObjective = input[secondLine].filter(Char::isDigit).toLong()
            )
        }
    }
}

data class Possibility(val holdingButton: Long, val distance: Long)