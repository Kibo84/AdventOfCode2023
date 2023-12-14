package challenges.day13.first

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun main() {
    val file = File("src/inputs/input-day-13.txt")

    val lines = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
    }

    val ashes = Ash.fromStrings(lines)
    val result = ashes.map(Ash::calculate).sum()
    println(result)
}

fun isValid(line1: CharArray, line2: CharArray) = line1.contentEquals(line2)

data class Ash(val ground: List<CharArray>) {

    fun calculate(): Int {
        val pairHorizontal = calculateHorizontally()
        val pairVertical = calculateVertically()

        return if (pairHorizontal.first > pairVertical.first) pairHorizontal.second else pairVertical.second
    }

    private fun calculateHorizontally(): Pair<Int, Int> {
        val zero = 0
        val indexAdjustment = 1
        val firstIndex = 0
        val lastIndex = ground.lastIndex
        val multiplier = 100
        var linesMirrored = 0
        var isMirror: Boolean
        var indexToReturn: Int? = null
        var result = 0

        ground.forEachIndexed { index, line ->
            var tempLinesMirrored = 0
            if (index != ground.lastIndex) {
                var nextIndex = index + indexAdjustment
                var nextLine = ground[nextIndex]
                if (isValid(line, nextLine)) {
                    tempLinesMirrored++
                    isMirror = true
                    var previousIndex = index - indexAdjustment
                    nextIndex++
                    while (isMirror && previousIndex >= firstIndex && nextIndex <= lastIndex) {
                        val previousLine = ground[previousIndex]
                        nextLine = ground[nextIndex]
                        isMirror = isValid(previousLine, nextLine)
                        if (isMirror) {
                            tempLinesMirrored++
                            previousIndex--
                            nextIndex++
                        }
                    }
                    isMirror = false
                    if (tempLinesMirrored > linesMirrored) {
                        linesMirrored = tempLinesMirrored
                        indexToReturn = index + indexAdjustment
                    }
                }
            }
        }
        indexToReturn?.let { result = it * multiplier }

        return Pair(linesMirrored, result)
    }

    private fun calculateVertically(): Pair<Int, Int> {
        val zero = 0
        val indexAdjustment = 1
        val firstIndex = 0
        val lastIndex = ground[0].size - indexAdjustment
        var linesMirrored = 0
        var isMirror: Boolean
        var indexToReturn: Int? = null
        var result = 0

        for (index in firstIndex .. lastIndex) {
            var tempLinesMirrored = 0
            if (index != lastIndex) {
                var nextIndex = index + indexAdjustment
                var line1 = ground.map { it[index] }.toCharArray()
                var line2 = ground.map { it[nextIndex] }.toCharArray()
                if (isValid(line1, line2)) {
                    tempLinesMirrored++
                    isMirror = true
                    var previousIndex = index - indexAdjustment
                    nextIndex++
                    while (isMirror && previousIndex >= firstIndex && nextIndex <= lastIndex) {
                        line1 = ground.map { it[previousIndex] }.toCharArray()
                        line2 = ground.map { it[nextIndex] }.toCharArray()
                        isMirror = isValid(line1, line2)
                        if (isMirror) {
                            tempLinesMirrored++
                            previousIndex--
                            nextIndex++
                        }
                    }
                    isMirror = false
                    if (tempLinesMirrored > linesMirrored) {
                        linesMirrored = tempLinesMirrored
                        indexToReturn = index + indexAdjustment
                    }
                }
            }
        }
        indexToReturn?.let { result = it }

        return Pair(linesMirrored, result)
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