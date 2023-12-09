package challenges.day9.first

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun main() {
    val file = File("src/inputs/input-day-9.txt")

    val result = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
            .map(::calculateNextValueOfLine)
            .sum()
    }

    println(result)
}

fun calculateNextValueOfLine(line: String): Int {
    val delimiter = " "
    val valueSteps = line.split(delimiter).map { it.toInt() }
    return calculateNextValueOfLineSteps(valueSteps)
}

fun calculateNextValueOfLineSteps(steps: List<Int>): Int {
    val firstIndex = 0
    val previousIndexAdjustment = 1
    val calculateLines = mutableListOf<MutableList<Int>>()
    calculateLines.add(steps.toMutableList())

    while (calculateLines.last().any { it.isNotZero() }) {
        val newLine = generateNextLine(calculateLines.last())
        calculateLines.add(newLine)
    }

    val newCalculateLines = calculateLines.reversed().toMutableList()

    newCalculateLines.mapIndexed { index, line ->
        val previousIndex = index - previousIndexAdjustment
        if (index != firstIndex) {
            val previousLine = newCalculateLines[previousIndex]
            val lastNumberOfPresentLine = line.last()
            val lastNumberOfPreviousLine = previousLine.last()
            newCalculateLines[index].add(lastNumberOfPresentLine + lastNumberOfPreviousLine)
        }
    }

    return newCalculateLines.last().last()
}

fun generateNextLine(steps: List<Int>): MutableList<Int> {
    val firstIndex = 0
    val previousIndexAdjustment = 1
    val nextLine = mutableListOf<Int>()
    steps.forEachIndexed { index, number ->
        val previousIndex = index - previousIndexAdjustment
        if (index != firstIndex) nextLine.add(number - steps[previousIndex])
    }
    return nextLine
}

fun Int.isNotZero(): Boolean {
    val zero = 0
    return this != zero
}