package challenges.day15.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

const val delimiter = ','

fun main() {
    val file = File("src/inputs/input-day-15.txt")

    val input = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLine().split(delimiter)
    }

    val map = executeInstructions(input)

    val result = map.entries.map(::calculatePower).sum()

    println(result)
}

fun executeInstructions(instructions: List<String>): Map<Int, MutableMap<String, Int>> {
    val removeLens = '-'
    val insertLens = '='
    val map: Map<Int, MutableMap<String, Int>> = (0..255).associateWith { mutableMapOf() }
    instructions.map(::instructionsFactory).map { it.executeInstruction(map) }

    return map
}

fun calculatePower(entry: Map.Entry<Int, MutableMap<String, Int>>): Int {
    val indexAdjustment = 1
    return entry.value.values.mapIndexed { index, value ->
        (entry.key + indexAdjustment) * (index + indexAdjustment) * value
    }.sum()
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

interface Instruction {
    fun executeInstruction(
        map: Map<Int, MutableMap<String, Int>>
    ): Map<Int, MutableMap<String, Int>>
}

class RemoveInstruction(private val instruction: String) : Instruction {
    override fun executeInstruction(
        map: Map<Int, MutableMap<String, Int>>
    ): Map<Int, MutableMap<String, Int>> {
        val delimiterInstruction = "-"
        val label = instruction.removeSuffix(delimiterInstruction)
        val box = hashAlgorithm(label)
        map[box]?.remove(label)

        return map
    }
}

class InsertInstruction(private val instruction: String) : Instruction {
    override fun executeInstruction(
        map: Map<Int, MutableMap<String, Int>>
    ): Map<Int, MutableMap<String, Int>> {
        val delimiterInstruction = "="
        val (label, lens) = instruction.split(delimiterInstruction)
        val box = hashAlgorithm(label)
        map[box]?.set(label, lens.toInt())

        return map
    }
}

fun instructionsFactory(instruction: String): Instruction {
    val delimiterInstructionRemove = "-"
    return if (instruction.contains(delimiterInstructionRemove)) {
        RemoveInstruction(instruction)
    } else {
        InsertInstruction(instruction)
    }
}