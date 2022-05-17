package me.leon.ext.fx

import java.util.prefs.Preferences

object Prefs {
    private const val IGNORE_UPDATE = "isIgnoreUpdate"
    private const val ALWAYS_ON_TOP = "alwaysOnTop"
    private const val LANGUAGE = "language"
    private const val AUTO_COPY = "autoCopy"
    private val preference = Preferences.userNodeForPackage(Prefs::class.java)
    fun preference(): Preferences = preference
    var isIgnoreUpdate
        get() = preference.getBoolean(IGNORE_UPDATE, false)
        set(value) {
            preference.putBoolean(IGNORE_UPDATE, value)
        }
    var alwaysOnTop
        get() = preference.getBoolean(ALWAYS_ON_TOP, true)
        set(value) {
            preference.putBoolean(ALWAYS_ON_TOP, value)
        }
    var language: String
        get() = preference.get(LANGUAGE, "zh")
        set(value) {
            preference.put(LANGUAGE, value)
        }
    var autoCopy: Boolean
        get() = preference.getBoolean(AUTO_COPY, false)
        set(value) {
            preference.putBoolean(AUTO_COPY, value)
        }
}
