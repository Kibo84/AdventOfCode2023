package challenges.day2.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun main() {
    val file = File("src/inputs/input-day-2.txt")
    val result = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
            .map(Game.Companion::fromString)
            .map(Game::power)
            .sum()
    }

    println(result)
}

data class Game(
    val number: Int,
    val rounds: List<Round>,
    var minBagConfiguration: Round = Round(red = null, green = null, blue = null),
    var power: Int = 0
) {
    companion object {
        fun fromString(writtenGame: String): Game {
            val gameIndex = 0
            val gameNumberIndex = 1
            val roundsIndex = 1

            val gameStringList = writtenGame.split(": ")
            val number = gameStringList[gameIndex].split(" ")[gameNumberIndex].toInt()
            val rounds = gameStringList[roundsIndex].split("; ").map(Round.Companion::fromString)
            val minBagConfiguration = calculateMinBagConfiguration(rounds)
            val power = calculatePower(minBagConfiguration)

            return Game(
                number = number,
                rounds = rounds,
                minBagConfiguration = minBagConfiguration,
                power = power
            )
        }

        private fun calculateMinBagConfiguration(rounds: List<Round>): Round {
            var red = 0
            var green = 0
            var blue = 0

            rounds.forEach { round ->
                round.red?.let { if (it > red) red = it }
                round.green?.let { if (it > green) green = it }
                round.blue?.let { if (round.blue > blue) blue = round.blue }
            }

            return Round(red = red, green = green, blue = blue)
        }

        private fun calculatePower(minBagConfiguration: Round): Int {
            return (minBagConfiguration.red ?: 1) * (minBagConfiguration.green ?: 1) * (minBagConfiguration.blue ?: 1)
        }
    }
}

data class Round(val red: Int?, val green: Int?, val blue: Int?) {
    companion object {
        fun fromString(writtenRound: String): Round {
            val numberPosition = 0
            val colorList = writtenRound.split(", ")
            val red = colorList.firstOrNull { it.contains("red") }
            val green = colorList.firstOrNull { it.contains("green") }
            val blue = colorList.firstOrNull { it.contains("blue") }

            return Round(
                red = red?.split(" ")?.get(numberPosition)?.toInt(),
                green = green?.split(" ")?.get(numberPosition)?.toInt(),
                blue = blue?.split(" ")?.get(numberPosition)?.toInt()
            )
        }
    }
}