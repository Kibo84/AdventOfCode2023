package challenges.day3.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

const val indexAdjustment = 1
const val gearSymbol = '*'
fun main() {
    val file = File("src/inputs/input-day-3.txt")
    val listOfLines: List<String>
    var listOfPossibleMotorPiece: MutableList<PossibleMotorPiece> = mutableListOf()
    val listOfGears: MutableList<Gear> = mutableListOf()

    BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        listOfLines = fileReader.readLines()
    }

    listOfLines.forEachIndexed { index, line ->
        listOfPossibleMotorPiece.addAll(searchPossiblePieces(line, index))
        listOfGears.addAll(searchGears(line, index))
    }

    listOfPossibleMotorPiece = listOfPossibleMotorPiece.filter {
        isValidMotorPiece(
            possibleMotorPiece = it,
            previousLine = listOfLines.getOrNull(it.numberOfLine - indexAdjustment),
            line = listOfLines[it.numberOfLine],
            nextLine = listOfLines.getOrNull(it.numberOfLine + indexAdjustment)
        )
    }.toMutableList()

    val result = listOfGears.map { gearTouchPieces(it, listOfPossibleMotorPiece) }
        .filter { it.touchPieces.size >= 2 }
        .map(::calculatePowerOfGear)
        .sum()

    println(result)
}

fun searchGears(line: String, numberOfLine: Int): List<Gear> {
    val gearsList: MutableList<Gear> = mutableListOf()

    line.forEachIndexed { index, char ->
        if (char == gearSymbol) {
            gearsList.add(Gear(positionX = index, positionY = numberOfLine, touchPieces = mutableListOf()))
        }
    }

    return gearsList.toList()
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

    if (line.getOrNull(indexPrevious)?.equals(gearSymbol) == true
        || line.getOrNull(indexNext)?.equals(gearSymbol) == true) {
        return true
    }

    if (previousLine?.substring(initSubString, endSubString).stringContainsSymbol()) return true
    if (nextLine?.substring(initSubString, endSubString).stringContainsSymbol()) return true

    return false
}

fun String?.stringContainsSymbol(): Boolean {
    return this?.any { it == gearSymbol } ?: false
}

fun gearTouchPieces(gear: Gear, listPieces: List<PossibleMotorPiece>): Gear {
    listPieces.map {
        if (coordenateYInRange(gear.positionY, it.numberOfLine)
            && coordinateXInRange(gear.positionX, it.firstIndex, it.lastIndex)
        ) {
            gear.touchPieces.add(it)
        }
    }
    return gear
}

fun calculatePowerOfGear(gear: Gear): Int {
    var power = 1
    gear.touchPieces.forEach { power *= it.number.toInt() }
    return power
}

fun coordenateYInRange(coordinateOne: Int, coordinateTwo: Int): Boolean {
    return coordinateOne in coordinateTwo - 1 .. coordinateTwo + 1
}

fun coordinateXInRange(coordinateOne: Int, coordinateTwo: Int, coordinateThree: Int): Boolean {
    return coordinateOne in coordinateTwo - 1 .. coordinateThree + 1
}

data class PossibleMotorPiece(var number: String, var numberOfLine: Int, var firstIndex: Int, var lastIndex: Int)

data class Gear(val positionX: Int, val positionY: Int, var touchPieces: MutableList<PossibleMotorPiece>)