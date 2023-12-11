package challenges.day10.first

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
const val pipeDivider = 2
var listSteps: MutableList<Coordinate> = mutableListOf()
val tileMap: MutableMap<Coordinate, Char> = mutableMapOf()
var start: Coordinate = Coordinate(0, 0)
var coordinate: Coordinate = Coordinate(0, 0)

fun main() {
    val file = File("src/inputs/input-day-10.txt")

    BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines().forEachIndexed { index, line ->
            line.forEachIndexed { charIndex, char ->
                tileMap[Coordinate(coordinateY = index, coordinateX = charIndex)] = char
            }
        }
    }
    val initialSteps = listOf(
        Instruction.UP,
        Instruction.LEFT,
        Instruction.RIGHT,
        Instruction.DOWN
    )
    start = tileMap.entries.first { it.value == 'S' }.key
    coordinate = start.copy()

    val possibleRoutes = initialSteps.map(::travelPipe)
    val result = possibleRoutes.filterNotNull().first().count() / pipeDivider + stepAdjustment

    println(result)
}

fun travelPipe(instruction: Instruction): List<Coordinate>? {
    var nextInstruction = instruction
    while (nextInstruction != Instruction.STOP && nextInstruction != Instruction.ARRIVE) {
        nextInstruction = nextMove(nextInstruction)
    }
    if (nextInstruction == Instruction.STOP) {
        coordinate = start
        listSteps = mutableListOf()
        return null
    }
    return listSteps.toList()
}

fun nextMove(instruction: Instruction): Instruction {
    return when(instruction) {
        Instruction.UP -> moveToUp()
        Instruction.LEFT -> moveToLeft()
        Instruction.RIGHT -> moveToRight()
        Instruction.DOWN -> moveToDown()
        else -> instruction
    }
}

fun moveToUp(): Instruction {
    if (coordinate != start) listSteps.add(coordinate.copy())
    coordinate.coordinateY -= stepAdjustment
    return when (tileMap[coordinate]) {
        upDawn -> Instruction.UP
        leftDown -> Instruction.LEFT
        rightDown -> Instruction.RIGHT
        startEnd -> Instruction.ARRIVE
        else -> Instruction.STOP
    }
}

fun moveToLeft(): Instruction {
    if (coordinate != start) listSteps.add(coordinate.copy())
    coordinate.coordinateX -= stepAdjustment
    return when (tileMap[coordinate]) {
        leftRight -> Instruction.LEFT
        rightUp -> Instruction.UP
        rightDown -> Instruction.DOWN
        startEnd -> Instruction.ARRIVE
        else -> Instruction.STOP
    }
}

fun moveToDown(): Instruction {
    if (coordinate != start) listSteps.add(coordinate.copy())
    coordinate.coordinateY += stepAdjustment
    return when (tileMap[coordinate]) {
        upDawn -> Instruction.DOWN
        rightUp -> Instruction.RIGHT
        leftUp -> Instruction.LEFT
        startEnd -> Instruction.ARRIVE
        else -> Instruction.STOP
    }
}

fun moveToRight(): Instruction {
    if (coordinate != start) listSteps.add(coordinate.copy())
    coordinate.coordinateX += stepAdjustment
    return when (tileMap[coordinate]) {
        leftRight -> Instruction.RIGHT
        leftDown -> Instruction.DOWN
        leftUp -> Instruction.UP
        startEnd -> Instruction.ARRIVE
        else -> Instruction.STOP
    }
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