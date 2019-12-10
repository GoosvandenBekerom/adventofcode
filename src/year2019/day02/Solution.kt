package year2019.day02

import util.getFromResources

fun main() {
    val wantedOutput = 19690720

    for (noun in 0..99) {
        for (verb in 0..99) {
            val intCodes = "/2019/day02/input.txt".getFromResources()
                .split(',')
                .map(String::toInt)
                .toMutableList()

            //restore the gravity assist program
            intCodes[1] = noun
            intCodes[2] = verb

            for (i in intCodes.indices step 4) {
                val (opcode, el1, el2, dest) = intCodes.subList(i, i + 4)

                if (opcode == 99) break

                val operator: (Int, Int) -> Int = when (opcode) {
                    1 -> Int::plus
                    2 -> Int::times
                    else -> throw UnsupportedOperationException("opcode $opcode is not supported")
                }

                intCodes[dest] = operator(intCodes[el1], intCodes[el2])
            }

            if (intCodes.first() == wantedOutput) {
                println(100 * noun + verb)
                return
            }
        }
    }
}


// STEP 1
//fun main() {
//    val intCodes = "/year2019.day02/input.txt".getFromResources()
//        .split(',')
//        .map(String::toInt)
//        .toMutableList()
//
//    //restore the gravity assist program
//    intCodes[1] = 12
//    intCodes[2] = 2
//
//    for (i in intCodes.indices step 4) {
//        val (opcode, el1, el2, dest) = intCodes.subList(i, i + 4)
//
//        if (opcode == 99) break
//
//        val operator: (Int, Int) -> Int = when (opcode) {
//            1 -> Int::plus
//            2 -> Int::times
//            else -> throw UnsupportedOperationException("opcode $opcode is not supported")
//        }
//
//        intCodes[dest] = operator(intCodes[el1], intCodes[el2])
//    }
//
//    val output = intCodes.first()
//    println(output)
//}
