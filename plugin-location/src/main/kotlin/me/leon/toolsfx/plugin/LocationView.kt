package me.leon.toolsfx.plugin

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import me.leon.ext.*
import me.leon.ext.fx.*
import tornadofx.*

class LocationView : PluginFragment("LocationView") {
    override val version = "v1.2.0"
    override val date: String = "2022-04-06"
    override val author = "Leon406"
    override val description = "经纬度相关"
    init {
        println("Plugin Info:$description $version $date $author  ")
    }
    lateinit var taInput: TextArea
    lateinit var taOutput: TextArea
    private val controller: LocationController by inject()
    private var locationServiceType: LocationServiceType = LocationServiceType.WGS2GCJ
    private val inputText: String
        get() = taInput.text.trim()
    private val outputText: String
        get() = taOutput.text
    private val isSingleLine = SimpleBooleanProperty(false)
    private val eventHandler = fileDraggedHandler {
        taInput.text =
            with(it.first()) {
                if (length() <= 10 * 1024 * 1024)
                    if (realExtension() in unsupportedExts) "unsupported file extension"
                    else readText()
                else "not support file larger than 10M"
            }
    }

    private val promptList =
        arrayOf(
            "输入经纬度,格式: 120.233920,30.349616",
            "输入两个地点经纬度,格式: 120.233920,30.349616-120.233920,30.349616",
            "输入地址,格式: 北京天安门"
        )

    override val root = vbox {
        prefWidth = 800.0
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
        title = "LocationView"
        hbox {
            alignment = Pos.CENTER_LEFT
            spacing = DEFAULT_SPACING
            label(messages["input"])
            button(graphic = imageview("/img/import.png")) {
                action { taInput.text = clipboardText() }
            }
        }

        taInput =
            textarea {
                isWrapText = true
                onDragEntered = eventHandler
                promptText = promptList.first()
            }
        hbox {
            alignment = Pos.CENTER_LEFT
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            spacing = DEFAULT_SPACING
            label("function:")
            tilepane {
                vgap = 8.0
                alignment = Pos.TOP_LEFT
                prefColumns = 7
                togglegroup {
                    locationServiceTypeMap.forEach {
                        radiobutton(it.key) {
                            setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                            if (it.value == LocationServiceType.WGS2GCJ) isSelected = true
                        }
                    }
                    selectedToggleProperty().addListener { _, _, new ->
                        locationServiceType = new.cast<RadioButton>().text.locationServiceType()
                        taInput.promptText =
                            when (locationServiceType) {
                                LocationServiceType.DISTANCE -> promptList[1]
                                LocationServiceType.GEO_BD, LocationServiceType.GEO_AMPA ->
                                    promptList.last()
                                else -> promptList.first()
                            }
                        println(locationServiceType)
                    }
                }
            }
        }

        hbox {
            alignment = Pos.CENTER_LEFT
            spacing = DEFAULT_SPACING
            paddingLeft = DEFAULT_SPACING
            checkbox(messages["singleLine"], isSingleLine)

            button(messages["run"], imageview("/img/run.png")) { action { doProcess() } }
            button("百度坐标拾取") {
                action { "https://api.map.baidu.com/lbsapi/getpoint/index.html".openInBrowser() }
            }
            button("高德坐标拾取") { action { "https://lbs.amap.com/tools/picker".openInBrowser() } }
            button("腾讯坐标拾取") { action { "https://lbs.qq.com/getPoint/".openInBrowser() } }
        }
        hbox {
            spacing = DEFAULT_SPACING
            alignment = Pos.CENTER_LEFT
            label(messages["output"])
            button(graphic = imageview("/img/copy.png")) { action { outputText.copy() } }
            button(graphic = imageview("/img/up.png")) {
                action {
                    taInput.text = outputText
                    taOutput.text = ""
                }
            }
        }
        taOutput =
            textarea {
                promptText = messages["outputHint"]
                isWrapText = true
            }
    }

    private fun doProcess() {
        if (inputText.isEmpty()) return
        runAsync { controller.process(locationServiceType, inputText, isSingleLine.get()) } ui
            {
                taOutput.text = it
            }
    }
}
