package challenges.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

val listStringNumbers: List<String> = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

fun main() {
    val file = File("src/inputs/input.txt")
    val result = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
            .map(::replaceNumbersInOrder)
            .map(String::toCharArray)
            .map(::extractCharArrayNumericValue)
            .sum()
    }

    println(result)
}

fun replaceNumbersInOrder(string: String): String {
    val nonExistingIndex: Int = -1
    val mapFirstNumbers: MutableMap<String, Int> = mutableMapOf()

    listStringNumbers.forEach {
        mapFirstNumbers[it] = nonExistingIndex
    }

    val mapLastNumbers: MutableMap<String, Int> = mapFirstNumbers.toMutableMap()

    mapFirstNumbers.entries.forEach { entry ->
        val index = Regex(entry.key).findAll(string).map { it.range.first }.firstOrNull()
        mapFirstNumbers[entry.key] = index ?: nonExistingIndex
    }

    mapLastNumbers.entries.forEach { entry ->
        val index = Regex(entry.key).findAll(string).map { it.range.first }.lastOrNull()
        mapLastNumbers[entry.key] = index ?: nonExistingIndex
    }

    val firstNumber = mapFirstNumbers.filter { entry -> entry.value != nonExistingIndex }.minByOrNull { it.value }?.key
    val lastNumber = mapLastNumbers.filter { entry -> entry.value != nonExistingIndex }.maxByOrNull { it.value }?.key

    return replaceWrittenNumbersToDigits(string, firstNumber, lastNumber)
}

fun replaceWrittenNumbersToDigits(string: String, firstReplace: String?, lastReplace: String?): String {
    val mapNumbers: MutableMap<String, String> = mutableMapOf()
    val indexAdjustment = 1

    listStringNumbers.forEachIndexed { index, stringNumber ->
        mapNumbers[stringNumber] = "${index + indexAdjustment}"
    }

    var result: String = string

    firstReplace?.let {
        result = result.replaceFirst(it, "${mapNumbers.getValue(it)}$it")
    }

    lastReplace?.let {
        result = result.replaceLast(it, "$it${mapNumbers.getValue(it)}")
    }

    return result
}

fun extractCharArrayNumericValue(charArray: CharArray): Int {
    val numbersList: List<Char> = charArray.filter(Char::isDigit).toList()
    val firstNumber = numbersList.first()
    val lastNumber = numbersList.last()

    return "$firstNumber$lastNumber".toInt()
}

fun String.replaceLast(oldValue: String, newValue: String, ignoreCase: Boolean = false): String {
    val index = lastIndexOf(oldValue, ignoreCase = ignoreCase)
    return if (index < 0) this else this.replaceRange(index, index + oldValue.length, newValue)
}