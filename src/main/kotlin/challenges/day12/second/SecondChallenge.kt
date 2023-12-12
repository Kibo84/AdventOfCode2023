package challenges.day12.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

const val incognita = '?'
const val damage = '#'
const val operational = '.'

fun main() {
    val file = File("src/inputs/input-day-12.txt")

    val lines = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
    }
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
    val (arrange, damaged) = arrangedSprings
    val possibilities = replaceAllIncognita(listOf(arrange), damaged)

    return possibilities.count { regex.matches(it) }
}

fun replaceAllIncognita(possibilities: List<String>, damaged: MutableList<Int>): List<String> {
    val copyList = mutableListOf<String>()
    val damageRepeats = damaged.removeFirst()
    val stringToSearch = "[#\\?]{$damageRepeats}"
    val stringToReplace = damage.toString().repeat(damageRepeats)

    possibilities.forEach { possibility ->
        val listIndex = findAllIndices(possibility, stringToSearch)
        listIndex.forEach { index ->
            val modifyString = possibility.replaceFromIndex(stringToSearch, stringToReplace, index)
            modifyString?.let { copyList.add(it) }
        }
    }

    if (damaged.isEmpty()) {
        copyList.map { it.replace(incognita, operational) }.toList().map(::println)
        return copyList
    } else {
        return replaceAllIncognita(copyList, damaged)
    }
}

fun findAllIndices(mainString: String, searchString: String): List<Int> {
    val indices = mutableListOf<Int>()
    val regex = searchString.toRegex()
    val matches = regex.findAll(mainString)

    for (match in matches) {
        indices.add(match.range.first)
    }

    return indices
}

fun String.replaceFromIndex(targetSequence: String, replacement: String, index: Int): String? {
    if (index < 0 || index >= this.length) {
        return null
    }
    val prefix = this.substring(0, index)
    val suffix = this.substring(index)
    val regex = targetSequence.toRegex()

    return prefix + regex.replaceFirst(suffix, replacement)
}

data class ArrangedSprings(val arrange: String, val damaged: MutableList<Int>)