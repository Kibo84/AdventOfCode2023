package challenges.day6.first

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun main() {
    val file = File("src/inputs/input-day-6.txt")
    val input = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
    }
    val races = Race.fromInput(input)
    val validCombinationsList = races.map(Race::calculatePossibilities).map(Race::calculateValidCombinations)
    var result = 1
    validCombinationsList.forEach { result *= it }

    println(result)
}

fun stringToListInt(input: String): List<Int> {
    val indexAdjustment = 1
    var number = ""
    var actualNumber = false
    val listNumbers = mutableListOf<Int>()

    input.forEachIndexed { index, char ->
        if (char.isDigit()) {
            if (!actualNumber) actualNumber = true
            number += char
            if (index == input.length - indexAdjustment) listNumbers.add(number.toInt())
        } else {
            if (actualNumber) {
                actualNumber = false
                listNumbers.add(number.toInt())
                number = ""
            }
        }
    }
    return listNumbers.toList()
}

data class Race(
    val duration: Int,
    val distanceObjective: Int,
    val possibilities: MutableList<Possibility> = mutableListOf()
) {

    fun calculatePossibilities(): Race {
        for (secondsHold in 0 .. duration) {
            val distance = (duration - secondsHold) * secondsHold
            possibilities.add(
                Possibility(
                    holdingButton = secondsHold,
                    distance = distance
                )
            )
        }

        return this
    }

    fun calculateValidCombinations(): Int {
        val validCombinations = this.possibilities.filter { possibility ->
            possibility.distance > distanceObjective
        }.toList()

        return validCombinations.size
    }
    companion object {
        fun fromInput(input: List<String>): List<Race> {
            val firstLine = 0
            val secondLine = 1
            val times = stringToListInt(input[firstLine])
            val distances = stringToListInt(input[secondLine])
            val races = mutableListOf<Race>()

            times.forEachIndexed { index, time ->
                races.add(Race(duration = time, distanceObjective = distances[index]))
            }

            return races.toList()
        }
    }
}

data class Possibility(val holdingButton: Int, val distance: Int)