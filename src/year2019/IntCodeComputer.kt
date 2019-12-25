package year2019

import year2019.IntCodeComputer.Mode.*

val program = mutableListOf<Int>()

class IntCodeComputer(software: List<Int>) {
    init {
        program += software
    }

    fun execute() {
        var index = 0
        while (true) {
            val instructionString = program[index].toString().padStart(5, '0')
            val (mode3, mode2, mode1, opLeft, opRight) = instructionString.map(Char::toString).map(String::toInt)
            val opcode = "$opLeft$opRight".toInt()

            val instruction =  Instruction.from(opcode, index)

            if (instruction is Instruction.Terminate) break

            index = instruction.execute(Mode.from(mode1), Mode.from(mode2), Mode.from(mode3))

            if (index > program.lastIndex || index < 0)
                throw UnsupportedOperationException("Index returned was out of program bounds ($index)")
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
                    print("Input: ")
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
}