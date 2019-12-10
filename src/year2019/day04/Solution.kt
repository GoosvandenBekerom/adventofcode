package year2019.day04

fun main() {
    val rangeStart = 240298
    val rangeEnd = 784956

    val result = (rangeStart..rangeEnd).count(::isValid)
    println(result)
}

fun isValid(input: Int): Boolean {
    val ints = input.toString().toCharArray().map(Char::toString).map(String::toInt)

    val increasing = (0 until ints.lastIndex).none{ ints[it] > ints[it+1] }
    if (!increasing) return false

    if (ints.groupBy { it }.none { it.value.size == 2 }) return false

    return true
}
