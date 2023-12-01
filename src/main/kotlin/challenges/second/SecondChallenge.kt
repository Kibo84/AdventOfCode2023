package challenges.second

import challenges.first.extractCharArrayNumericValue
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

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
    val listStringNumbers: List<String> = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
    val nonExistingRange: Int = -1
    val mapFirstNumbers: MutableMap<String, Int> = mutableMapOf()

    listStringNumbers.forEach {
        mapFirstNumbers[it] = nonExistingRange
    }

    val mapLastNumbers: MutableMap<String, Int> = mapFirstNumbers.toMutableMap()

    mapFirstNumbers.entries.forEach { entre ->
        val index = Regex(entre.key).findAll(string).map { it.range.first }.firstOrNull()
        mapFirstNumbers[entre.key] = index ?: nonExistingRange
    }

    mapLastNumbers.entries.forEach { entre ->
        val index = Regex(entre.key).findAll(string).map { it.range.first }.lastOrNull()
        mapLastNumbers[entre.key] = index ?: -1
    }

    val firstNumber = mapFirstNumbers.filter { entre -> entre.value != nonExistingRange }.minByOrNull { it.value }?.key
    val lastNumber = mapLastNumbers.filter { entre -> entre.value != nonExistingRange }.maxByOrNull { it.value }?.key

    return replaceWrittenNumbersToDigits(string, firstNumber, lastNumber)
}

fun replaceWrittenNumbersToDigits(string: String, firstReplace: String?, lastReplace: String?): String {
    val mapNumbers: MutableMap<String, String> = mutableMapOf()
    mapNumbers["one"] = "1"
    mapNumbers["two"] = "2"
    mapNumbers["three"] = "3"
    mapNumbers["four"] = "4"
    mapNumbers["five"] = "5"
    mapNumbers["six"] = "6"
    mapNumbers["seven"] = "7"
    mapNumbers["eight"] = "8"
    mapNumbers["nine"] = "9"

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