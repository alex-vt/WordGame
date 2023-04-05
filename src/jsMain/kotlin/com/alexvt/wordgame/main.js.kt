@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "EXPOSED_PARAMETER_TYPE")
// Workaround: ComposeWindow and ComposeLayer are internal,
// see https://slack-chats.kotlinlang.org/t/2224952/compose-for-web-on-canvas-skia-wasm-i-have-a-simple-function#59978a9c-356f-4e08-a5c9-9a096a050ddb
package com.alexvt.wordgame

import com.alexvt.wordgame.viewui.MainNavigationView
import com.alexvt.wordgame.viewui.common.WindowLifecycleEnvironment
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import moe.tlaster.precompose.preComposeWindow
import org.jetbrains.skiko.wasm.onWasmReady
import org.w3c.dom.HTMLCanvasElement

fun main() {
    val dependencies = AppDependencies::class.create()

    fun HTMLCanvasElement.fillViewportSize() {
        setAttribute("width", "${window.innerWidth}")
        setAttribute("height", "${window.innerHeight}")
    }

    var canvas = (document.getElementById("ComposeTarget") as HTMLCanvasElement).apply {
        fillViewportSize()
    }

    onWasmReady {
        preComposeWindow(title = "Word Game") {
            window.addEventListener("resize", {
                val newCanvas = canvas.cloneNode(false) as HTMLCanvasElement
                canvas.replaceWith(newCanvas)
                canvas = newCanvas
                newCanvas.fillViewportSize()
                layer.layer.attachTo(newCanvas)
                layer.layer.needRedraw()
                val density = window.devicePixelRatio.toFloat()
                val scale = layer.layer.contentScale
                layer.setSize(
                    (newCanvas.width / scale * density).toInt(),
                    (newCanvas.height / scale * density).toInt()
                )
            })

            MainNavigationView(
                dependencies,
                lifecycle = WindowLifecycleEnvironment(
                    windowVisibilityFlow = MutableStateFlow(true), // todo document.addEventListener("visibilitychange"...
                    backgroundCoroutineDispatcher = Dispatchers.Default,
                ),
            )
        }
    }
}
