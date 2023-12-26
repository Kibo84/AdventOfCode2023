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

const val initialPosition = 'S'
const val firstPosition = 0
var height = 0
var width = 0

fun main() {
    val file = File("src/inputs/input-testing.txt")

    var garden = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines().map(String::toCharArray)
    }

    val matrixHeight = garden.size - 1
    val centerMatrix = calculateStartPosition(garden).y

    garden = expandGrid(garden, 11)
    garden.map { it.map(::print); println() }

    height = garden.size
    width = garden[firstPosition].size

    val startPosition = calculateStartPosition(garden)

    val valueOne = calculatePossiblePositions(startPosition, garden, 6).toLong()
    val x2 = (centerMatrix + matrixHeight)
    val valueTwo = calculatePossiblePositions(startPosition, garden, 10).toLong()
    val x3 = (centerMatrix + (matrixHeight * 2))
    val valueThree = calculatePossiblePositions(startPosition, garden, 50).toLong()
    val x4 = (centerMatrix + (matrixHeight * 3))
    val valueFour = calculatePossiblePositions(startPosition, garden, 100).toLong()
    val x5 = (centerMatrix + (matrixHeight * 4))
    val valueFive = calculatePossiblePositions(startPosition, garden, 500).toLong()
    calculatePossiblePositions(startPosition, garden, 500).toLong()


    var knowValues = listOf(valueOne, valueTwo, valueThree, valueFour, valueFive)
    repeat(500) { knowValues = calculateNextValueOfSteps(knowValues) }

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
            positions[step + 1]?.addAll(nextPositions.filter { it.isValidPosition(garden) })
        }
        step++
    }
    println(positions[step]!!.size)
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
    val rows = original.size
    val cols = original[0].size
    val expanded = List(rows * factor) { CharArray(cols * factor) }

    for (i in 0..< rows * factor) {
        for (j in 0..< cols * factor) {
            expanded[i][j] = original[i % rows][j % cols]
            if (expanded[i][j] == 'S' && (i % rows != rows / 2 || j % cols != cols / 2)) {
                expanded[i][j] = '.'
            }
        }
    }
    return expanded
}

fun calculateNextValueOfSteps(steps: List<Long>): List<Long> {
    val firstIndex = 0
    val previousIndexAdjustment = 1
    val calculateLines = mutableListOf<MutableList<Long>>()
    calculateLines.add(steps.toMutableList())
    println(steps)

    while (calculateLines.last().any { it.isNotZero() }) {
        val newLine = generateNextLine(calculateLines.last())
        println(newLine)
        calculateLines.add(newLine)
    }

    val newCalculateLines = calculateLines.reversed().toMutableList()

    newCalculateLines.mapIndexed { index, line ->
        val previousIndex = index - previousIndexAdjustment
        if (index != firstIndex) {
            val previousLine = newCalculateLines[previousIndex]
            println(previousLine)
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