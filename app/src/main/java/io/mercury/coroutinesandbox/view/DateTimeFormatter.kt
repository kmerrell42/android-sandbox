package io.mercury.coroutinesandbox.view

import java.text.SimpleDateFormat
import javax.inject.Inject


class DateTimeFormatter @Inject constructor() {
    fun formatClockTime(millis: Long): String {
        return FORMAT_CLOCK_TIME.format(millis)
    }

    companion object {
        private val FORMAT_CLOCK_TIME = SimpleDateFormat.getTimeInstance()
    }
}