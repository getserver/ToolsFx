package me.leon.view

import java.security.Security
import java.util.ServiceLoader
import kotlin.reflect.KClass
import me.leon.BUILD_DATE
import me.leon.ToolsApp.Companion.isEnableBigInt
import me.leon.ToolsApp.Companion.isEnableClassical
import me.leon.ToolsApp.Companion.isEnableInternalWebview
import me.leon.ToolsApp.Companion.isEnableMac
import me.leon.ToolsApp.Companion.isEnablePBE
import me.leon.ToolsApp.Companion.isEnableQrcode
import me.leon.ToolsApp.Companion.isEnableSignature
import me.leon.ToolsApp.Companion.isEnableSymmetricStream
import me.leon.VERSION
import me.leon.ext.fx.Prefs
import me.leon.toolsfx.plugin.PluginFragment
import me.leon.toolsfx.plugin.PluginView
import org.bouncycastle.jce.provider.BouncyCastleProvider
import tornadofx.*
import tornadofx.FX.Companion.messages

class Home : View("${messages["appName"]} v$VERSION build $BUILD_DATE") {
    private var views: MutableList<KClass<out Fragment>> = mutableListOf()

    init {
        if (isEnableClassical) views.add(ClassicalView::class)
        views.addAll(mutableListOf(EncodeView::class, EncodeTransferView::class))
        if (isEnableBigInt) views.add(BigIntFragment::class)
        views.add(StringProcessView::class)
        views.add(DigestView::class)
        if (isEnableMac) views.add(MacView::class)
        views.add(SymmetricCryptoView::class)
        if (isEnableSymmetricStream) views.add(SymmetricCryptoStreamView::class)
        views.add(AsymmetricCryptoView::class)
        if (isEnableSignature) views.add(SignatureView::class)
        if (isEnableQrcode) views.add(QrcodeView::class)
        if (isEnablePBE) views.add(PBEView::class)
        if (isEnableInternalWebview)
            runCatching { Class.forName("javafx.scene.web.WebView") }.onSuccess {
                views.add(OnlineWebView::class)
            }
    }

    override val root = tabpane {
        views.forEach { tab(find(it)) }
        // support library
        ServiceLoader.load(PluginFragment::class.java)?.forEach {
            views.add(it.javaClass.kotlin)
            tab(it) {
                this.text = it.description
                println(this.text)
            }
        }
        ServiceLoader.load(PluginView::class.java)?.forEach {
            tab(it) {
                this.text = it.description
                println(this.text)
            }
        }
        tab<AboutView>()
        primaryStage.isAlwaysOnTop = Prefs.alwaysOnTop
        contextmenu {
            item("Top ${"  √".takeIf { Prefs.alwaysOnTop } ?: ""}") {
                action {
                    Prefs.alwaysOnTop = !Prefs.alwaysOnTop
                    primaryStage.isAlwaysOnTop = Prefs.alwaysOnTop
                    text = "Top ${"  √".takeIf { Prefs.alwaysOnTop } ?: ""}"
                }
            }
            menu("Language") {
                item("English(require restart)${"  √".takeIf { Prefs.language != "zh" } ?: ""}") {
                    action { Prefs.language = "en" }
                }
                item("中文(需重启)${"  √".takeIf { Prefs.language == "zh" } ?: ""}") {
                    action { Prefs.language = "zh" }
                }
            }

            item("${messages["autoCopy"]}${"  √".takeIf { Prefs.autoCopy } ?: ""}") {
                action {
                    Prefs.autoCopy = !Prefs.autoCopy
                    text = "${messages["autoCopy"]}${"  √".takeIf { Prefs.autoCopy } ?: ""}"
                }
            }

            item("open in new window") {
                action {
                    with(this@tabpane.selectionModel.selectedIndex) {
                        if (this < views.size) find(views[this]).openWindow()
                    }
                }
            }
        }
    }

    companion object {
        init {
            Security.addProvider(BouncyCastleProvider())
        }
    }
}
