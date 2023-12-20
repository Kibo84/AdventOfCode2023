package challenges.day18.first

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.math.abs
import challenges.day18.first.Directions.*

enum class Directions(val value: String, val x: Int, val y: Int) {
    UP("U", 0, -1),
    LEFT("L", -1, 0),
    RIGHT("R", 1, 0),
    DOWN("D", 0, 1)
}

fun main() {
    val file = File("src/inputs/input-testing.txt")

    val instructions = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines().map(Instruction::fromString)
    }
    
    val lagoon = drawLagoon(instructions)
    val result = polygonArea(lagoon)

    println(result)
}

fun drawLagoon(instructions: List<Instruction>): List<Position> {
    val firstPositionX = 0
    val firstPositionY = 0
    var actualPositionX = firstPositionX
    var actualPositionY = firstPositionY
    val positions = mutableListOf<Position>()
    var firstHorizontalDirection: Directions? = null
    var firstVerticalDirection: Directions? = null

    instructions.forEach { instruction ->
        positions.add(
            Position(
                color = instruction.color,
                coordinateX = actualPositionX,
                coordinateY = actualPositionY
            )
        )
        var adjustment = 1
//        if (instruction.direction == UP || instruction.direction == DOWN) {
//            if (firstVerticalDirection == null) {
//                firstVerticalDirection = instruction.direction
//            }
//            if (instruction.direction != firstVerticalDirection) adjustment = 0
//        }
        if (instruction.direction == LEFT || instruction.direction == RIGHT) {
            if (instruction.direction != firstHorizontalDirection) adjustment = 0
            firstHorizontalDirection = instruction.direction
        }
        actualPositionX += (instruction.direction.x * (instruction.repeats + adjustment))
        actualPositionY += (instruction.direction.y * (instruction.repeats + adjustment))
    }
    positions.map(::println)
    return positions
}

fun polygonArea(vertices: List<Position>): Int {
    val numberOfVertices = vertices.size
    val firstPosition = 0
    val positionAdjustment = 1
    var area = 0

    for (position in firstPosition ..< numberOfVertices) {
        val current = vertices[position]
        val next = vertices[(position + positionAdjustment) % numberOfVertices]
        area += (current.coordinateX * next.coordinateY - next.coordinateX * current.coordinateY)
    }

    area = abs(area) / 2

    return area
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