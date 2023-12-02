package challenges.day1.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

val listStringNumbers: List<String> = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

fun main() {
    val file = File("src/inputs/input-day-1.txt")
    val result = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
            .map(::replaceWrittenNumbersToDigits)
            .map(String::toCharArray)
            .map(::extractCharArrayNumericValue)
            .sum()
    }

    println(result)
}

fun replaceWrittenNumbersToDigits(line: String): String {
    val indexAdjustment = 1
    val firstIndex = 0
    var result: String = line

    listStringNumbers.forEachIndexed { index, textNumber ->
        result = result.replace(
            textNumber,
            "${ textNumber[firstIndex] }${ index + indexAdjustment }${ textNumber[textNumber.lastIndex] }"
        )
    }

    return result
}

fun extractCharArrayNumericValue(charArray: CharArray): Int {
    val numbersList: List<Char> = charArray.filter(Char::isDigit).toList()

    return "${ numbersList.first() }${ numbersList.last() }".toInt()
}