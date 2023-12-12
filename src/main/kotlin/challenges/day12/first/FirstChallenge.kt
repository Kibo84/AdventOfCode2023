package challenges.day12.first

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

const val incognita = '?'
const val damage = '#'
const val operational = '.'

fun main() {
    val file = File("src/inputs/input-testing.txt")

    val result = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines().map(ArrangedSprings::fromString).map(::calculatePossibilities).sum()
    }

    println(result)
}

fun createRegex(damagedRepeats: List<Int>): Regex {
    var stringRegex = "^\\.*"
    damagedRepeats.forEachIndexed { index, int ->
        stringRegex += if (index != damagedRepeats.lastIndex) "#{$int}\\.+" else "#{$int}\\.*\$"
    }

    return stringRegex.toRegex()
}
fun calculatePossibilities(arrangedSprings: ArrangedSprings): Int {
    val regex = createRegex(arrangedSprings.damaged)
    val modifyString = arrangedSprings.arrange.replace(incognita, operational)
    val possibilities = replaceAllIncognita(listOf(modifyString), arrangedSprings.damaged, regex)

    return possibilities
}

fun replaceAllIncognita(possibilities: List<String>, damaged: MutableList<Int>, regex: Regex): Int {
    val copyList = mutableListOf<String>()
    val damageRepeats = damaged.removeFirst()
    val stringToReplace = damage.toString().repeat(damageRepeats)

    possibilities.forEach { possibility ->
        val listIndex = possibility.indices.toList()
        listIndex.forEach { index ->
            val modifyString = possibility.replaceFromIndex(stringToReplace, index)
            modifyString?.let { copyList.add(it) }
        }
    }

    return if (damaged.isEmpty()) {
        copyList.filter { regex.matches(it) }.toSet().count()
    } else {
        replaceAllIncognita(copyList, damaged, regex)
    }
}

fun String.replaceFromIndex(replacement: String, index: Int): String? {
    val endIndex = index + replacement.length
    if (endIndex < 0 || endIndex > this.length) {
        return null
    }

    return this.replaceRange(index,  endIndex, replacement)
}

data class ArrangedSprings(val arrange: String, val damaged: MutableList<Int>) {
    companion object {
        fun fromString(line: String): ArrangedSprings {
            val delimiterSection = " "
            val delimiterNumbers = ","
            val arrangeIndex = 0
            val damagedIndex = 1

            val subStrings = line.split(delimiterSection)
            val arrange = subStrings[arrangeIndex]
            val damaged = subStrings[damagedIndex].split(delimiterNumbers).map(String::toInt)

            return ArrangedSprings(arrange, damaged.toMutableList())
        }
    }
}