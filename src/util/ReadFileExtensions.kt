package util

fun String.getFromResources() = this.javaClass::class.java.getResource(this).readText()