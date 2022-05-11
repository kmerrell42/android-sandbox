package io.mercury.coroutinesandbox.view.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun ThemedMaterial(content: @Composable () -> Unit) {
    MaterialTheme(colors = Colors(
        isLight = true,
        primary = Color(0xffffb74d),
        primaryVariant = Color(0xffc88719),
        secondary = Color(0xff303f9f),
        secondaryVariant = Color(0xff666ad1),
        background = Color(0xffeeeeee),
        error = Color.Red,
        onBackground = Color.Black,
        onError = Color.White,
        onPrimary = Color.Black,
        onSecondary = Color.White,
        surface = Color.White,
        onSurface = Color.Black
    ), content = content)
}