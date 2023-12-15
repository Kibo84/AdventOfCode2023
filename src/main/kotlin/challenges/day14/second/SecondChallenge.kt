package challenges.day14.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import challenges.day14.second.Directions.*
import challenges.day14.second.Axis.*

const val movableRock = 'O'
const val space = '.'

enum class Directions { UP, DOWN, LEFT, RIGHT }
enum class Axis { HORIZONTAL, VERTICAL }

fun main() {
    val file = File("src/inputs/input-day-14.txt")

    val lines = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
    }

    val matrix = readMatrixFromLines(lines)
    val result = loopExecute(matrix)

    println(result)
}

fun readMatrixFromLines(lines: List<String>) = lines.map { it.toCharArray().toList() }.toList()

fun rocksToDirection(matrix: List<List<Char>>, direction: Directions): List<List<Char>> {
    val copyOfMatrix = matrix.map { it.toMutableList() }.toMutableList()
    val firstIndex = 0
    val unchanged = 0
    val axis = if (direction == LEFT || direction == RIGHT) HORIZONTAL else VERTICAL
    val indexAdjustment = if (direction == LEFT || direction == UP) 1 else -1
    val lastIndex = if (axis == HORIZONTAL) copyOfMatrix[firstIndex].lastIndex else copyOfMatrix.lastIndex
    var exitLoop = false

    while (!exitLoop) {
        var changes = unchanged
        copyOfMatrix.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, char ->
                val index = if (axis == HORIZONTAL) columnIndex else rowIndex
                val indexValid = if (direction == LEFT || direction == UP) index != lastIndex else index != firstIndex
                val adjacentIndex = index + indexAdjustment
                if (indexValid) {
                    val adjacentChar = when (axis) {
                        HORIZONTAL -> row[adjacentIndex]
                        VERTICAL -> copyOfMatrix[adjacentIndex][columnIndex]
                    }
                    if (char == space && adjacentChar == movableRock) {
                        when (axis) {
                            HORIZONTAL -> {
                                row[columnIndex] = adjacentChar.also { row[adjacentIndex] = char }
                            }
                            VERTICAL -> {
                                copyOfMatrix[rowIndex][columnIndex] = adjacentChar.also {
                                    copyOfMatrix[adjacentIndex][columnIndex] = char
                                }
                            }
                        }
                        changes++
                    }
                }
            }
        }
        exitLoop = changes == unchanged
    }
    return copyOfMatrix.map { it.toList() }.toList()
}

fun calculateLoadOfRow(matrix: List<List<Char>>): Int {
    val maxLoad = matrix.size
    var load = 0
    matrix.forEachIndexed { index, chars ->
        chars.forEach {
            if (it == movableRock) load += (maxLoad - index)
        }
    }
    return load
}

fun loopExecute(matrix: List<List<Char>>): Int {
    val generalLoopNumber = 1000000000
    var matrixToReturn = matrix
    var loop = 0
    var initPattern = 0
    val pattern = mutableMapOf<Int, List<List<Char>>>()
    var exitLoop = false
    val indexAdjustment = 1

    while (!exitLoop) {
        matrixToReturn = rocksToDirection(matrixToReturn, UP)
        matrixToReturn = rocksToDirection(matrixToReturn, LEFT)
        matrixToReturn = rocksToDirection(matrixToReturn, DOWN)
        matrixToReturn = rocksToDirection(matrixToReturn, RIGHT)

        if (!pattern.values.any { compareMatrix(it, matrixToReturn) }) {
            pattern[loop] = matrixToReturn
        } else {
            initPattern = pattern.entries.first { compareMatrix(it.value, matrixToReturn) }.key
            exitLoop = true
        }
        loop++
    }
    val patternIndex = pattern.keys.filter { it in initPattern..< loop }
    val loopToReturn = patternIndex[(generalLoopNumber - (initPattern + indexAdjustment)) % patternIndex.size]
    return calculateLoadOfRow(pattern[loopToReturn]!!)
}

fun compareMatrix(firstMatrix: List<List<Char>>, secondMatrix: List<List<Char>>): Boolean {
    firstMatrix.forEachIndexed { index, chars ->
        chars.forEachIndexed { indexChar, char ->
            if (char != secondMatrix[index][indexChar]) return false
        }
    }
    return true
}