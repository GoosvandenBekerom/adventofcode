package year2019.day05

import util.getFromResources
import year2019.day05.Instruction.*
import year2019.day05.Mode.*
import javax.naming.OperationNotSupportedException

val program = mutableListOf<Int>()

fun main() {
    program += "/2019/day05/input.txt".getFromResources()
        .split(',')
        .map(String::toInt)
        .toMutableList()

    var index = 0
    while (true) {
        val instructionString = program[index].toString().padStart(5, '0')
        val (mode3, mode2, mode1, opLeft, opRight) = instructionString.map(Char::toString).map(String::toInt)
        val opcode = "$opLeft$opRight".toInt()

        val instruction =  Instruction.from(opcode, index)

        if (instruction is Terminate) break

        index = instruction.execute(Mode.from(mode1), Mode.from(mode2), Mode.from(mode3))

        if (index > program.lastIndex || index < 0)
            throw OperationNotSupportedException("Index returned was out of program bounds ($index)")
    }
}

sealed class Instruction {
    open class MathInstruction protected constructor(private val opcodeIndex: Int, private val operation: (Int, Int) -> Int) : Instruction() {
        override fun execute(mode1: Mode, mode2: Mode, mode3: Mode): Int {
            val param1 = getParam(mode1, opcodeIndex + 1)
            val param2 = getParam(mode2, opcodeIndex + 2)
            val destination = program[opcodeIndex + 3]
            program[destination] = operation(param1, param2)
            return opcodeIndex + 4
        }
    }

    class Sum(opcodeIndex: Int) : MathInstruction(opcodeIndex, Int::plus)
    class Multiply(opcodeIndex: Int) : MathInstruction(opcodeIndex, Int::times)

    class Input(private val opcodeIndex: Int) : Instruction() {
        override fun execute(mode1: Mode, mode2: Mode, mode3: Mode): Int {
            val param1 = program[opcodeIndex + 1]
            val input = run {
                println("Which system would you like to test?")
                readLine()!!.toInt()
            }
            program[param1] = input
            return opcodeIndex + 2
        }
    }

    class Output(private val opcodeIndex: Int) : Instruction() {
        override fun execute(mode1: Mode, mode2: Mode, mode3: Mode): Int {
            val param1 = getParam(mode1, opcodeIndex + 1)
            println(param1)
            return opcodeIndex + 2
        }
    }

    open class JumpIf protected constructor(private val jumpCondition: (Int) -> Boolean, private val opcodeIndex: Int) : Instruction() {
        override fun execute(mode1: Mode, mode2: Mode, mode3: Mode): Int {
            val param1 = getParam(mode1, opcodeIndex + 1)
            val param2 = getParam(mode2, opcodeIndex + 2)
            return if (jumpCondition(param1)) param2
            else opcodeIndex + 3
        }
    }

    class JumpIfTrue(opcodeIndex: Int) : JumpIf({ it != 0 }, opcodeIndex)
    class JumpIfFalse(opcodeIndex: Int) : JumpIf({ it == 0 }, opcodeIndex)

    open class LogicInstruction protected constructor(private val logicOperation: (Int, Int) -> Boolean, private val opcodeIndex: Int) : Instruction() {
        override fun execute(mode1: Mode, mode2: Mode, mode3: Mode): Int {
            val param1 = getParam(mode1, opcodeIndex + 1)
            val param2 = getParam(mode2, opcodeIndex + 2)
            val destination = program[opcodeIndex + 3]
            program[destination] = if (logicOperation(param1, param2)) 1 else 0
            return opcodeIndex + 4
        }
    }

    class LessThan(opcodeIndex: Int) : LogicInstruction({p1, p2 -> p1 < p2}, opcodeIndex)
    class Equals(opcodeIndex: Int) : LogicInstruction({p1, p2 -> p1 == p2}, opcodeIndex)

    object Terminate : Instruction() {
        override fun execute(mode1: Mode, mode2: Mode, mode3: Mode) = -1
    }

    companion object {
        fun from(opcode: Int, index: Int) = when(opcode) {
            99 -> Terminate
            1 -> Sum(index)
            2 -> Multiply(index)
            3 -> Input(index)
            4 -> Output(index)
            5 -> JumpIfTrue(index)
            6 -> JumpIfFalse(index)
            7 -> LessThan(index)
            8 -> Equals(index)
            else -> throw UnsupportedOperationException("opcode $opcode is not supported")
        }
    }

    protected fun getParam(mode: Mode, index: Int) = if (mode == POSITION) program[program[index]] else program[index]

    abstract fun execute(mode1: Mode, mode2: Mode, mode3: Mode): Int
}

enum class Mode {
    POSITION,
    IMMEDIATE;

    companion object{
        fun from(number: Int) = when (number) {
            0 -> POSITION
            1 -> IMMEDIATE
            else -> throw UnsupportedOperationException("mode $number is not supported")
        }
    }
}

//    var i = 0
//    loop@while (i < intCodes.lastIndex) {
//        val instruction = intCodes[i].toString().padStart(5, '0')
//        val opcode = instruction.substring(instruction.lastIndex - 1).toInt()
//        val modeArg1 = instruction[instruction.lastIndex - 2].toString().toInt()
//        val modeArg2 = instruction[instruction.lastIndex - 3].toString().toInt()
//        val modeArg3 = instruction[instruction.lastIndex - 4].toString().toInt()
//        try {
//            val arg1 = if (modeArg1 == positionMode) intCodes[intCodes[i + 1]] else intCodes[i + 1]
//            val arg2 = if (modeArg2 == positionMode) intCodes[intCodes[i + 2]] else intCodes[i + 2]
//            when (opcode) {
//                99 -> break@loop
//                1, 2 -> { // operation on arguments
//                    val operator: (Int, Int) -> Int = when (opcode) {
//                        1 -> Int::plus
//                        2 -> Int::times
//                        else -> throw UnsupportedOperationException("parameter opcode $opcode is not supported")
//                    }
//                    val dest = if (modeArg3 == positionMode) intCodes[i + 3] else i + 3
//                    intCodes[dest] = operator(arg1, arg2)
//                    i += 4
//                }
//                3 -> { // Read input
//                    print("user id: ")
//                    intCodes[arg1] = readLine()?.toInt() ?: throw UnsupportedOperationException("Input has to be of type Int")
//                    i += 2
//                }
//                4 -> { // Output value
//                    println(arg1)
//                    i += 2
//                }
//                5 -> { // Jump-if-true
//                    if (arg1 != 0) i = arg2
//                    else i += 3
//                }
//                6 -> { // Jump-if-false
//                    if (arg1 == 0) i = arg2
//                    else i += 3
//                }
//                7 -> { // less then
//                    val dest = if (modeArg3 == positionMode) intCodes[intCodes[i + 3]] else intCodes[i + 3]
//                    if (arg1 < arg2)
//                        intCodes[dest] = 1
//                    else
//                        intCodes[dest] = 0
//                }
//                8 -> {
//                    val dest = if (modeArg3 == positionMode) intCodes[intCodes[i + 3]] else intCodes[i + 3]
//                    if (arg1 == arg2)
//                        intCodes[dest] = 1
//                    else
//                        intCodes[dest] = 0
//                }
//                else -> throw UnsupportedOperationException("opcode $opcode is not supported")
//            }
//        } catch (e: Exception) {
//            println("failed at index $i opcode $opcode")
//            e.printStackTrace()
//        }
//    }
//}
//
//const val positionMode: Int = 0
//
