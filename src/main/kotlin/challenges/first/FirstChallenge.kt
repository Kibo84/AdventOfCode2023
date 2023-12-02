package challenges.first

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun main() {
    val file = File("src/inputs/input-day-1.txt")
    val result = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
            .map(String::toCharArray)
            .map(::extractCharArrayNumericValue)
            .sum()
    }

    println(result)
}

fun extractCharArrayNumericValue(charArray: CharArray): Int {
    val numbersList: List<Char> = charArray.filter(Char::isDigit).toList()

    return "${ numbersList.first() }${ numbersList.last() }".toInt()
}