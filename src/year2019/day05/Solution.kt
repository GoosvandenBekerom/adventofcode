package year2019.day05

import util.getFromResources
import year2019.IntCodeComputer

fun main() {
    val program = "/2019/day05/input.txt".getFromResources()
        .split(',')
        .map(String::toInt)

    IntCodeComputer(program).execute()
}
