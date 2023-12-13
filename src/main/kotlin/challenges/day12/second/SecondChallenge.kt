package challenges.day12.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

const val incognita = '?'
const val damage = '#'
const val operational = '.'

fun main() {
    val file = File("src/inputs/input-day-12.txt")

    val lines = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
    }

    val result = lines.map(::unfold).map(::solutions).sum()

    println(result)
}

fun solutions(spring: Spring): Long {
    val initialGroupId = 0
    val initialGroupAmount = 0
    val initialPermutationCount = 1L
    val defaultValue = 0L
    val incrementId = 1
    val permutations = mutableMapOf(PermutationKey(initialGroupId, initialGroupAmount) to initialPermutationCount)
    spring.spring.forEach { character ->
        val nextGroup = mutableListOf<Triple<Int, Int, Long>>()
        permutations.forEach { entry ->
            val groupAmount = entry.key.groupAmount
            val groupId = entry.key.groupId
            val permCount = entry.value
            if (character != damage) {
                if (groupAmount == initialGroupAmount) {
                    nextGroup.add(Triple(groupId, groupAmount, permCount))
                } else if (groupAmount == spring.conditions[groupId]) {
                    nextGroup.add(Triple(groupId + incrementId, initialGroupAmount, permCount))
                }
            }
            if (character != operational && groupId < spring.conditions.size
                && groupAmount < spring.conditions[groupId]
                ) {
                nextGroup.add(Triple(groupId, groupAmount + incrementId, permCount))
            }
        }
        permutations.clear()
        nextGroup.forEach { (groupId, groupAmount, permCount) ->
            permutations[PermutationKey(groupId, groupAmount)] =
                permutations.getOrDefault(PermutationKey(groupId, groupAmount), defaultValue) + permCount
        }
    }
    return permutations.filterKeys { isValid(it.groupId, it.groupAmount, spring) }.values.sum()
}

fun unfold(line: String): Spring {
    val springConfigDelimiter = ' '
    val configsDelimiter = ','
    val repeats = 5
    val (springs, config) = line.split(springConfigDelimiter)
    val springConfig = config.split(configsDelimiter).map(String::toInt)
    val modSprings = List(repeats) { springs }
    val modConfig = mutableListOf<Int>()
    repeat(repeats) { modConfig.addAll(springConfig) }
    return Spring(modSprings.joinToString(incognita.toString()), modConfig.toList())
}

data class Spring(val spring: String, val conditions: List<Int>)

data class PermutationKey(var groupId: Int, var groupAmount: Int)

fun isValid(groupId: Int, groupAmount: Int, spring: Spring): Boolean {
    val indexAdjustment = 1
    return groupId == spring.conditions.size
            || groupId == spring.conditions.size - indexAdjustment && groupAmount == spring.conditions[groupId]
}