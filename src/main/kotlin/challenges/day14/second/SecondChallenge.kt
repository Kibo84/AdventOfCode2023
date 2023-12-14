package challenges.day14.second

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
    val result = loopExecute(matrix)

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

fun rocksToWest(matrix: List<List<Char>>): List<List<Char>> {
    val copyOfMatrix = matrix.map { it.toMutableList() }.toMutableList()
    val indexAdjustment = 1
    val unchanged = 0
    var exitLoop = false

    while (!exitLoop) {
        var changes = 0
        copyOfMatrix.forEach { row ->
            row.forEachIndexed { index, char ->
                if (index != row.lastIndex) {
                    val nextIndex = index + indexAdjustment
                    val nextChar = row[nextIndex]
                    if (char == space && nextChar == movableRock) {
                        row[index] = nextChar.also { row[nextIndex] = char }
                        changes++
                    }
                }
            }
        }
        if (changes == unchanged) exitLoop = true
    }
    return copyOfMatrix.map { it.toList() }.toList()
}

fun rocksToSouth(matrix: List<List<Char>>): List<List<Char>> {
    val copyOfMatrix = matrix.map { it.toMutableList() }.toMutableList()
    val indexAdjustment = 1
    val unchanged = 0
    val firstIndex = 0
    var exitLoop = false

    while (!exitLoop) {
        var changes = 0
        copyOfMatrix.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { index, char ->
                if (rowIndex != firstIndex) {
                    val previousIndex = rowIndex - indexAdjustment
                    val previousChar = copyOfMatrix[previousIndex][index]
                    if (char == space && previousChar == movableRock) {
                        copyOfMatrix[rowIndex][index] = previousChar.also { copyOfMatrix[previousIndex][index] = char }
                        changes++
                    }
                }
            }
        }
        if (changes == unchanged) exitLoop = true
    }
    return copyOfMatrix.map { it.toList() }.toList()
}

fun rocksToEast(matrix: List<List<Char>>): List<List<Char>> {
    val copyOfMatrix = matrix.map { it.toMutableList() }.toMutableList()
    val indexAdjustment = 1
    val unchanged = 0
    val firstIndex = 0
    var exitLoop = false

    while (!exitLoop) {
        var changes = 0
        copyOfMatrix.forEach { row ->
            row.forEachIndexed { index, char ->
                if (index != firstIndex) {
                    val previousIndex = index - indexAdjustment
                    val previousChar = row[previousIndex]
                    if (char == space && previousChar == movableRock) {
                        row[index] = previousChar.also { row[previousIndex] = char }
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
        println("loop number: $loop")

        val matrixWithRocksToNorth = rocksToNorth(matrixToReturn)
        val matrixWithRocksToWest = rocksToWest(matrixWithRocksToNorth)
        val matrixWithRocksToSouth = rocksToSouth(matrixWithRocksToWest)
        matrixToReturn = rocksToEast(matrixWithRocksToSouth)

        val load = calculateLoadOfRow(matrixToReturn)
        if (!pattern.values.any { compareMatrix(it, matrixToReturn) }) {
            pattern[loop] = matrixToReturn
        } else {
            initPattern = pattern.entries.first { compareMatrix(it.value, matrixToReturn) }.key
            exitLoop = true
        }
        println(load)
        loop++
    }
    val patternIndex = pattern.keys.filter { it in initPattern..< loop }
    println(patternIndex)
    val loopToReturn = patternIndex[(generalLoopNumber - (initPattern + indexAdjustment)) % patternIndex.size]
    println(loopToReturn)
    return calculateLoadOfRow(pattern[loopToReturn]!!)
}

fun compareMatrix(matrix1: List<List<Char>>, matrix2: List<List<Char>>): Boolean {
    matrix1.forEachIndexed { index, chars ->
        chars.forEachIndexed { indexChar, char ->
            if (char != matrix2[index][indexChar]) return false
        }
    }
    return true
}