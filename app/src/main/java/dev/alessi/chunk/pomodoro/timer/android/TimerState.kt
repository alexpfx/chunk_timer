package dev.alessi.chunk.pomodoro.timer.android

//unidade: millis
data class TimerState(
    val totalTime: Long,
    val onTick: (millisRemaining: Long) -> Unit,
    val onFinish: () -> Unit
) {
    companion object {
        const val KEY_TOTAL_TIME = "dev.alessi.chunk.pomodoro.timer.android.KEY_TOTAL_TIME"
        const val KEY_CURRENT_TIME = "dev.alessi.chunk.pomodoro.timer.android.KEY_CURRENT_TIME"
        const val KEY_CURRENT_STATUS = "dev.alessi.chunk.pomodoro.timer.android.KEY_CURRENT_STATUS"
    }

    var timer: ChunkCountDownTimer? = null
    var remainingTime: Long = 0
    var status: TimerStatus = TimerStatus.ready


    fun isRunning(): Boolean {
        return status == TimerStatus.running
    }

    fun isReady(): Boolean {
        return status == TimerStatus.ready
    }

    fun setup(timeRemaining: Long) {
        timer?.cancel()
        timer = ChunkCountDownTimer(timeRemaining, 1000, ::tick, onFinish)
        status = TimerStatus.ready
    }

    private fun tick(remainingTime: Long) {
        this.remainingTime = remainingTime
        debug("tick");
        onTick(remainingTime)
    }

    fun start() {
        debug("start $timer")
        timer?.start()
        status = TimerStatus.running
    }


    fun stop(): Long {
        timer?.cancel()
        timer = null
        status = TimerStatus.ready
        return remainingTime
    }

    fun reset() {
        stop()
        remainingTime = totalTime
        setup(remainingTime)
        tick(remainingTime)
    }

}






