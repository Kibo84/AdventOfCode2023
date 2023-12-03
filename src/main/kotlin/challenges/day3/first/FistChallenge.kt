package challenges.day3.first

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

const val indexAdjustment = 1
const val symbolInvalid = '.'
fun main() {
    val file = File("src/inputs/input-day-3.txt")
    val listOfLines: List<String>
    val listOfPossibleMotorPiece: MutableList<PossibleMotorPiece> = mutableListOf()

    BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        listOfLines = fileReader.readLines()
    }

    listOfLines.forEachIndexed { index, line ->
        listOfPossibleMotorPiece.addAll(searchPossiblePieces(line, index))
    }

    val result = listOfPossibleMotorPiece.filter {
            isValidMotorPiece(
                possibleMotorPiece = it,
                previousLine = listOfLines.getOrNull(it.numberOfLine - indexAdjustment),
                line = listOfLines[it.numberOfLine],
                nextLine = listOfLines.getOrNull(it.numberOfLine + indexAdjustment)
            )
        }.map(PossibleMotorPiece::number)
        .map(String::toInt)
        .sum()

    println(result)
}

fun searchPossiblePieces(line: String, numberOfLine: Int): List<PossibleMotorPiece> {
    var processingActualPiece = false
    var piece = ""
    var firstIndex: Int? = null
    var lastIndex: Int?
    val possibleMotorPieceList: MutableList<PossibleMotorPiece> = mutableListOf()

    line.forEachIndexed { index, char ->
        if (char.isDigit()) {
            piece += char
            if (!processingActualPiece) {
                firstIndex = index
                processingActualPiece = true
            }
            if (index == line.length - indexAdjustment) {
                lastIndex = index
                possibleMotorPieceList.add(
                    PossibleMotorPiece(
                        numberOfLine = numberOfLine,
                        number = piece,
                        firstIndex = firstIndex!!,
                        lastIndex = lastIndex!!
                    )
                )
            }
        } else {
            if (processingActualPiece) {
                lastIndex = index - indexAdjustment
                processingActualPiece = false
                possibleMotorPieceList.add(
                    PossibleMotorPiece(
                        numberOfLine = numberOfLine,
                        number = piece,
                        firstIndex = firstIndex!!,
                        lastIndex = lastIndex!!
                    )
                )
                piece = ""
                firstIndex = null
                lastIndex = null
            }
        }
    }

    return possibleMotorPieceList.toList()
}

fun isValidMotorPiece(
    possibleMotorPiece: PossibleMotorPiece,
    previousLine: String?,
    line: String,
    nextLine: String?
): Boolean {
    val indexPrevious = possibleMotorPiece.firstIndex - indexAdjustment
    val indexNext = possibleMotorPiece.lastIndex + indexAdjustment
    val initSubString = if (indexPrevious < 0) 0 else indexPrevious
    val endSubString = if (indexNext >= line.length) line.length - indexAdjustment else indexNext + indexAdjustment

    if (line.getOrNull(indexPrevious)?.equals(symbolInvalid) == false
        || line.getOrNull(indexNext)?.equals(symbolInvalid) == false) {
        return true
    }

    if (previousLine?.substring(initSubString, endSubString).stringContainsSymbol()) return true
    if (nextLine?.substring(initSubString, endSubString).stringContainsSymbol()) return true

    return false
}

fun String?.stringContainsSymbol(): Boolean {
    return this?.any { !it.isDigit() && it != symbolInvalid } ?: false
}

data class PossibleMotorPiece(var number: String, var numberOfLine: Int, var firstIndex: Int, var lastIndex: Int)