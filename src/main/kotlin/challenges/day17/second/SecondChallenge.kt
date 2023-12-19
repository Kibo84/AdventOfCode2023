package challenges.day17.second

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.system.exitProcess

const val firstPosition = 0
const val indexAdjustment = 1
var height = 0
var width = 0
var endX = 0
var endY = 0
var grid: List<List<Int>> = listOf()
val stateQueuesByCost = mutableMapOf<Int, MutableList<State>>()
val seenCostByState = mutableMapOf<State, Int>()

fun main() {
    val file = File("src/inputs/input-day-17.txt")

    grid = BufferedReader(InputStreamReader(file.inputStream())).use { fileReader ->
        fileReader.readLines().map { it.toCharArray().map { char -> char.toString().toInt() } }
    }

    height = grid.size
    width = grid[firstPosition].size

    endX = width - indexAdjustment
    endY = height - indexAdjustment

    calculateWayWithMinCost()
}

fun calculateWayWithMinCost() {
    val initialCost = 0
    val initialPosition = 0
    val oneStep = 1
    val zeroStep = 0
    val initialDistance = 1

    val initialPositionOne = State(
        positionX = initialPosition,
        positionY = initialPosition,
        directionX = oneStep,
        directionY = zeroStep,
        distance = initialDistance
    )
    val initialPositionTwo = State(
        positionX = initialPosition,
        positionY = initialPosition,
        directionX = zeroStep,
        directionY = oneStep,
        distance = initialDistance
    )

    moveAndAddState(cost = initialCost, state = initialPositionOne)
    moveAndAddState(cost = initialCost, state = initialPositionTwo)

    while (true) {
        val currentCost = stateQueuesByCost.keys.min()
        val nextStates = stateQueuesByCost.remove(currentCost)

        nextStates?.let {
            for (state in it) {
                val statesToAdd = mutableListOf<State>()
                if (state.distance >= 4) {
                    statesToAdd.add(
                        State(
                            positionX = state.positionX,
                            positionY = state.positionY,
                            directionX = state.directionY,
                            directionY = -state.directionX,
                            distance = initialDistance
                        )
                    )
                    statesToAdd.add(
                        State(
                            positionX = state.positionX,
                            positionY = state.positionY,
                            directionX = -state.directionY,
                            directionY = state.directionX,
                            distance = initialDistance
                        )
                    )
                }
                if (state.distance < 10) {
                    statesToAdd.add(
                        State(
                            positionX = state.positionX,
                            positionY = state.positionY,
                            directionX = state.directionX,
                            directionY = state.directionY,
                            distance = state.distance + initialDistance
                        )
                    )
                }
                statesToAdd.map { nextState -> moveAndAddState(currentCost, nextState) }
            }
        }
    }
}

fun moveAndAddState(cost: Int, state: State) {
    val (positionX: Int, positionY: Int, directionX: Int, directionY: Int, distance: Int) = state
    val currentX = positionX + directionX
    val currentY = positionY + directionY

    val newState = State(currentX, currentY, directionX, directionY, distance)

    if (!newState.isValidState()) {
        return
    }

    val newCost = cost + grid[currentY][currentX]

    if (newState.isLastState()) {
        println(newCost)
        exitProcess(0)
    }

    if (!seenCostByState.containsKey(newState) || seenCostByState[newState]!! > newCost) {
        stateQueuesByCost.getOrPut(newCost) { mutableListOf() }.add(newState)
        seenCostByState[newState] = newCost
    }
}

data class State(val positionX: Int, val positionY: Int, val directionX: Int, val directionY: Int, val distance: Int) {
    fun isValidState(): Boolean {
        return positionX in 0 ..< width && positionY in 0 ..< height
    }

    fun isLastState(): Boolean {
        return positionX == endX && positionY == endY
    }
}