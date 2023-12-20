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
    val firstPositionX = 0
    val firstPositionY = 0
    var actualPositionX = firstPositionX
    var actualPositionY = firstPositionY
    val vertices = mutableListOf<Vertices>()
    var perimeter = 0

    instructions.forEach { instruction ->
        vertices.add(Vertices(coordinateX = actualPositionX, coordinateY = actualPositionY))
        perimeter += instruction.repeats
        actualPositionX += (instruction.direction.x * instruction.repeats)
        actualPositionY += (instruction.direction.y * instruction.repeats)
    }

    return Polygon(vertices = vertices, perimeter = perimeter)
}

fun polygonArea(polygon: Polygon): Int {
    val (vertices, perimeter) = polygon
    val numberOfVertices = vertices.size
    val middleDivider = 2
    val positionAdjustment = 1
    var area = 0

    vertices.forEachIndexed { index, _ ->
        val current = vertices[index]
        val next = vertices[(index + positionAdjustment) % numberOfVertices]
        area += (current.coordinateX * next.coordinateY - next.coordinateX * current.coordinateY)
    }

    return (abs(area) / middleDivider) + ((perimeter / middleDivider) + positionAdjustment)
}

class Instruction(val direction: Directions, val repeats: Int) {
    companion object {
        fun fromString(line: String): Instruction {
            val delimiter = ' '
            val (direction, repeats, _) = line.split(delimiter)
            return Instruction(
                direction = Directions.entries.first { it.value == direction },
                repeats = repeats.toInt()
            )
        }
    }
}

data class Vertices(var coordinateX: Int, var coordinateY: Int)

data class Polygon(val vertices: List<Vertices>, val perimeter: Int)