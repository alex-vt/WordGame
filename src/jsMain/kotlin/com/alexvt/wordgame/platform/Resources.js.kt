package com.alexvt.wordgame.platform

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.skiko.loadBytesFromPath

actual suspend fun loadFontFromResources(res: String, weight: FontWeight, style: FontStyle): Font {
    return androidx.compose.ui.text.platform.Font(
        res, loadBytesFromPath("font/$res.ttf"), weight, style
    )
}

actual suspend fun loadTextFromResources(res: String): String {
    return loadBytesFromPath("raw/$res.txt").decodeToString()
}

@Composable
actual fun getImageBitmapFromResources(res: String): ImageBitmap {
    var imageBitmap by remember { mutableStateOf(ImageBitmap(1, 1)) }
    LaunchedEffect(res) {
        val bytes = loadBytesFromPath("drawable/$res.png")
        imageBitmap = org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap()
    }
    return imageBitmap
}