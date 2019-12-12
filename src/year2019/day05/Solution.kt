package year2019.day05

import util.getFromResources

const val positionMode: Int = 0

fun main() {
    val intCodes = "/2019/day05/input.txt".getFromResources()
        .split(',')
        .map(String::toInt)
        .toMutableList()

    var i = 0
    loop@while (i < intCodes.lastIndex) {
        val instruction = intCodes[i].toString().padStart(5, '0')
        val opcode = instruction.substring(instruction.lastIndex - 1).toInt()
        val modeArg1 = instruction[instruction.lastIndex - 2].toString().toInt()
        val modeArg2 = instruction[instruction.lastIndex - 3].toString().toInt()
        val modeArg3 = instruction[instruction.lastIndex - 4].toString().toInt()
        when (opcode) {
            99 -> break@loop
            1, 2 -> {
                val operator: (Int, Int) -> Int = when (opcode) {
                    1 -> Int::plus
                    2 -> Int::times
                    else -> throw UnsupportedOperationException("parameter opcode $opcode is not supported")
                }
                val arg1 = intCodes[i + 1]
                val arg2 = intCodes[i + 2]
                val dest = if (modeArg3 == positionMode) intCodes[i + 3] else i + 3
                intCodes[dest] = operator(
                    if (modeArg1 == positionMode) intCodes[arg1] else arg1,
                    if (modeArg2 == positionMode) intCodes[arg2] else arg2
                )
                i += 4
            }
            3 -> {
                print("userid: ")
                intCodes[intCodes[i + 1]] = readLine()?.toInt() ?: throw UnsupportedOperationException("Input has to be of type Int")
                i += 2
            }
            4 -> {
                println(
                    if (modeArg1 == positionMode) intCodes[intCodes[i + 1]]
                    else intCodes[i + 1]
                )
                i += 2
            }
            else -> throw UnsupportedOperationException("opcode $opcode is not supported")
        }
    }
}

