package challenges.day20.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import challenges.day20.second.Pulses.*
import challenges.day8.second.isZero

enum class Pulses { HIGH, LOW }
val pileOfPulses = mutableListOf<Triple<String, Pulses, String>>()
const val exitModule = "rx"

fun main() {
    val file = File("src/inputs/input-day-20.txt")

    val input = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
    }
    val moduleList = input.map(::moduleFactory)
    val moduleMap = moduleList.associateBy { it.name }
    moduleList.map { module ->
        val filteredModules = moduleMap.filterKeys { it in module.listNames }
        module.prepareConnections(modules = filteredModules)
    }

    val previousExitModule = searchModuleWithExitOutput(exitModule, moduleList)
    val result = if (previousExitModule::class == ConjunctionModule::class) {
        calculatePulsationsToSendLowPulseToExit(previousExitModule as ConjunctionModule, moduleMap)
    } else {
        calculatePulsationsToSendLowPulseToExit(previousExitModule as FlipFlopModule, moduleMap)
    }
    println(result)
}

fun searchModuleWithExitOutput(exitOutput: String, listModules: List<Module>): Module {
    return listModules.first { it.listNames.contains(exitOutput) }
}

fun calculatePulsationsToSendLowPulseToExit(
    previousModuleToExit: ConjunctionModule,
    moduleMap: Map<String, Module>
): Long {
    val mapConditions = previousModuleToExit.inputModules.keys.associateWith { 0L }.toMutableMap()
    var pulsations = 0L
    var exit = false
    while(!exit) {
        pulsations++
        pileOfPulses.add(Triple("broadcaster", LOW, ""))
        while (pileOfPulses.isNotEmpty()) {
            val instruction = pileOfPulses.removeFirst()
            if (
                instruction.first == previousModuleToExit.name
                && mapConditions[instruction.third] == 0L
                && instruction.second == HIGH
            ) {
                mapConditions[instruction.third] = pulsations
            }
            if (mapConditions.values.all { it != 0L }) exit = true
            moduleMap[instruction.first]?.receivePulse(instruction.second, instruction.third)
        }
    }
    return calculateLeastCommonMultipleOfList(mapConditions.values.toList())
}

fun calculatePulsationsToSendLowPulseToExit(
    previousModuleToExit: FlipFlopModule,
    moduleMap: Map<String, Module>
): Long {
    var pulsations = 0L
    var exit = false
    while(!exit) {
        pulsations++
        pileOfPulses.add(Triple("broadcaster", LOW, ""))
        while (!exit && pileOfPulses.isNotEmpty()) {
            val instruction = pileOfPulses.removeFirst()
            moduleMap[instruction.first]?.receivePulse(instruction.second, instruction.third)
            if (moduleMap[instruction.first] == previousModuleToExit &&
                previousModuleToExit.stateOn && instruction.second == HIGH) {
                exit = true
            }
        }
    }
    return pulsations
}

fun moduleFactory(line: String): Module {
    val flipFlopIdentifier = "%"
    val conjunctionIdentifier = "&"
    val delimiter = " -> "
    val delimiterOutputs = ", "

    val type = if (line.startsWith(flipFlopIdentifier)) {
        "%"
    } else if (line.startsWith(conjunctionIdentifier)) {
        "&"
    } else {
        "broadcaster"
    }
    var (name, listNameOutputs) = line.split(delimiter)
    name = name.removePrefix(type)
    return when (type) {
        "%" -> FlipFlopModule(
            name = name,
            listNames = listNameOutputs.split(delimiterOutputs)
        )
        "&" -> ConjunctionModule(
            name = name,
            listNames = listNameOutputs.split(delimiterOutputs)
        )
        else -> BroadcasterModule(
            name = "broadcaster",
            listNames = listNameOutputs.split(delimiterOutputs)
        )
    }
}

interface Module {
    val name: String
    var sentPulses: Int
    val listNames: List<String>
    fun receivePulse(pulse: Pulses, nameNode: String)
    fun prepareConnections(modules: Map<String, Module>)
}

data class FlipFlopModule(
    override val name: String,
    var stateOn: Boolean = false,
    var outputModules: Map<String, Module> = mutableMapOf(),
    override val listNames: List<String>
) : Module {
    override var sentPulses: Int = 0

    override fun receivePulse(pulse: Pulses, nameNode: String) {
        if (pulse == HIGH) return
        stateOn = !stateOn
        listNames.forEach {
            pileOfPulses.add(Triple(it, if (stateOn) HIGH else LOW, name))
        }
    }

    override fun prepareConnections(modules: Map<String, Module>) {
        outputModules = modules
        outputModules.filter { it.value is ConjunctionModule }
            .forEach { module -> (module.value as ConjunctionModule).receiveInputInfo(name) }
    }

    override fun toString(): String {
        return "FlipFlopModule(name=$name, outputs=${outputModules.keys})"
    }
}

data class ConjunctionModule(
    override val name: String,
    var outputModules: Map<String, Module> = mutableMapOf(),
    val inputModules: MutableMap<String, Pulses> = mutableMapOf(),
    override val listNames: List<String>
) : Module {
    override var sentPulses: Int = 0
    override fun receivePulse(pulse: Pulses, nameNode: String) {
        inputModules[nameNode] = pulse
        listNames.forEach {
            pileOfPulses.add(
                Triple(it, if (inputModules.values.all { pulse -> pulse == HIGH }) LOW else HIGH, name)
            )
        }
    }

    override fun prepareConnections(modules: Map<String, Module>) {
        outputModules = modules
        outputModules.filter { it.value is ConjunctionModule }
            .forEach { module -> (module.value as ConjunctionModule).receiveInputInfo(name) }
    }

    fun receiveInputInfo(name: String) {
        inputModules[name] = LOW
    }

    override fun toString(): String {
        return "ConjunctionModule(name=$name, inputs=${inputModules.keys}, outputs=${outputModules.keys})"
    }

}

data class BroadcasterModule(
    override val name: String,
    var receivedPulse: Pulses = LOW,
    var outputModules: Map<String, Module> = mutableMapOf(),
    override val listNames: List<String>
) : Module {
    override var sentPulses: Int = 0
    override fun receivePulse(pulse: Pulses, nameNode: String) {
        receivedPulse = pulse
        listNames.forEach {
            pileOfPulses.add(Triple(it, pulse, name))
        }
    }

    override fun prepareConnections(modules: Map<String, Module>) {
        outputModules = modules
        outputModules.filter { it.value is ConjunctionModule }
            .forEach { module -> (module.value as ConjunctionModule).receiveInputInfo(name) }
    }

    override fun toString(): String {
        return "BroadcasterModule(name=$name, outputs=${outputModules.keys})"
    }
}

fun calculateLeastCommonMultipleOfList(buttonPulsations: List<Long>): Long {
    val firstIndex = 0
    var result = buttonPulsations[firstIndex]
    buttonPulsations.forEachIndexed { index, _ ->
        result = calculateLeastCommonMultiple(result, buttonPulsations[index])
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