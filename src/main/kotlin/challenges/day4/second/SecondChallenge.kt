package challenges.day4.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun main() {
    val numberMonoDigitWithOneSpace = "  "
    val numberMonoDigitWithTwoSpace = "   "
    val numberMonoDigitWithZero = " 0"
    val file = File("src/inputs/input-day-4.txt")
    val listOfCopiesGameCards: MutableList<GameCard> = mutableListOf()
    val listOfGameCards = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
            .map { it.replace(numberMonoDigitWithTwoSpace, numberMonoDigitWithOneSpace) }
            .map { it.replace(numberMonoDigitWithOneSpace, numberMonoDigitWithZero) }
            .map(GameCard::fromString)
            .map(GameCard::calculateScore)
    }

    listOfGameCards.forEach {
        listOfCopiesGameCards.add(it.copy())
    }

    var totalScore = listOfCopiesGameCards.map(GameCard::score).sum()

    while (totalScore > 0) {
        val tempList: MutableList<GameCard> = mutableListOf()
        listOfCopiesGameCards.forEach { gameCard ->
            while (gameCard.score > 0) {
                val numberToCopy = gameCard.number + gameCard.score
                val cardToCopy = listOfGameCards.firstOrNull { card -> card.number == numberToCopy }
                cardToCopy?.let { tempList.add(it.copy()) }
                gameCard.score--
            }
        }
        listOfCopiesGameCards.addAll(tempList)
        totalScore = listOfCopiesGameCards.map(GameCard::score).sum()
    }

    val result = listOfCopiesGameCards.size

    println(result)
}

data class GameCard(
    val number: Int,
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
            val numberOfCard = cardListString[indexOfName].split(delimiterNumbers)[indexOfNumbers]
            val numbersOfCard = cardListString[indexOfNumbers].split(delimiterNumbersOfCard)
            val winnerNumbersList = numbersOfCard[indexOfWinnerNumbers].split(delimiterNumbers)
            val attemptNumberList = numbersOfCard[indexOfAttemptNumbers].split(delimiterNumbers)

            return GameCard(
                number = numberOfCard.toInt(),
                winnerNumbers = winnerNumbersList,
                attemptNumbers = attemptNumberList,
                score = initialScore
            )
        }
    }

    fun calculateScore(): GameCard {
        val wins = winnerNumbers.intersect(attemptNumbers.toSet())
        score = wins.size

        return this
    }
}