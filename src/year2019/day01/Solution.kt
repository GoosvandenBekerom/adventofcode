package year2019.day01

import util.getFromResources
import kotlin.math.floor
import kotlin.math.max

fun main() {
    val result = "/2019/day01/input.txt".getFromResources()
        .split("\n")
        .map { it.trim().toInt() }
        .map {
            it.calculateFuelNeeded()
        }
        .sum()

    println(result)
}

fun Int.calculateFuelNeeded(): Int {
    val result = floor(div(3f)).toInt() - 2
    return if (result > 0) result + max(0, result.calculateFuelNeeded())
    else result
}