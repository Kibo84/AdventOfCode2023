package challenges.day8.first

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

val regexInstructions = Regex("^[LR]*$")
val nodeList: MutableList<Node> = mutableListOf()
const val firstNode = 0
const val leftInstruction = 'L'
const val rightInstruction = 'R'
const val indexAdjustment = 1
const val destiny = "ZZZ"
const val origin = "AAA"

fun main() {
    var instructions: String = ""
    val file = File("src/inputs/input-day-8.txt")

    val lines = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
    }
    lines.filter { it.isNotEmpty() }.forEach {
        if (it.contains(regexInstructions)) instructions = it
        else nodeList.add(Node.fromString(it))
    }
    var actualNode = nodeList.first { it.nodeName == origin }
    var result = 0
    var repeat = 0
    while (result == 0) {
        instructions.forEachIndexed { index, instruction ->
            var nextNode: Node? = null
            if (instruction == leftInstruction) nextNode = nodeList.first { it.nodeName == actualNode.nextLeft }
            if (instruction == rightInstruction) nextNode = nodeList.first { it.nodeName == actualNode.nextRight }
            nextNode?.let { actualNode = it }
            if (nextNode?.nodeName == destiny) result = index + indexAdjustment
        }
        if (result != 0) result += (instructions.length * repeat)
        repeat++
    }
    println(result)
}

data class Node(val nodeName: String, val nextLeft: String, val nextRight: String) {
    companion object {
        fun fromString(line: String): Node {
            val delimiterBetweenNodeMap = " = "
            val nodeNameIndex = 0
            val nodeMapIndex = 1
            val leftIndex = 0
            val rightIndex = 1
            val mapDelimiter = ", "
            val writtenNodes = line.split(delimiterBetweenNodeMap)

            val nodeName = writtenNodes[nodeNameIndex]
            val maps = writtenNodes[nodeMapIndex].split(mapDelimiter)

            return Node(
                nodeName = nodeName,
                nextLeft = maps[leftIndex].filter(Char::isLetter),
                nextRight = maps[rightIndex].filter(Char::isLetter)
            )
        }
    }
}