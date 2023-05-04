package pl.piter.commons.util

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun generateId(length: Int): String = List(length) { charPool.random() }
    .joinToString("")