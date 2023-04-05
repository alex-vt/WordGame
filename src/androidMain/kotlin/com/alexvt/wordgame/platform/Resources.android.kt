package com.alexvt.wordgame.platform

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import java.nio.charset.Charset

@SuppressLint("DiscouragedApi") // querying by name in multiplatform
actual suspend fun loadFontFromResources(res: String, weight: FontWeight, style: FontStyle): Font {
    val context = appContext
    val id = context.resources.getIdentifier(res, "font", context.packageName)
    return Font(id, weight, style)
}

@SuppressLint("DiscouragedApi") // querying by name in multiplatform
actual suspend fun loadTextFromResources(res: String): String {
    val id = appContext.resources.getIdentifier(res, "raw", appContext.packageName)
    return appContext.resources.openRawResource(id).readBytes().toString(Charset.defaultCharset())
}

lateinit var appContext: Application

@SuppressLint("DiscouragedApi") // querying by name in multiplatform
@Composable
actual fun getImageBitmapFromResources(res: String): ImageBitmap {
    val context = appContext
    return remember {
        val option = BitmapFactory.Options()
        option.inPreferredConfig = Bitmap.Config.ARGB_8888
        val id = context.resources.getIdentifier(res, "drawable", context.packageName)
        BitmapFactory.decodeResource(context.resources, id, option).asImageBitmap()
    }
}
