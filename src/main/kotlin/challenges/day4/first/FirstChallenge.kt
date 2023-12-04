package challenges.day4.first

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun main() {
    val numberMonoDigitWithSpace = "  "
    val numberMonoDigitWithZero = " 0"
    val file = File("src/inputs/input-day-4.txt")
    val result = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
            .asSequence()
            .map { it.replace(numberMonoDigitWithSpace, numberMonoDigitWithZero) }
            .map(GameCard::fromString)
            .map(GameCard::calculateScore)
            .map(GameCard::score)
            .sum()
    }

    println(result)
}

data class GameCard(
    val name: String,
    val winnerNumbers: List<String>,
    val attemptNumbers: List<String>,
    var score: Int
) {
    companion object {
        fun fromString(line: String): GameCard {
            val delimiterNameCard = ": "
            val delimiterNumbersOfCard = " | "
            val delimiterNumbers = " "
            val indexOfName = 0
            val indexOfNumbers = 1
            val indexOfWinnerNumbers = 0
            val indexOfAttemptNumbers = 1
            val initialScore = 0

            val cardListString = line.split(delimiterNameCard)
            val nameOfCard = cardListString[indexOfName]
            val numbersOfCard = cardListString[indexOfNumbers].split(delimiterNumbersOfCard)
            val winnerNumbersList = numbersOfCard[indexOfWinnerNumbers].split(delimiterNumbers)
            val attemptNumberList = numbersOfCard[indexOfAttemptNumbers].split(delimiterNumbers)

            return GameCard(
                name = nameOfCard,
                winnerNumbers = winnerNumbersList,
                attemptNumbers = attemptNumberList,
                score = initialScore
            )
        }
    }

    fun calculateScore(): GameCard {
        val multiplier = 2
        val wins = winnerNumbers.intersect(attemptNumbers.toSet())
        var numberOfWins = wins.size

        if (numberOfWins > 0) {
            score++
            numberOfWins--

            while (numberOfWins > 0) {
                score *= multiplier
                numberOfWins--
            }
        }
        return this
    }
}