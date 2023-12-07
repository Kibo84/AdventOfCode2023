package challenges.day7.second

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
    'T' to 10,
    '9' to 9,
    '8' to 8,
    '7' to 7,
    '6' to 6,
    '5' to 5,
    '4' to 4,
    '3' to 3,
    '2' to 2,
    'J' to 1,
)

const val indexAdjustment = 1

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

fun calculateWinnings(index: Int, bid: Long) = (index + indexAdjustment) * bid

data class Hand(val cards: String, var type: HandsType = HandsType.HIGH_CARD, val bid: Long) : Comparable<Hand> {

    fun calculateTypeOfHand(): Hand {
        val joker = 'J'
        this.cards.forEach { cardSymbol ->
            val tempHand = this.copy(cards = this.cards.replace(joker, cardSymbol))

            val possibleType = calculateType(tempHand)
            if (possibleType.value > type.value) type = possibleType
        }

        return this
    }

    override fun compareTo(other: Hand): Int {
        val thisMajor = 1
        val otherMajor = -1
        val equals = 0
        val valueTypeOfThis = this.type.value
        val valueTypeOfOther = other.type.value

        if (valueTypeOfThis > valueTypeOfOther) return thisMajor
        if (valueTypeOfThis < valueTypeOfOther) return otherMajor

        this.cards.forEachIndexed { index, card ->
            val valueOfThisCard = valueOfCardsMap[card]
            val valueOfOtherCard = valueOfCardsMap[other.cards[index]]

            if (valueOfThisCard != null && valueOfOtherCard != null) {
                if (valueOfThisCard > valueOfOtherCard) return thisMajor
                if (valueOfThisCard < valueOfOtherCard) return otherMajor
            }
        }

        return equals
    }

    companion object {
        fun fromString(line: String): Hand {
            val cardsIndex = 0
            val bidIndex = 1
            val delimiter = " "

            val writtenHand = line.split(delimiter)
            return Hand(cards = writtenHand[cardsIndex], bid = writtenHand[bidIndex].toLong())
        }

        fun calculateType(hand: Hand): HandsType {
            var setOfFive = 0
            var setOfFour = 0
            var setOfThree = 0
            var setOfTwo = 0

            valueOfCardsMap.keys.filter{ it in hand.cards }.forEach { cardSymbol ->
                val repeats = hand.cards.count { card -> card == cardSymbol }
                if (repeats == 5) setOfFive++
                if (repeats == 4) setOfFour++
                if (repeats == 3) setOfThree++
                if (repeats == 2) setOfTwo++
            }

            return when {
                setOfFive == 1 -> HandsType.FIVE_OF_A_KIND
                setOfFour == 1 -> HandsType.FOUR_OF_A_KIND
                setOfThree == 1 && setOfTwo == 1 -> HandsType.FULL_HOUSE
                setOfThree == 1 && setOfTwo == 0 -> HandsType.THREE_OF_A_KIND
                setOfTwo == 2 -> HandsType.TWO_PAIR
                setOfTwo == 1 -> HandsType.ONE_PAIR
                else -> HandsType.HIGH_CARD
            }
        }
    }
}