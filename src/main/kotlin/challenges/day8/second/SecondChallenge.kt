package challenges.day8.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

val regexInstructions = Regex("^[LR]*$")
val nodeMap: MutableMap<String, Node> = mutableMapOf()
const val leftInstruction = 'L'
const val indexAdjustment = 1
const val destiny = 'Z'
const val origin = 'A'

fun main() {
    var instructions = ""
    val file = File("src/inputs/input-day-8.txt")
    val lines = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
    }

    lines.filter { it.isNotEmpty() }.forEach {
        if (it.contains(regexInstructions)) {
            instructions = it
        }
        else {
            val node = Node.fromString(it)
            nodeMap[node.nodeName] = node
        }
    }
    val nodeRoutes = nodeMap.values.filter(Node::isOrigin)
    val result = calculateLeastCommonMultipleOfList(calculateAllRoutesSteps(instructions, nodeRoutes))

    println(result)
}

fun calculateAllRoutesSteps(instructions: String, nodeList: List<Node>): List<Long> {
    return nodeList.map { calculateRoutSteps(instructions, it) }
}

fun calculateRoutSteps(instructions: String, node: Node): Long {
    var result = 0L
    var repeat = 0
    var tempNode = node.copy()
    while (result.isZero()) {
        instructions.forEachIndexed { index, instruction ->
            tempNode = searchNextNode(tempNode, instruction)
            if (Node.arriveToDestiny(tempNode)) result = (index + indexAdjustment).toLong()
        }
        if (!result.isZero()) result += (instructions.length * repeat)
        repeat++
    }
    return result
}

fun searchNextNode(node: Node, instruction: Char): Node {
    return if (instruction == leftInstruction) nodeMap[node.nextLeft]!! else nodeMap[node.nextRight]!!
}

fun calculateLeastCommonMultipleOfList(routesSteps: List<Long>): Long {
    val firstIndex = 0
    var result = routesSteps[firstIndex]
    routesSteps.forEachIndexed { index, _ ->
        result = calculateLeastCommonMultiple(result, routesSteps[index])
    }

    return result
}

fun calculateGreatestCommonDivisor(numberA: Long, numberB: Long): Long {
    return if (numberB.isZero()) numberA else calculateGreatestCommonDivisor(numberB, numberA % numberB)
}

fun calculateLeastCommonMultiple(numberA: Long, numberB: Long): Long {
    val commonMultipleWithZero = 0L
    return if (numberA.isZero() || numberB.isZero()) {
        commonMultipleWithZero
    } else {
        numberA * numberB / calculateGreatestCommonDivisor(numberA, numberB)
    }
}

fun Long.isZero(): Boolean{
    return this == 0L
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
                nextLeft = maps[leftIndex].filter(Char::isLetterOrDigit),
                nextRight = maps[rightIndex].filter(Char::isLetterOrDigit)
            )
        }

        fun arriveToDestiny(node: Node) = node.nodeName.last() == destiny

        fun isOrigin(node: Node) = node.nodeName.last() == origin
    }
}