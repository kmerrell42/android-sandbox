package io.mercury.coroutinesandbox.view

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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