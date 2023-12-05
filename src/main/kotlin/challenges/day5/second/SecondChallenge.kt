package challenges.day5.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun main() {
    val mappersCollection: MutableMap<String, MutableList<SeedMapper>> = mutableMapOf()
    var lineSeeds = ""

    mappersCollection["soilList"] = mutableListOf()
    mappersCollection["fertilizerList"] = mutableListOf()
    mappersCollection["waterList"] = mutableListOf()
    mappersCollection["lightList"] = mutableListOf()
    mappersCollection["temperatureList"] = mutableListOf()
    mappersCollection["humidityList"] = mutableListOf()
    mappersCollection["locationList"] = mutableListOf()

    var actualMapper = ""

    val file = File("src/inputs/input-day-5.txt")
    BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines().filter { it.isNotEmpty() }.forEach {
            if (it.contains("seeds:")) lineSeeds = it
            if (it.contains("-to-")) actualMapper = extractMapperName(it)
            if (it.first().isDigit()) mappersCollection["${actualMapper}List"]?.add(SeedMapper.fromString(it))
        }
    }

    val result = Seed.calculateMin(lineSeeds, mappersCollection)

    println(result)
}

fun extractMapperName(line: String): String {
    val mapperNameContainerDelimiter = "-to-"
    val nameDelimiter = " "
    val nameContainerIndex = 1
    val nameIndex = 0

    return line.split(mapperNameContainerDelimiter)[nameContainerIndex].split(nameDelimiter)[nameIndex]
}

data class Seed(
    val seed: Long,
    var soil: Long,
    var fertilizer: Long,
    var water: Long,
    var light: Long,
    var temperature: Long,
    var humidity: Long,
    var location: Long
) {
    companion object {
        fun calculateMin(line: String, mappersCollection: MutableMap<String, MutableList<SeedMapper>>): Long {
            val seedsIndex = 1
            val titleDelimiter = ": "
            val seedsDelimiter = " "
            val writtenSeeds = line.split(titleDelimiter)[seedsIndex]
            var min = Long.MAX_VALUE

            val listLong = writtenSeeds.split(seedsDelimiter).map { it.toLong() }

            listLong.forEachIndexed { index, long ->
                if (index % 2 == 0) {
                    for (number in long .. long + listLong[index + 1]) {
                        val seed = fromLongInput(number)
                        seed.processSeed(mappersCollection)
                        if (seed.location < min) min = seed.location
                    }
                }
            }

            return min
        }

        private fun fromLongInput(input: Long): Seed {
            return Seed(
                seed = input,
                soil = input,
                fertilizer = input,
                water = input,
                light = input,
                temperature = input,
                humidity = input,
                location = input
            )
        }
    }
    fun processSeed(mappersCollection: MutableMap<String, MutableList<SeedMapper>>): Seed {
        soil = calculateNewValue(seed, mappersCollection["soilList"]?.toList() ?: emptyList())
        fertilizer = calculateNewValue(soil, mappersCollection["fertilizerList"]?.toList() ?: emptyList())
        water = calculateNewValue(fertilizer, mappersCollection["waterList"]?.toList() ?: emptyList())
        light = calculateNewValue(water, mappersCollection["lightList"]?.toList() ?: emptyList())
        temperature = calculateNewValue(light, mappersCollection["temperatureList"]?.toList() ?: emptyList())
        humidity = calculateNewValue(temperature, mappersCollection["humidityList"]?.toList() ?: emptyList())
        location = calculateNewValue(humidity, mappersCollection["locationList"]?.toList() ?: emptyList())
        return this
    }
    private fun calculateNewValue(inputValue: Long, mappers: List<SeedMapper>): Long {
        var returnValue = inputValue

        val mapper = mappers.firstOrNull { inputValue in it.inputInit.rangeTo(it.inputInit + it.transformValue) }
        mapper?.let {
            returnValue += (mapper.outputInit - mapper.inputInit)
        }

        return returnValue
    }
}

data class SeedMapper(val inputInit: Long, val outputInit: Long, val transformValue: Long) {
    companion object {
        fun fromString(line: String): SeedMapper {
            val valuesDelimiter = " "
            val destinationRangeStartIndex = 0
            val sourceRangeStartIndex = 1
            val rangeLengthIndex = 2

            val lineValues = line.split(valuesDelimiter)

            return SeedMapper(
                inputInit = lineValues[sourceRangeStartIndex].toLong(),
                outputInit = lineValues[destinationRangeStartIndex].toLong(),
                transformValue = lineValues[rangeLengthIndex].toLong(),
            )
        }
    }
}