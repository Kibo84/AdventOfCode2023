package challenges.day21.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

enum class Direction(val y: Int, val x: Int) {
    UP( y = -1, x = 0),
    DOWN(y = 1, x = 0),
    LEFT(y = 0, x = -1),
    RIGHT(y = 0, x = 1)
}

const val totalSteps = 26501365
const val initialPosition = 'S'
const val firstPosition = 0
var height = 0
var width = 0

fun main() {
    val file = File("src/inputs/input-day-21.txt")

    var garden = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines().map(String::toCharArray)
    }

    val matrixHeight = garden.size
    val centerMatrix = calculateStartPosition(garden).y

    garden = expandGrid(garden, 51)

    height = garden.size
    width = garden[firstPosition].size

    val startPosition = calculateStartPosition(garden)

    val valueOne = calculatePossiblePositions(startPosition, garden, centerMatrix).toLong()
    val stepsTwo = (centerMatrix + matrixHeight)
    val valueTwo = calculatePossiblePositions(startPosition, garden, stepsTwo).toLong()
    val stepsThree = (centerMatrix + (matrixHeight * 2))
    val valueThree = calculatePossiblePositions(startPosition, garden, stepsThree).toLong()
    val stepsFour = (centerMatrix + (matrixHeight * 3))
    val valueFour = calculatePossiblePositions(startPosition, garden, stepsFour).toLong()

    var knowValues = mutableListOf(valueOne, valueTwo, valueThree, valueFour)
    val repeats = (totalSteps - stepsThree) / matrixHeight
    println(repeats)

    repeat(repeats) {
        knowValues = calculateNextValueOfSteps(knowValues)
        knowValues.removeFirst()
    }

    val result = knowValues.last()

    println(result)
}

fun calculatePossiblePositions(firstPosition: Position, garden: List<CharArray>, steps: Int): Int {
    val lastStep = steps
    val positions: MutableMap<Int, MutableSet<Position>> = mutableMapOf()
    positions[0] = mutableSetOf(firstPosition)

    var step = 0

    while (step < lastStep) {
        val beforePositions = positions.remove(step)
        positions[step + 1] = mutableSetOf()
        beforePositions?.forEach { position ->
            val nextPositions = Direction.entries.map { position.move(it) }
            val validNextPositions = nextPositions.filter { it.isValidPosition(garden) }
            positions[step + 1]?.addAll(validNextPositions)
        }
        step++
    }
    return positions[step]!!.size
}

fun calculateStartPosition(garden: List<CharArray>): Position {
    var firstPosition = Position(-1, -1)
    garden.forEachIndexed { line, chars ->
        chars.forEachIndexed { indexChar, char ->
            if (char == initialPosition) firstPosition = Position(line, indexChar)
        }
    }
    return firstPosition
}

fun expandGrid(original: List<CharArray>, factor: Int): List<CharArray> {
    val multiplier = if (factor % 2 == 0) factor + 1 else factor
    val rows = original.size
    val cols = original[0].size
    val expanded = List(rows * factor) { CharArray(cols * factor) }

    var count = 0

    for (i in 0..< rows * multiplier) {
        for (j in 0..< cols * multiplier) {
            expanded[i][j] = original[i % rows][j % cols]
            if (expanded[i][j] == 'S') {
                count++
                if (count != ((multiplier * multiplier) / 2) + 1) {
                    expanded[i][j] = '.'
                }
            }
        }
    }
    return expanded
}

fun calculateNextValueOfSteps(steps: MutableList<Long>): MutableList<Long> {
    val firstIndex = 0
    val previousIndexAdjustment = 1
    val calculateLines = mutableListOf<MutableList<Long>>()
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

    return newCalculateLines.last()
}

fun generateNextLine(steps: List<Long>): MutableList<Long> {
    val firstIndex = 0
    val previousIndexAdjustment = 1
    val nextLine = mutableListOf<Long>()
    steps.forEachIndexed { index, number ->
        val previousIndex = index - previousIndexAdjustment
        if (index != firstIndex) nextLine.add(number - steps[previousIndex])
    }
    return nextLine
}

fun Long.isNotZero(): Boolean {
    val zero = 0L
    return this != zero
}

fun Position.move(direction: Direction): Position {
    return Position(this.y + direction.y, this.x + direction.x)
}

fun Position.isValidPosition(garden: List<CharArray>): Boolean {
    val rock = '#'

    if (this.y !in firstPosition..< height || this.x !in firstPosition..< width) return false
    if (garden[this.y][this.x] == rock) return false

    return true
}

data class Position(var y: Int, var x: Int)