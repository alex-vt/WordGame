package com.alexvt.wordgame.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

expect suspend fun loadFontFromResources(res: String, weight: FontWeight, style: FontStyle): Font

expect suspend fun loadTextFromResources(res: String): String

@Composable
expect fun getImageBitmapFromResources(res: String): ImageBitmap