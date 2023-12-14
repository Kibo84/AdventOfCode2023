package challenges.day14.first

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

const val movableRock = 'O'
const val space = '.'

fun main() {
    val file = File("src/inputs/input-day-14.txt")

    val lines = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
    }

    val matrix = readMatrixFromLines(lines)
    val matrixWithRocksToNorth = rocksToNorth(matrix)
    val result = calculateLoadOfRow(matrixWithRocksToNorth)

    println(result)
}

fun readMatrixFromLines(lines: List<String>) = lines.map { it.toCharArray().toList() }.toList()

fun rocksToNorth(matrix: List<List<Char>>): List<List<Char>> {
    val copyOfMatrix = matrix.map { it.toMutableList() }.toMutableList()
    val indexAdjustment = 1
    val unchanged = 0
    var exitLoop = false

    while (!exitLoop) {
        var changes = 0
        copyOfMatrix.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { index, char ->
                if (rowIndex != copyOfMatrix.lastIndex) {
                    val nextIndex = rowIndex + indexAdjustment
                    val nextChar = copyOfMatrix[nextIndex][index]
                    if (char == space && nextChar == movableRock) {
                        copyOfMatrix[rowIndex][index] = nextChar.also { copyOfMatrix[nextIndex][index] = char }
                        changes++
                    }
                }
            }
        }
        if (changes == unchanged) exitLoop = true
    }
    return copyOfMatrix.map { it.toList() }.toList()
}

fun calculateLoadOfRow(matrix: List<List<Char>>): Int {
    val maxLoad = matrix.size
    var load = 0
    matrix.forEachIndexed { index, chars ->
        chars.forEach {
            if (it == challenges.day14.second.movableRock) load += (maxLoad - index)
        }
    }
    return load
}