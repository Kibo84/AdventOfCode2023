package challenges.day19.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun main() {
    val file = File("src/inputs/input-day-19.txt")
    var changeSection = false
    val instructionLinesList = mutableListOf<String>()

    BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines().forEach {
            if (it.isEmpty()) {
                changeSection = true
            } else {
                if (!changeSection) instructionLinesList.add(it)
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

    val result = evaluateInstructions(firstPiece, Node.nodeMap["in"]!!.instructions)

    println(result)
}

fun evaluateInstructions(piece: Piece?, instructions: MutableList<Instruction>): Long {
    piece?.let {
        val rejected = "R"
        val accepted = "A"
        val instruction = instructions.removeFirstOrNull()
        instruction?.let {
            val pairPieces = evaluateCondition(piece, instruction)
            if (instruction.nextNode == rejected) {
                return 0 + evaluateInstructions(pairPieces.second, instructions)
            }
            if (instruction.nextNode == accepted) {
                return pairPieces.first!!.calculatePossibilities() + evaluateInstructions(pairPieces.second, instructions)
            }
            if (instruction.valueCondition == null) {
                return evaluateInstructions(piece, Node.nodeMap[instruction.nextNode]!!.instructions)
            }
            return evaluateInstructions(pairPieces.first, Node.nodeMap[instruction.nextNode]!!.instructions) +
                    evaluateInstructions(pairPieces.second, instructions)
        }
    }
    return 0
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
            rangeOne = instruction.valueCondition!! + 1.. range.last
            rangeTwo = range.first .. instruction.valueCondition
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

data class Node(val instructions: MutableList<Instruction>) {
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
            nodeMap[stringKey] = Node(instructions = instructionList.toMutableList())
        }
    }
}

data class Instruction(val paramToAnalyze: Char?, val condition: Char?, val valueCondition: Int?, val nextNode: String)

data class Piece(val x: IntRange, val m: IntRange, val a: IntRange, val s: IntRange) {
    fun calculatePossibilities(): Long {
        return x.count().toLong() * m.count() * a.count() * s.count()
    }
}