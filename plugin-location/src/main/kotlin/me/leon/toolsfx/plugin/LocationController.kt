package me.leon.toolsfx.plugin

import me.leon.ext.lineAction2String
import me.leon.ext.stacktrace
import tornadofx.*

class LocationController : Controller() {
    fun process(type: LocationServiceType, input: String, isSingleLine: Boolean): String {
        return if (isSingleLine) input.lineAction2String { process(type, it) }
        else process(type, input)
    }

    private fun process(type: LocationServiceType, input: String): String {
        return runCatching { type.process(input, mutableMapOf()) }.getOrElse { it.stacktrace() }
    }
}
