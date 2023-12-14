package challenges.day13.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun main() {
    val file = File("src/inputs/input-day-13.txt")

    val lines = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
    }

    val ashes = Ash.fromStrings(lines)
    val result = ashes.map(Ash::calculateValue).sum()
    println(result)
}

enum class Axe { VERTICAL, HORIZONTAL }

data class Ash(val ground: List<CharArray>) {

    fun calculateValue(): Int {
        val multiplierHorizontal = 100
        val resultHorizontal = calculateMirroring(Axe.HORIZONTAL)
        val resultVertical = calculateMirroring(Axe.VERTICAL)

        return if (resultHorizontal.linesMirrored > resultVertical.linesMirrored) {
            resultHorizontal.value * multiplierHorizontal
        } else {
            resultVertical.value
        }
    }

    private fun calculateMirroring(axe: Axe): Result {
        val indexAdjustment = 1
        val firstIndex = 0
        val lastIndex = calculateLastIndex(axe)
        var linesMirrored = 0
        var countLinesMirrored: Int? = null
        val smudge = 1
        val minValue = 0

        for (index in firstIndex ..< lastIndex) {
            var tempLinesMirrored = 0
            var linesCompared = 0
            var indexFirstLine = index
            var indexSecondLine = index + indexAdjustment
            var difference = 0
            while (difference <= 1 && indexFirstLine >= firstIndex && indexSecondLine <= lastIndex) {
                linesCompared++
                val line1 = extractLine(indexFirstLine, axe)
                val line2 = extractLine(indexSecondLine, axe)
                difference = isValid(line1, line2, difference)
                if (difference <= smudge) {
                    tempLinesMirrored++
                    indexFirstLine--
                    indexSecondLine++
                }
            }
            if (tempLinesMirrored > linesMirrored && linesCompared == tempLinesMirrored && difference == smudge) {
                linesMirrored = tempLinesMirrored
                countLinesMirrored = index + indexAdjustment
            }
        }
        return Result(linesMirrored = linesMirrored, value = countLinesMirrored ?: minValue)
    }

    private fun extractLine(index: Int, axe: Axe): CharArray {
        return if (axe == Axe.VERTICAL) ground.map { it[index] }.toCharArray() else ground[index]
    }

    private fun calculateLastIndex(axe: Axe): Int {
        val indexAdjustment = 1
        val firstIndex = 0
        return (if (axe == Axe.VERTICAL) ground[firstIndex].size else ground.size) - indexAdjustment
    }

    private fun isValid(line1: CharArray, line2: CharArray, difference: Int): Int {
        val firstLine = line1.joinToString()
        val secondLine = line2.joinToString()
        var differenceToReturn = difference

        firstLine.forEachIndexed { index, character ->
            if (character != secondLine[index]) differenceToReturn++
        }
        return differenceToReturn
    }

    companion object {
        fun fromStrings(lines: List<String>): List<Ash> {
            val ashList = mutableListOf<Ash>()
            val tempList = mutableListOf<CharArray>()
            lines.forEachIndexed { index, line ->
                if (line.isEmpty() || index == lines.lastIndex) {
                    ashList.add(Ash(tempList.toList()))
                    tempList.clear()
                } else {
                    tempList.add(line.toCharArray())
                }
            }

            return ashList.toList()
        }
    }
}

class Result(val linesMirrored: Int, val value: Int)