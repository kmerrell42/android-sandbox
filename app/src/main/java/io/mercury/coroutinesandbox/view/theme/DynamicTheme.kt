package io.mercury.coroutinesandbox.view.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import io.mercury.coroutinesandbox.view.theme.ColorPallette.Companion
import io.mercury.coroutinesandbox.view.theme.DynamicTheme.LocalColors

@Composable
fun DynamicThemeWrapper(
    lightColorPalette: ColorPallette = ColorPallette.DefaultLightColorPalette,
    darkColorPallette: ColorPallette = ColorPallette.DefaultDarkColorPalette,
    content: @Composable () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    CompositionLocalProvider(
        LocalColors provides if (isDarkMode) darkColorPallette else lightColorPalette,
        content = content
    )
}


class ColorPallette(
    val primary: Color,
    val secondary: Color,
    val background: Color) {

    companion object {
        val DefaultDarkColorPalette = ColorPallette(
            primary = Color(0xFFFFFFFF),
            secondary = Color(0xFF00FF00),
            background = Color(0xFF444444)
        )

        val DefaultLightColorPalette = ColorPallette(
            primary = Color(0xFF000000),
            secondary = Color(0xFF33FF33),
            background = Color(0xFFFFFFFF)
        )
    }
}

internal object DynamicTheme {

    val colors: ColorPallette
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    internal val LocalColors =
        compositionLocalOf<ColorPallette> { throw IllegalStateException("LocalColors has not yet been initialized, have you wrapped your composable content() in Theme {}?") }
}