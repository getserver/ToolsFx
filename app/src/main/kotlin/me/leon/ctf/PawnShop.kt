package me.leon.ctf

import me.leon.ext.splitBySpace

private val map =
    mapOf(
        0 to arrayOf('目', '口', '凹', '凸', '田'),
        1 to arrayOf('由'),
        2 to arrayOf('中'),
        3 to arrayOf('人', '入', '古'),
        4 to arrayOf('工', '互'),
        5 to arrayOf('果', '克', '尔', '土', '大'),
        6 to arrayOf('木', '王'),
        7 to arrayOf('夫', '主'),
        8 to arrayOf('井', '关', '丰', '并'),
        9 to arrayOf('圭', '羊'),
    )

private val reverseMap by lazy {
    map.values.zip(map.keys).flatMap { (array, key) -> array.map { it to key } }.toMap()
}
// ASCII
fun String.pawnshop() =
    toCharArray().filter { it.code in 0..127 }.joinToString(" ") {
        it.code.split().joinToString("") { map[it]!!.random().toString() }
    }

private fun Int.split() = this.toString().toCharArray().map { it - '0' }

fun String.pawnshopDecode() =
    splitBySpace()
        .map { it.toCharArray().map { reverseMap[it] }.joinToString("").toInt().toChar() }
        .joinToString("")
