package challenges.day11.first

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.math.abs

const val galaxy = '#'

fun main() {
    val file = File("src/inputs/input-day-11.txt")

    val lines = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
    }

    val galaxies = Galaxy.fromLines(lines)
    val listGalaxies = galaxies.values.toList()
    expandUniverse(lines, listGalaxies)

    val result = listGalaxies.sumOf { it.calculateDistanceOfGalaxy(galaxies) }

    println(result)
}

fun expandUniverse(lines: List<String>, galaxies: List<Galaxy>): List<Galaxy> {
    val indexAdjustment = 1
    lines.forEachIndexed { index, line ->
        if (!line.contains(galaxy)) {
            galaxies.filter { it.coordinateY > index }.forEach {
                it.coordinateToCalculateY += indexAdjustment
            }
        }
    }
    lines.first().forEachIndexed { index, _ ->
        if (isVoidColumn(index, lines)) {
            galaxies.filter { it.coordinateX > index }.forEach {
                it.coordinateToCalculateX += indexAdjustment
            }
        }
    }

    return galaxies
}

fun isVoidColumn(index: Int, lines: List<String>) = lines.all { it[index] != galaxy }

data class Galaxy(
    val number: Int,
    val coordinateX: Int,
    val coordinateY: Int,
    var coordinateToCalculateX: Long,
    var coordinateToCalculateY: Long
) {

    fun calculateDistanceOfGalaxy(galaxyMap: Map<Int, Galaxy>): Long {
        val firstIndex = this.number
        var result = 0L
        for (galaxyIndex in firstIndex .. galaxyMap.size) {
            result += calculateDistanceBetweenGalaxies(galaxyMap[galaxyIndex] ?: this)
        }

        return result
    }

    private fun calculateDistanceBetweenGalaxies(secondGalaxy: Galaxy): Long {
        val verticalDistance = abs(this.coordinateToCalculateY - secondGalaxy.coordinateToCalculateY)
        val horizontalDistance = abs(this.coordinateToCalculateX - secondGalaxy.coordinateToCalculateX)

        return verticalDistance + horizontalDistance
    }
    companion object {
        fun fromLines(lines: List<String>): Map<Int, Galaxy> {
            val mapGalaxy = mutableMapOf<Int, Galaxy>()
            var galaxyNumber = 0

            lines.forEachIndexed { indexLine, line ->
                line.forEachIndexed { indexChar, char ->
                    if (char == galaxy) {
                        galaxyNumber++
                        mapGalaxy[galaxyNumber] = Galaxy(
                            number = galaxyNumber,
                            coordinateX = indexChar,
                            coordinateY = indexLine,
                            coordinateToCalculateX = indexChar.toLong(),
                            coordinateToCalculateY = indexLine.toLong()
                        )
                    }
                }
            }
            return mapGalaxy.toMap()
        }
    }
}