package year2019.day06

import util.getFromResources

fun main() {
    val inputAsNodes = "/2019/day06/input.txt".getFromResources()
        .split('\n')
        .map { it.split(')') }
        .groupBy({ it[0] }, { it[1] })
        .map { Node(it.key, mutableListOf(*it.value.map { name -> Node(name) }.toTypedArray())) }
        .toMutableList()

    val com = inputAsNodes.removeAt(inputAsNodes.indexOfFirst { it.name == "COM" })

    buildChain(com, inputAsNodes)

    val checksum = calculateChecksum(com)

    println(checksum)
}

data class Node(val name: String, val children: MutableList<Node> = mutableListOf())

fun buildChain(node: Node, remainder: MutableList<Node>) : Node {
    for ((i, child) in node.children.withIndex()) {
        val index = remainder.indexOfFirst { it.name == child.name }
        if (index >= 0) {
            node.children[i] = remainder.removeAt(index)
        }
    }
    if (remainder.isNotEmpty()) {
        for (child in node.children) {
            buildChain(child, remainder)
        }
    }

    return node
}

fun calculateChecksum(node: Node, depth: Int = 0) : Int {
    var checksum = 0
    for (child in node.children) {
        checksum += calculateChecksum(child, depth + 1)
    }
    return checksum + depth
}
