package challenges.day21.first

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

fun main() {
    val file = File("src/inputs/input-day-21.txt")

    val garden = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines().map(String::toCharArray)
    }

    val firstPosition = calculateFirstPosition(garden)
    val result = calculatePossiblePositions(firstPosition, garden)

    println(result)
}

fun calculatePossiblePositions(firstPosition: Pair<Int, Int>, garden: List<CharArray>): Int {
    val lastStep = 65
    val positions: MutableMap<Int, MutableSet<Pair<Int, Int>>> = mutableMapOf()
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
    return positions[step]!!.size
}

fun calculateFirstPosition(garden: List<CharArray>): Pair<Int, Int> {
    var firstPosition = Pair(-1, -1)
    garden.forEachIndexed { line, chars ->
        chars.forEachIndexed { indexChar, char ->
            if (char == initialPosition) firstPosition = Pair(line, indexChar)
        }
    }
    return firstPosition
}

fun Pair<Int, Int>.move(direction: Direction): Pair<Int, Int> {
    return Pair(this.first + direction.y, this.second + direction.x)
}

fun Pair<Int, Int>.isValidPosition(garden: List<CharArray>): Boolean {
    val firstPosition = 0
    val height = garden.size
    val width = garden[firstPosition].size
    val rock = '#'

    if (this.first !in firstPosition..< height || this.second !in firstPosition..< width) return false
    if (garden[this.first][this.second] == rock) return false

    return true
}