package io.mercury.coroutinesandbox.view.theming

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.mercury.coroutinesandbox.view.theme.ColorPallette
import io.mercury.coroutinesandbox.view.theme.DynamicTheme
import io.mercury.coroutinesandbox.view.theme.DynamicThemeWrapper

class ThemeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lightOverride1 = ColorPallette(
            primary = "#0000ff".toColor(),
            secondary = "#ff00ff".toColor(),
            background = "#8fb38b".toColor()
        )
        val darkOverride1 = ColorPallette(
            primary = "#0000ff".toColor(),
            secondary = "#ff00ff".toColor(),
            background = "#082904".toColor()
        )

        val lightOverride2 = ColorPallette(
            primary = "#00ff00".toColor(),
            secondary = "#ff00ff".toColor(),
            background = "#c79fc0".toColor()
        )
        val darkOverride2 = ColorPallette(
            primary = "#00ff00".toColor(),
            secondary = "#ff00ff".toColor(),
            background = "#290422".toColor()
        )

        setContent {
            DynamicThemeWrapper {
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(DynamicTheme.colors.background)
                ) {

                    ColorTestLayout("Default") // Note that this isn't directly wrapped
                    Spacer(modifier = Modifier.height(18.dp))

                    DynamicThemeWrapper(
                        lightColorPalette = lightOverride1,
                        darkColorPallette = darkOverride1
                    ) {
                        ColorTestLayout("Light 1")
                    }
                    Spacer(modifier = Modifier.height(18.dp))

                    DynamicThemeWrapper(
                        lightColorPalette = lightOverride2,
                        darkColorPallette = darkOverride2
                    ) {
                        ColorTestLayout("Light 2")
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }

        }
    }

    @Composable
    private fun ColorTestLayout(label: String) {
        Column {
            Text(text = label) // no color applied
            Column(
                modifier = Modifier
                    .height(90.dp)
                    .fillMaxWidth()
                    .background(DynamicTheme.colors.background)
                    .border(2.dp, Color.Black)
            ) {
                Text(text = "Primary Color", color = DynamicTheme.colors.primary)
                Text(text = "Secondary Color", color = DynamicTheme.colors.secondary)
            }
        }
    }

    fun String.toColor(): Color {
        return Color(android.graphics.Color.parseColor(this))
    }
}