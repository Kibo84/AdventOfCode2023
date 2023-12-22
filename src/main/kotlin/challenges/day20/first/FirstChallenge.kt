package challenges.day20.first

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import challenges.day20.first.Pulses.*

enum class Pulses { HIGH, LOW }
val pileOfPulses = mutableListOf<Triple<String, Pulses, String>>()

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

    var lowPulses = 0
    var highPulses = 0
    var pulsations = 1000
    while(pulsations > 0) {
        pulsations--
        pileOfPulses.add(Triple("broadcaster", LOW, ""))
        while (pileOfPulses.isNotEmpty()) {
            val instruction = pileOfPulses.removeFirst()
            if (instruction.second == LOW) lowPulses++ else highPulses++
            moduleMap[instruction.first]?.receivePulse(instruction.second, instruction.third)
        }
    }
    println(lowPulses * highPulses)
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