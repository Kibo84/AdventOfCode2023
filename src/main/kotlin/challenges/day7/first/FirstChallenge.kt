package challenges.day7.first

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

enum class HandsType(val value: Int) {
    FIVE_OF_A_KIND(7),
    FOUR_OF_A_KIND(6),
    FULL_HOUSE(5),
    THREE_OF_A_KIND(4),
    TWO_PAIR(3),
    ONE_PAIR(2),
    HIGH_CARD(1)
}

val valueOfCardsMap: Map<Char, Int> = mapOf(
    'A' to 13,
    'K' to 12,
    'Q' to 11,
    'J' to 10,
    'T' to 9,
    '9' to 8,
    '8' to 7,
    '7' to 6,
    '6' to 5,
    '5' to 4,
    '4' to 3,
    '3' to 2,
    '2' to 1
)

fun main() {
    val file = File("src/inputs/input-day-7.txt")

    val result = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines()
            .asSequence()
            .map(Hand::fromString)
            .map(Hand::calculateTypeOfHand)
            .sorted()
            .mapIndexed { index, hand -> calculateWinnings(index, hand.bid) }
            .sum()
    }

    println(result)
}

fun calculateWinnings(index: Int, bid: Long) = (index + 1) * bid

data class Hand(val cards: String, var type: HandsType = HandsType.HIGH_CARD, val bid: Long) : Comparable<Hand> {

    fun calculateTypeOfHand(): Hand {
        var setOfFive = 0
        var setOfFour = 0
        var setOfThree = 0
        var setOfTwo = 0

        valueOfCardsMap.keys.forEach { cardSymbol ->
            val repeats = cards.count { card -> card == cardSymbol }
            if (repeats == 5) setOfFive++
            if (repeats == 4) setOfFour++
            if (repeats == 3) setOfThree++
            if (repeats == 2) setOfTwo++
        }

        type = when {
            setOfFive == 1 -> HandsType.FIVE_OF_A_KIND
            setOfFour == 1 -> HandsType.FOUR_OF_A_KIND
            setOfThree == 1 && setOfTwo == 1 -> HandsType.FULL_HOUSE
            setOfThree == 1 && setOfTwo == 0 -> HandsType.THREE_OF_A_KIND
            setOfTwo == 2 -> HandsType.TWO_PAIR
            setOfTwo == 1 -> HandsType.ONE_PAIR
            else -> HandsType.HIGH_CARD
        }

        return this
    }

    override fun compareTo(other: Hand): Int {
        val valueTypeOfThis = this.type.value
        val valueTypeOfOther = other.type.value

        if (valueTypeOfThis > valueTypeOfOther) return 1
        if (valueTypeOfThis < valueTypeOfOther) return -1

        this.cards.forEachIndexed { index, card ->
            val valueOfThisCard = valueOfCardsMap[card]
            val valueOfOtherCard = valueOfCardsMap[other.cards[index]]

            if (valueOfThisCard != null && valueOfOtherCard != null) {
                if (valueOfThisCard > valueOfOtherCard) return 1
                if (valueOfThisCard < valueOfOtherCard) return -1
            }
        }

        return 0
    }

    companion object {
        fun fromString(line: String): Hand {
            val cardsIndex = 0
            val bidIndex = 1
            val delimiter = " "

            val writtenHand = line.split(delimiter)
            val cards = writtenHand[cardsIndex]
            return Hand(cards = cards, bid = writtenHand[bidIndex].toLong())
        }
    }
}