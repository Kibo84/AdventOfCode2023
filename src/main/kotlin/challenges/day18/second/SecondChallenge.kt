package challenges.day18.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.math.abs

enum class Directions(val value: Char, val x: Int, val y: Int) {
    UP('3', 0, -1),
    LEFT('2', -1, 0),
    RIGHT('0', 1, 0),
    DOWN('1', 0, 1)
}

fun main() {
    val file = File("src/inputs/input-day-18.txt")

    val instructions = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines().map(Instruction::fromString)
    }

    val polygon = calculatePolygon(instructions)
    val result = polygonArea(polygon)

    println(result)
}

fun calculatePolygon(instructions: List<Instruction>): Polygon {
    val firstPositionX = 0L
    val firstPositionY = 0L
    var actualPositionX = firstPositionX
    var actualPositionY = firstPositionY
    val vertices = mutableListOf<Vertices>()
    var perimeter = 0L

    instructions.forEach {
        vertices.add(Vertices(coordinateX = actualPositionX, coordinateY = actualPositionY))
        perimeter += it.repeats
        actualPositionX += (it.direction.x * it.repeats)
        actualPositionY += (it.direction.y * it.repeats)
    }

    return Polygon(vertices = vertices, perimeter = perimeter)
}

fun polygonArea(polygon: Polygon): Long {
    val (vertices, perimeter) = polygon
    val numberOfVertices = vertices.size
    val middleDivider = 2
    val positionAdjustment = 1
    var area = 0L

    vertices.forEachIndexed { index, _ ->
        val current = vertices[index]
        val next = vertices[(index + positionAdjustment) % numberOfVertices]
        area += (current.coordinateX * next.coordinateY - next.coordinateX * current.coordinateY)
    }

    return (abs(area) / middleDivider) + ((perimeter / middleDivider) + positionAdjustment)
}

class Instruction(val direction: Directions, val repeats: Long) {
    companion object {
        @OptIn(ExperimentalStdlibApi::class)
        fun fromString(line: String): Instruction {
            val prefixToRemove = "(#"
            val suffixToRemove = ")"
            val firstPosition = 0
            val delimiter = ' '

            val (_, _, hexInstruction) = line.split(delimiter)
            val hexString = hexInstruction.removePrefix(prefixToRemove).removeSuffix(suffixToRemove)
            val direction = hexString.last()
            val repeats = hexString.substring(firstPosition ..< hexString.lastIndex)

            return Instruction(
                direction = Directions.entries.first { it.value == direction },
                repeats = repeats.hexToLong()
            )
        }
    }
}

data class Vertices(var coordinateX: Long, var coordinateY: Long)

data class Polygon(val vertices: List<Vertices>, val perimeter: Long)