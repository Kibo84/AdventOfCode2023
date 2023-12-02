package challenges.third

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

val bagConfiguration: Round = Round(red = 12, green = 13, blue = 14)

fun main() {
    val file = File("src/inputs/input-day-2.txt")
    val result = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
            .map(Game::fromString)
            .filter(Game::isValidGame)
            .map(Game::number)
            .sum()
    }

    println(result)
}

data class Game(val number: Int, val rounds: List<Round>) {
    companion object {
        fun fromString(writtenGame: String): Game {
            val gameIndex = 0
            val gameNumberIndex = 1
            val roundsIndex = 1

            val gameStringList = writtenGame.split(": ")
            val number = gameStringList[gameIndex].split(" ")[gameNumberIndex].toInt()
            val rounds = gameStringList[roundsIndex].split("; ").map(Round::fromString)

            return Game(number = number, rounds)
        }
    }
    fun isValidGame(): Boolean {
        return this.rounds.all(Round::isValidRound)
    }
}

data class Round(val red: Int = 0, val green: Int = 0, val blue: Int = 0) {
    companion object {
        fun fromString(writtenRound: String): Round {
            val numberPosition = 0
            val noCubeOfColor = 0
            val colorList = writtenRound.split(", ")
            val red = colorList.firstOrNull { it.contains("red") }
            val green = colorList.firstOrNull { it.contains("green") }
            val blue = colorList.firstOrNull { it.contains("blue") }

            return Round(
                red = red?.split(" ")?.get(numberPosition)?.toInt() ?: noCubeOfColor,
                green = green?.split(" ")?.get(numberPosition)?.toInt() ?: noCubeOfColor,
                blue = blue?.split(" ")?.get(numberPosition)?.toInt() ?: noCubeOfColor
            )
        }
    }
    fun isValidRound(): Boolean {
        return this.red <= bagConfiguration.red
                && this.green <= bagConfiguration.green
                && this.blue <= bagConfiguration.blue
    }
}