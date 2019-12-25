package year2019.day06

import util.getFromResources

fun main() {
//    Part 1
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

//    Part 2
    val orbitalTransfers = "/2019/day06/input.txt".getFromResources()
        .split('\n')
        .map { it.split(')') }
        .map { (parent, child) -> child to parent }
        .toMap()
        .calculateOrbitalTransfers("YOU", "SAN")

    println(orbitalTransfers)
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

fun calculatePath(planet: String, orbits: Map<String, String>) : List<String> =
    if(planet == "COM")
        listOf("COM")
    else
        calculatePath(orbits.getValue(planet), orbits) + planet

fun Map<String, String>.calculateOrbitalTransfers(from: String, to: String) : Int {
    val fromPath = calculatePath(from, this).toMutableList()
    val toPath = calculatePath(to, this).toMutableList()

    while(fromPath.first() == toPath.first()) {
        fromPath.removeAt(0)
        toPath.removeAt(0)
    }

    return fromPath.size + toPath.size - 2
}
