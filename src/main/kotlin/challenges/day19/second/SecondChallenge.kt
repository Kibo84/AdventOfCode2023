package challenges.day19.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

val pieceRangeList = mutableListOf<Piece?>()

fun main() {
    val file = File("src/inputs/input-testing.txt")
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
    val initialRange = 1 .. 4000

    val firstPiece = Piece(
        x = initialRange,
        m = initialRange,
        a = initialRange,
        s = initialRange
    )
    evaluateNode(firstPiece, Node.nodeMap["in"]!!)
    val result = pieceRangeList.filterNotNull().map(::println)

    println(result)
}

fun evaluateCondition(piece: Piece, instruction: Instruction): Pair<Piece?, Piece?> {
    val littleThan = '<'
    val greatThan = '>'
    val range = when (instruction.paramToAnalyze) {
        'x' -> piece.x
        'm' -> piece.m
        'a' -> piece.a
        's' -> piece.s
        else -> 0..0
    }
    var rangeOne: IntRange = 0..0
    var rangeTwo: IntRange = 0..0

    instruction.condition?.let {
        if (it == littleThan) {
            rangeOne = range.first ..< instruction.valueCondition!!
            rangeTwo = instruction.valueCondition.. range.last
        }
        if (it == greatThan) {
            rangeOne = instruction.valueCondition!!.. range.last
            rangeTwo = range.first ..< instruction.valueCondition
        }

        val pieceOne = Piece(
            x = if (instruction.paramToAnalyze == 'x') rangeOne else piece.x,
            m = if (instruction.paramToAnalyze == 'm') rangeOne else piece.m,
            a = if (instruction.paramToAnalyze == 'a') rangeOne else piece.a,
            s = if (instruction.paramToAnalyze == 's') rangeOne else piece.s,
        )

        val pieceTwo = Piece(
            x = if (instruction.paramToAnalyze == 'x') rangeTwo else piece.x,
            m = if (instruction.paramToAnalyze == 'm') rangeTwo else piece.m,
            a = if (instruction.paramToAnalyze == 'a') rangeTwo else piece.a,
            s = if (instruction.paramToAnalyze == 's') rangeTwo else piece.s,
        )

        return Pair(pieceOne, pieceTwo)
    }

    return Pair(piece, null)
}

fun evaluateNode(piece: Piece, node: Node): Piece? {
    val rejected = "R"
    val accepted = "A"
    var tempPiece = piece
    node.instructions.forEach { instruction ->
        val pairPieces = evaluateCondition(tempPiece, instruction)
        when (instruction.nextNode) {
            rejected -> pieceRangeList.add(null)
            accepted -> pieceRangeList.add(pairPieces.first)
            else -> {
                pairPieces.second?.let { tempPiece = it }
                evaluateNode(pairPieces.first!!, Node.nodeMap[instruction.nextNode]!!)
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

data class Piece(val x: IntRange, val m: IntRange, val a: IntRange, val s: IntRange) {
    fun calculatePosibilities(): Long {
        val xPossibilities = (x.last - x.first).toLong()
        val mPossibilities = (m.last - m.first).toLong()
        val aPossibilities = (a.last - a.first).toLong()
        val sPossibilities = (s.last - s.first).toLong()

        return xPossibilities * mPossibilities * aPossibilities * sPossibilities
    }
}