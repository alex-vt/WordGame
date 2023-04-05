package com.alexvt.wordgame.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import java.nio.charset.Charset

actual suspend fun loadFontFromResources(res: String, weight: FontWeight, style: FontStyle): Font =
       androidx.compose.ui.text.platform.Font("font/$res.ttf", weight, style)

actual suspend fun loadTextFromResources(res: String): String =
       useResource("raw/$res.txt") { it.readAllBytes() }.toString(Charset.defaultCharset())

@Composable
actual fun getImageBitmapFromResources(res: String): ImageBitmap =
       remember {
              val bytes = useResource("drawable/$res.png") { it.readAllBytes() }
              org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap()
       }