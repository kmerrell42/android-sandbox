package io.mercury.coroutinesandbox.view.compose

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun RecompositionIndicator(
    state: Any,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.body2,
    color: Color = MaterialTheme.colors.secondary
) {

    var iterationState by remember {
        mutableStateOf(IterationState(0, state))
    }

    if (state != iterationState.state) {
        iterationState = IterationState(iterationState.interation + 1, state)
    }

    Text(
        text = iterationState.interation.toString(),
        modifier = modifier,
        style = style,
        color = color
    )
}

private class IterationState(val interation: Int, val state: Any)
