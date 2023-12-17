package challenges.day15.first

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

const val delimiter = ','

fun main() {
    val file = File("src/inputs/input-day-15.txt")

    val result = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLine().split(delimiter).map(::hashAlgorithm).sum()
    }

    println(result)
}

fun hashAlgorithm(string: String): Int {
    val multiplier = 17
    val divider = 256
    var result = 0

    string.toByteArray().forEach {
        var tempValue = result + it
        tempValue *= multiplier
        result = tempValue % divider
    }

    return result
}