package io.mercury.coroutinesandbox.view.ext

import android.app.Activity
import android.content.Intent

fun Activity.startActivity(cla: Class<out Activity>) {
    startActivity(Intent(this, cla), null)
}