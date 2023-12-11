package challenges.day10.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import challenges.day10.second.Pipes.*
import challenges.day10.second.Instruction.*

const val stepAdjustment = 1
val initialSteps = listOf(UP, LEFT, RIGHT, DOWN)
val northSteps = listOf(UP_DOWN.value, LEFT_UP.value, RIGHT_UP.value)
lateinit var tileMap: Map<Coordinate, Char>
lateinit var start: Coordinate
lateinit var coordinate: Coordinate

fun main() {
    val file = File("src/inputs/input-day-10.txt")

    val lines = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
    }

    tileMap = lines.mapIndexed { index, line ->
        line.mapIndexed { charIndex, char ->
            Coordinate(coordinateY = index, coordinateX = charIndex) to char
        }.toMap()
    }.flatMap { it.entries }.associate { Pair(it.key, it.value) }

    start = tileMap.entries.first { it.value == 'S' }.key
    coordinate = start.copy()

    val listStepsDefinitive = initialSteps.firstNotNullOf(::travelPipe)
    val result = countInsideLoopPipes(listLines = lines, listSteps = listStepsDefinitive)
    println(result)
}

fun travelPipe(instruction: Instruction): List<Coordinate>? {
    val listSteps: MutableList<Coordinate> = mutableListOf()
    var nextInstruction = instruction
    while (nextInstruction != STOP && nextInstruction != ARRIVE) {
        nextInstruction = nextMove(nextInstruction, listSteps)
    }
    if (nextInstruction == STOP) {
        coordinate = start
        return null
    }
    return listSteps.toList()
}

fun nextMove(instruction: Instruction, listSteps: MutableList<Coordinate>): Instruction {
    return when(instruction) {
        UP -> moveToUp(listSteps)
        LEFT -> moveToLeft(listSteps)
        RIGHT -> moveToRight(listSteps)
        DOWN -> moveToDown(listSteps)
        else -> instruction
    }
}

fun moveToUp(listSteps: MutableList<Coordinate>): Instruction {
    listSteps.add(coordinate.copy())
    coordinate.coordinateY -= stepAdjustment
    return when (tileMap[coordinate]) {
        UP_DOWN.value -> UP
        LEFT_DOWN.value -> LEFT
        RIGHT_DOWN.value -> RIGHT
        START_END.value -> ARRIVE
        else -> STOP
    }
}

fun moveToLeft(listSteps: MutableList<Coordinate>): Instruction {
    listSteps.add(coordinate.copy())
    coordinate.coordinateX -= stepAdjustment
    return when (tileMap[coordinate]) {
        LEFT_RIGHT.value -> LEFT
        RIGHT_UP.value -> UP
        RIGHT_DOWN.value -> DOWN
        START_END.value -> ARRIVE
        else -> STOP
    }
}

fun moveToDown(listSteps: MutableList<Coordinate>): Instruction {
    listSteps.add(coordinate.copy())
    coordinate.coordinateY += stepAdjustment
    return when (tileMap[coordinate]) {
        UP_DOWN.value -> DOWN
        RIGHT_UP.value -> RIGHT
        LEFT_UP.value -> LEFT
        START_END.value -> ARRIVE
        else -> STOP
    }
}

fun moveToRight(listSteps: MutableList<Coordinate>): Instruction {
    listSteps.add(coordinate.copy())
    coordinate.coordinateX += stepAdjustment
    return when (tileMap[coordinate]) {
        LEFT_RIGHT.value -> RIGHT
        LEFT_DOWN.value -> DOWN
        LEFT_UP.value -> UP
        START_END.value -> ARRIVE
        else -> STOP
    }
}

fun countInsideLoopPipes(listLines: List<String>, listSteps: List<Coordinate>): Int {
    var insideLoop = false
    var insideLoopCount = 0
    listLines.forEachIndexed { index, line ->
        line.forEachIndexed { charIndex, char ->
            var charCopy = char
            if (char == START_END.value) charCopy = UP_DOWN.value
            val cord = Coordinate(coordinateY = index, coordinateX = charIndex)
            if (cord in listSteps && charCopy in northSteps) {
                insideLoop = !insideLoop
            }
            if (cord !in listSteps && insideLoop) {
                insideLoopCount++
            }
        }
    }
    return insideLoopCount
}

data class Coordinate(var coordinateY: Int, var coordinateX: Int)

enum class Instruction { UP, LEFT, RIGHT, DOWN, STOP, ARRIVE }

enum class Pipes(val value: Char) {
    START_END('S'),
    UP_DOWN('|'),
    LEFT_DOWN('7'),
    LEFT_RIGHT('-'),
    LEFT_UP('J'),
    RIGHT_UP('L'),
    RIGHT_DOWN('F')
}