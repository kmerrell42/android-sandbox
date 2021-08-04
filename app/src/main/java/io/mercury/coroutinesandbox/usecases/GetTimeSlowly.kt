package io.mercury.coroutinesandbox.usecases

class GetTimeSlowly {
    fun invoke(): Long {
        Thread.sleep(5000)
        return System.currentTimeMillis()
    }
}