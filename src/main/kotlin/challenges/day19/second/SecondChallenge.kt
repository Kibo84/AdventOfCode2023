package challenges.day19.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.math.pow

var totalPossibilities = 0L

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

    val initialPossibilities = 4000.0.pow(4).toLong()
    evaluateNode(initialPossibilities, Node.nodeMap["in"]!!)

    println(totalPossibilities)
}

fun evaluateCondition(possibilities: Long, instruction: Instruction): Long {
    val littleThan = '<'
    var newPossibilities = possibilities

    instruction.condition?.let {
        val percentage = if (it == littleThan) {
            (instruction.valueCondition!! - 1).toDouble() / 4000
        } else {
            (4000 - instruction.valueCondition!! + 1).toDouble() / 4000
        }

        newPossibilities = (possibilities * percentage).toLong()
    }

    return newPossibilities
}

fun evaluateNode(possibilities: Long, node: Node) {
    val rejected = "R"
    val accepted = "A"
    var tempPossibility = possibilities
    node.instructions.forEach { instruction ->
        val newPossibility = evaluateCondition(tempPossibility, instruction)
        tempPossibility -= newPossibility
        when (instruction.nextNode) {
            rejected -> return
            accepted -> totalPossibilities += newPossibility
            else -> {
                println("${instruction.nextNode}: $newPossibility")
                evaluateNode(newPossibility, Node.nodeMap[instruction.nextNode]!!)
            }
        }
    }
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