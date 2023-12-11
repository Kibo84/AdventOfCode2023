package challenges.day10.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

const val startEnd = 'S'
const val upDawn = '|'
const val leftDown = '7'
const val leftRight = '-'
const val leftUp = 'J'
const val rightUp = 'L'
const val rightDown = 'F'
const val stepAdjustment = 1
val initialSteps = listOf(
    Instruction.UP,
    Instruction.LEFT,
    Instruction.RIGHT,
    Instruction.DOWN
)
val northSteps = listOf(
    upDawn,
    leftUp,
    rightUp
)
val tileMap: MutableMap<Coordinate, Char> = mutableMapOf()
var start: Coordinate = Coordinate(0, 0)
var coordinate: Coordinate = Coordinate(0, 0)

fun main() {
    val file = File("src/inputs/input-day-10.txt")

    val lines = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
    }

    lines.forEachIndexed { index, line ->
        line.forEachIndexed { charIndex, char ->
            tileMap[Coordinate(coordinateY = index, coordinateX = charIndex)] = char
        }
    }

    start = tileMap.entries.first { it.value == 'S' }.key
    coordinate = start.copy()

    val listStepsDefinitive = initialSteps.firstNotNullOf(::travelPipe)
    val result = countInsideLoopPipes(listLines = lines, listSteps = listStepsDefinitive)
    println(result)
}

fun travelPipe(instruction: Instruction): List<Coordinate>? {
    val listSteps: MutableList<Coordinate> = mutableListOf()
    var nextInstruction = instruction
    while (nextInstruction != Instruction.STOP && nextInstruction != Instruction.ARRIVE) {
        nextInstruction = nextMove(nextInstruction, listSteps)
    }
    if (nextInstruction == Instruction.STOP) {
        coordinate = start
        return null
    }
    return listSteps.toList()
}

fun nextMove(instruction: Instruction, listSteps: MutableList<Coordinate>): Instruction {
    return when(instruction) {
        Instruction.UP -> moveToUp(listSteps)
        Instruction.LEFT -> moveToLeft(listSteps)
        Instruction.RIGHT -> moveToRight(listSteps)
        Instruction.DOWN -> moveToDown(listSteps)
        else -> instruction
    }
}

fun moveToUp(listSteps: MutableList<Coordinate>): Instruction {
    listSteps.add(coordinate.copy())
    coordinate.coordinateY -= stepAdjustment
    return when (tileMap[coordinate]) {
        upDawn -> Instruction.UP
        leftDown -> Instruction.LEFT
        rightDown -> Instruction.RIGHT
        startEnd -> Instruction.ARRIVE
        else -> Instruction.STOP
    }
}

fun moveToLeft(listSteps: MutableList<Coordinate>): Instruction {
    listSteps.add(coordinate.copy())
    coordinate.coordinateX -= stepAdjustment
    return when (tileMap[coordinate]) {
        leftRight -> Instruction.LEFT
        rightUp -> Instruction.UP
        rightDown -> Instruction.DOWN
        startEnd -> Instruction.ARRIVE
        else -> Instruction.STOP
    }
}

fun moveToDown(listSteps: MutableList<Coordinate>): Instruction {
    listSteps.add(coordinate.copy())
    coordinate.coordinateY += stepAdjustment
    return when (tileMap[coordinate]) {
        upDawn -> Instruction.DOWN
        rightUp -> Instruction.RIGHT
        leftUp -> Instruction.LEFT
        startEnd -> Instruction.ARRIVE
        else -> Instruction.STOP
    }
}

fun moveToRight(listSteps: MutableList<Coordinate>): Instruction {
    listSteps.add(coordinate.copy())
    coordinate.coordinateX += stepAdjustment
    return when (tileMap[coordinate]) {
        leftRight -> Instruction.RIGHT
        leftDown -> Instruction.DOWN
        leftUp -> Instruction.UP
        startEnd -> Instruction.ARRIVE
        else -> Instruction.STOP
    }
}

fun countInsideLoopPipes(listLines: List<String>, listSteps: List<Coordinate>): Int {
    val listString = mutableListOf<String>()
    var insideLoop = false
    var insideLoopCount = 0
    listLines.forEachIndexed { index, line ->
        var newLine = ""
        line.forEachIndexed { charIndex, char ->
            var charCopy = char
            if (charCopy == 'S') charCopy = '|'
            val cord = Coordinate(coordinateY = index, coordinateX = charIndex)
            if (cord in listSteps && charCopy in northSteps) {
                insideLoop = !insideLoop
            }
            if (cord !in listSteps && insideLoop) {
                insideLoopCount++
            }
            newLine += charCopy
        }
        listString.add(newLine)
    }
    return insideLoopCount
}

data class Coordinate(var coordinateY: Int, var coordinateX: Int)

enum class Instruction {
    UP,
    LEFT,
    RIGHT,
    DOWN,
    STOP,
    ARRIVE
}