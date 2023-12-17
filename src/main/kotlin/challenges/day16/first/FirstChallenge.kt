package challenges.day16.first

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import challenges.day16.first.Directions.*

enum class Directions { UP, LEFT, RIGHT, DOWN }

const val energizedChar = '#'
var matrix: List<CharArray> = listOf()
var copyOfMatrix: List<MutableList<Char>> = listOf()

fun main() {
    val file = File("src/inputs/input-day-16.txt")

    val input = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
    }

    matrix = input.map(String::toCharArray)
    copyOfMatrix = matrix.map { line ->
        MutableList(line.size) { '.' }
    }

    calculateDirection(position = Position(0, 0), from = LEFT)

    val result = copyOfMatrix.sumOf { line ->
        line.count { it == energizedChar }
    }

    println(result)
}

fun calculateNextPosition(position: Position, direction: Directions) {
    val positionAdjustment = 1
    val newPosition = when (direction) {
        UP -> Position(x = position.x, y = position.y - positionAdjustment)
        LEFT -> Position(x = position.x - positionAdjustment, y = position.y)
        RIGHT -> Position(x = position.x + positionAdjustment, y = position.y)
        DOWN -> Position(x = position.x, y = position.y + positionAdjustment)
    }

    if (newPosition.isValidPosition()) calculateDirection(newPosition, direction.reverse())
}

fun calculateDirection(position: Position, from: Directions) {
    val emptySpace = '.'
    val horizontalDivider = '-'
    val verticalDivider = '|'
    val mirrorOne = '\\'
    val mirrorTwo = '/'

    val char = matrix[position.y][position.x]

    if ((char == horizontalDivider || char == verticalDivider)
        && copyOfMatrix[position.y][position.x] == energizedChar) {
        return
    }

    copyOfMatrix[position.y][position.x] = energizedChar

    when (char) {
        emptySpace -> calculateNextPosition(position, from.reverse())
        horizontalDivider -> {
            if (from == UP || from == DOWN) {
                calculateNextPosition(position, LEFT)
                calculateNextPosition(position, RIGHT)
            } else {
                calculateNextPosition(position, from.reverse())
            }
        }
        verticalDivider -> {
            if (from == LEFT || from == RIGHT) {
                calculateNextPosition(position, UP)
                calculateNextPosition(position, DOWN)
            } else {
                calculateNextPosition(position, from.reverse())
            }
        }
        mirrorOne -> {
            if (from == UP) calculateNextPosition(position, RIGHT)
            if (from == DOWN) calculateNextPosition(position, LEFT)
            if (from == RIGHT) calculateNextPosition(position, UP)
            if (from == LEFT) calculateNextPosition(position, DOWN)
        }
        mirrorTwo -> {
            if (from == UP) calculateNextPosition(position, LEFT)
            if (from == DOWN) calculateNextPosition(position, RIGHT)
            if (from == RIGHT) calculateNextPosition(position, DOWN)
            if (from == LEFT) calculateNextPosition(position, UP)
        }
    }
}

class Position(val x: Int, val y: Int) {
    fun isValidPosition(): Boolean {
        val firstPosition = 0
        val lastPositionX = matrix[firstPosition].size
        val lastPositionY = matrix.size
        return x in firstPosition ..< lastPositionX && y in firstPosition ..< lastPositionY
    }
}

fun Directions.reverse(): Directions {
    return when (this) {
        UP -> DOWN
        LEFT -> RIGHT
        RIGHT -> LEFT
        DOWN -> UP
    }
}