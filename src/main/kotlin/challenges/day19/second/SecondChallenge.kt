package challenges.day19.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun main() {
    val file = File("src/inputs/input-day-19.txt")
    var changeSection = false
    val instructionLinesList = mutableListOf<String>()
    val piecesLinesList = mutableListOf<String>()

    BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines().forEach {
            if (it.isEmpty()) {
                changeSection = true
            } else {
                if (!changeSection) instructionLinesList.add(it) else piecesLinesList.add(it)
            }
        }
    }
    instructionLinesList.map(Node::fromString)
    piecesLinesList.map(Piece::fromString)

    val piecesOk = Piece.listPieces.mapNotNull { evaluateNode(it, Node.nodeMap["in"]!!) }
    val result = piecesOk.sumOf { it.x + it.m + it.a + it.s }
}

fun evaluateCondition(piece: Piece, instruction: Instruction): Boolean {
    val littleThan = '<'
    val greatThan = '>'
    val value = when (instruction.paramToAnalyze) {
        'x' -> piece.x
        'm' -> piece.m
        'a' -> piece.a
        's' -> piece.s
        else -> 0
    }

    instruction.condition?.let {
        if (it == littleThan) return value < instruction.valueCondition!!
        if (it == greatThan) return value > instruction.valueCondition!!
    }
    return true
}

fun evaluateNode(piece: Piece, node: Node): Piece? {
    val rejected = "R"
    val accepted = "A"
    node.instructions.forEach {
        if (evaluateCondition(piece, it)) {
            return when (it.nextNode) {
                rejected -> null
                accepted -> piece
                else -> evaluateNode(piece, Node.nodeMap[it.nextNode]!!)
            }
        }
    }
    return null
}

data class Node(val instructions: List<Instruction>) {
    companion object {
        val nodeMap = mutableMapOf<String, Node>()

        fun fromString(line: String) {
            val delimiterOne = '{'
            val delimiterTwo = ','
            val delimiterThree = ':'
            val suffixToRemove = "}"
            val firstPosition = 0
            val secondPosition = 1

            val (stringKey, stringInstructions) = line.removeSuffix(suffixToRemove).split(delimiterOne)
            val instructions = stringInstructions.split(delimiterTwo)

            val instructionList = instructions.mapIndexed { index, instruction ->
                if (index != instructions.lastIndex) {
                    Instruction(
                        paramToAnalyze = instruction[firstPosition],
                        condition = instruction[secondPosition],
                        valueCondition = instruction.filter(Char::isDigit).toInt(),
                        nextNode = instruction.split(delimiterThree)[secondPosition]
                    )
                } else {
                    Instruction(
                        paramToAnalyze = null,
                        condition = null,
                        valueCondition = null,
                        nextNode = instruction
                    )
                }
            }
            nodeMap[stringKey] = Node(instructions = instructionList)
        }
    }
}

data class Instruction(val paramToAnalyze: Char?, val condition: Char?, val valueCondition: Int?, val nextNode: String)

data class Piece(val x: Int, val m: Int, val a: Int, val s: Int) {
    companion object {
        val listPieces = mutableListOf<Piece>()
        fun fromString(line: String) {
            val prefixToRemove = "{"
            val suffixToRemove = "}"
            val delimiterOne = ','
            val delimiterTwo = '='
            val indexValue = 1

            val stringPiece = line.removePrefix(prefixToRemove).removeSuffix(suffixToRemove)
            val (stringX, stringM, stringA, stringS) = stringPiece.split(delimiterOne)
            listPieces.add(
                Piece(
                    x = stringX.split(delimiterTwo)[indexValue].toInt(),
                    m = stringM.split(delimiterTwo)[indexValue].toInt(),
                    a = stringA.split(delimiterTwo)[indexValue].toInt(),
                    s = stringS.split(delimiterTwo)[indexValue].toInt()
                )
            )
        }
    }
}