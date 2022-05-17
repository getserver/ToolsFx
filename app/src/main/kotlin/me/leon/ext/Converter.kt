package me.leon.ext

import java.nio.charset.Charset
import me.leon.encode.*
import me.leon.encode.base.BYTE_BITS
import me.leon.encode.base.BYTE_MASK
import tornadofx.*

const val HEX_RADIX = 16
const val DECIMAL_RADIX = 10
const val OCTAL_RADIX = 8

/** 16进制编解码 */
fun ByteArray.toHex() = hex

fun ByteArray.toHexReverse() = hex.chunked(2).joinToString(" ") { it.reversed() }

fun String.toHex() = toByteArray().toHex()

fun String.hex2String(charset: String = "UTF-8") =
    hex2ByteArray().toString(Charset.forName(charset))

fun String.hex2ByteArray() =
    stripAllSpace().chunked(2).map { it.toInt(HEX_RADIX).toByte() }.toByteArray()

fun String.hexReverse2ByteArray() =
    stripAllSpace().chunked(2).map { it.reversed().toInt(HEX_RADIX).toByte() }.toByteArray()

fun ByteArray.toBinaryString() =
    joinToString("") {
        with((it.toInt() and BYTE_MASK).toString(2)) {
            this.takeIf { it.length == BYTE_BITS } ?: ("0".repeat(BYTE_BITS - this.length) + this)
        }
    }

/** 二进制编解码 */
fun String.toBinaryString() = toByteArray().toBinaryString()

fun String.binary2Ascii() = String(binary2ByteArray(), Charsets.UTF_8)

fun String.binary2ByteArray() =
    toList().chunked(BYTE_BITS).map { it.joinToString("").toInt(2).toByte() }.toByteArray()

/** unicode编解码 */
fun String.toUnicodeString() =
    fold(StringBuilder()) { acc, c -> acc.append("\\u").append(c.code.toString(HEX_RADIX)) }
        .toString()

/** js hex 编解码 \x61 */
fun String.toJsHexEncodeString() =
    toByteArray()
        .map { it.toUByte() }
        .fold(StringBuilder()) { acc, c -> acc.append("\\x").append(c.toInt().toString(HEX_RADIX)) }
        .toString()

/** js hex 解码 \x61 */
fun String.jsHexDecodeString() =
    split("(?i)\\\\x".toRegex())
        .filterNot { it.isEmpty() }
        .map { it.toInt(HEX_RADIX).toByte() }
        .toByteArray()
        .toString(Charsets.UTF_8)

/** js octal 编解码 \141 */
fun String.jsOctalDecodeString() =
    split("(?i)\\\\".toRegex())
        .filterNot { it.isEmpty() }
        .map { it.toInt(OCTAL_RADIX).toByte() }
        .toByteArray()
        .toString(Charsets.UTF_8)

/** js octal 编码 \141 */
fun String.toJsOctalEncodeString() =
    toByteArray()
        .map { it.toUByte() }
        .fold(StringBuilder()) { acc, c ->
            acc.append("\\").append(c.toInt().toString(OCTAL_RADIX))
        }
        .toString()

fun String.unicode2String() =
    if (contains("&#"))
        "(?i)&#x([0-9a-f]+);|&#(\\d+);"
            .toRegex()
            .findAll(this)
            .map {
                it.groupValues[1].ifEmpty { it.groupValues[2] } to
                    if (it.groupValues[0].contains("x", true)) HEX_RADIX else DECIMAL_RADIX
            }
            .fold(StringBuilder()) { acc, (c, radix) -> acc.append(c.toInt(radix).toUnicodeChar()) }
            .toString()
    else
        split("(?i)\\\\u\\+?".toRegex())
            .filterIndexed { index, _ -> index != 0 }
            .fold(StringBuilder()) { acc, c -> acc.append(c.toInt(HEX_RADIX).toChar()) }
            .toString()

fun String.htmlEntity2String() =
    if (contains("&#")) {
        StringBuilder(this).replace("(?i)&#(x?[0-9a-z]+)+;".toRegex()) {
            it.value.charHtmlEntityDecode()?.toUnicodeChar()
                ?: if (it.groupValues[1].startsWith("x")) {
                    it.groupValues[1].substring(1).toInt(HEX_RADIX).toUnicodeChar()
                } else {
                    it.groupValues[1].toInt().toUnicodeChar()
                }
        }
    } else this

/** htmlEntity编解码 */
fun String.toHtmlEntity(radix: Int = 10, isAll: Boolean = true) =
    fold(StringBuilder()) { acc, c ->
            if (isAll) acc.append(c.code.toHtmlEntityAll(radix))
            else acc.append(c.code.toHtmlEntity() ?: c)
        }
        .toString()

fun String.unicodeMix2String() =
    StringBuilder(this).replace(
            "(?i:\\\\u\\+?[0-9a-zA-Z]{1,5}|(?i)&#x([0-9a-f]+);|&#(\\d+);)+".toRegex()
        ) { it.value.unicode2String() }
