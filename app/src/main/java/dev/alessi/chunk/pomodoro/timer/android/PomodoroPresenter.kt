package dev.alessi.chunk.pomodoro.timer.android

interface PomodoroPresenter {

    /**
     * Configura o CountDownTimer de acordo com o size.
     *
     * Altera o status para ready.
     */
    fun setup(size: Int)

    /**
     * Altera o status atual do timer.
     */
    fun toggleStatus()


    fun onStop()

    fun onStart(remainingTime: Long)



}