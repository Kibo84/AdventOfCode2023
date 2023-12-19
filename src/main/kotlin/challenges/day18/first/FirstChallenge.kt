package challenges.day18.first

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.math.abs

enum class Directions(val value: String, val x: Int, val y: Int) {
    UP("U", 0, -1),
    LEFT("L", -1, 0),
    RIGHT("R", 1, 0),
    DOWN("D", 0, 1)
}

const val sizeAdjustment = 1

fun main() {
    val file = File("src/inputs/input-day-18.txt")

    val instructions = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines().map(Instruction::fromString)
    }
    
    val lagoon = drawLagoon(instructions)

    val matrixHeight = lagoon.keys.maxOf { it.first } + sizeAdjustment
    val matrixWith = lagoon.keys.maxOf { it.second } + sizeAdjustment

    val matrix = MutableList(matrixHeight) { MutableList(matrixWith) { ' ' } }
    val copyOfMatrix = matrix.toMutableList()

    matrix.forEachIndexed { indexLine, line ->
        line.forEachIndexed { index, _ ->
            if (Pair(indexLine, index) in lagoon.keys) {
                copyOfMatrix[indexLine][index] = '#'
            } else {
                copyOfMatrix[indexLine][index] = '.'
            }
        }
    }

    copyOfMatrix.map {
        it.map(::print)
        println()
    }
}

fun drawLagoon(instructions: List<Instruction>): Map<Pair<Int, Int>, String> {
    val firstPositionX = 0
    val firstPositionY = 0
    var actualPositionX = firstPositionX
    var actualPositionY = firstPositionY
    val positions = mutableListOf<Position>()
    val map = mutableMapOf<Pair<Int, Int>, String>()
    
    instructions.forEach {
        for (repeat in 0 ..< it.repeats) {
            positions.add(
                Position(
                    color = it.color,
                    coordinateX = actualPositionX,
                    coordinateY = actualPositionY
                )
            )
            actualPositionX += it.direction.x
            actualPositionY += it.direction.y
        }
    }

    val lagoon = centerLagoon(positions)

    lagoon.forEach {
        map[Pair(it.coordinateY, it.coordinateX)] = it.color
    }
    
    return map.toMap()
}

fun centerLagoon(positions: List<Position>): List<Position> {
    val axeXAdjustment = abs(positions.minOf { it.coordinateX })
    val axeYAdjustment = abs(positions.minOf { it.coordinateY })
    val positionsToReturn = positions.toMutableList()
    positions.forEachIndexed { index, position ->
        positionsToReturn[index] = Position(
            color = position.color,
            coordinateX = position.coordinateX + axeXAdjustment,
            coordinateY = position.coordinateY + axeYAdjustment
        )
    }

    return positionsToReturn
}

class Instruction(val direction: Directions, val repeats: Int, val color: String) {
    companion object {
        fun fromString(line: String): Instruction {
            val delimiter = ' '
            val (direction, repeats, color) = line.split(delimiter)
            return Instruction(
                direction = Directions.entries.first { it.value == direction },
                repeats = repeats.toInt(),
                color = color
            )
        }
    }
}

data class Position(val color: String, var coordinateX: Int, var coordinateY: Int)