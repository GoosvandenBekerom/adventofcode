package year2019.day03

import util.getFromResources
import kotlin.math.absoluteValue

fun main() {
    val (wire1, wire2) =
        "/2019/day03/input.txt"
            .getFromResources()
            .split('\n')
            .map { it.split(',').map { s -> Direction.parse(s) } }
            .map {
                val path = mutableListOf(Coordinates(0, 0))
                for (direction in it) {
                    val lastPosition = path.last()
                    path.addAll(direction.execute(lastPosition))
                }
                path
            }

    val normalizedIntersections = wire1.filter { it in wire2 }.map { (x, y) ->
        Coordinates(x.absoluteValue, y.absoluteValue)
    }.map {
        val stepsWire1 = wire1.indexOf(it)
        val stepsWire2 = wire2.indexOf(it)
        Pair(stepsWire1, stepsWire2)
    }

    val steps = normalizedIntersections.map { it.first + it.second }.sorted()

    println(steps.filter { it != 0 })

// STEP 1
//    val normalizedIntersections = wire1.filter { it in wire2 }.map { (x, y) ->
//        Coordinates(x.absoluteValue, y.absoluteValue)
//    }
//
//    val distances = normalizedIntersections.map { it.first + it.second }.sorted()
}

typealias Coordinates = Pair<Int, Int>

sealed class Direction(val step: Int) {
    class UP(step: Int) : Direction(step) {
        override fun execute(coords: Coordinates): List<Coordinates> {
            if (step == 0) return emptyList()

            val currentY = coords.second
            return (currentY+1..currentY + step).map {
                Coordinates(coords.first, it)
            }
        }
    }
    class DOWN(step: Int) : Direction(step) {
        override fun execute(coords: Coordinates): List<Coordinates> {
            if (step == 0) return emptyList()

            val currentY = coords.second
            return (currentY-1 downTo currentY - step).map {
                Coordinates(coords.first, it)
            }
        }
    }
    class RIGHT(step: Int) : Direction(step) {
        override fun execute(coords: Coordinates): List<Coordinates> {
            if (step == 0) return emptyList()

            val currentX = coords.first
            return (currentX+1..currentX + step).map {
                Coordinates(it, coords.second)
            }
        }
    }
    class LEFT(step: Int) : Direction(step) {
        override fun execute(coords: Coordinates): List<Coordinates> {
            if (step == 0) return emptyList()

            val currentX = coords.first
            return (currentX-1 downTo currentX - step).map {
                Coordinates(it, coords.second)
            }
        }
    }

    companion object {
        fun parse(input: String) : Direction {
            val directionChar = input.first()
            val step = input.substringAfterLast(directionChar).toInt()
            return when (directionChar) {
                'U' -> UP(step)
                'D' -> DOWN(step)
                'R' -> RIGHT(step)
                'L' -> LEFT(step)
                else -> throw UnsupportedOperationException("Direction $directionChar is not supported.")
            }
        }
    }

    abstract fun execute(coords: Coordinates): List<Coordinates>
}
