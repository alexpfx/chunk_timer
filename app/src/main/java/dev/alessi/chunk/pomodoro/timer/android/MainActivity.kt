package dev.alessi.chunk.pomodoro.timer.android

import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PomodoroView {

    private lateinit var sizeButtons: List<MaterialButton>
    private lateinit var presenter: PomodoroPresenter
    private lateinit var sfxManager: SoundEffectManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sfxManager = SoundEffectManager(this)

        viewInit()
        presenter = PomodoroPresenterImpl(this)
        presenter.setup(2)


    }

    fun onToggleActionClick(view: View) {
        presenter.toggleStatus()
    }

    private fun viewInit() {
        this.sizeButtons = listOf(
            btnSizePP,
            btnSizeP,
            btnSizeM,
            btnSizeG,
            btnSizeGG
        )
        initTags()
    }

    private fun initTags() {
        sizeButtons.forEachIndexed { index, button ->
            run {

                print(button.text)
                button.tag = index

            }
        }
    }


    override fun showTick(totalTimeInSeconds: Long, secondsToFinish: Long) {
        val formatedTime = DateUtils.formatElapsedTime(secondsToFinish)
        txtTimerDisplay.text = formatedTime
        sfxManager.playTick()
        val percFinish = secondsToFinish * 100 / totalTimeInSeconds
        progress(percFinish.toInt())

    }

    override fun showTimerSetted(seconds: Long) {
        val formatedTime = DateUtils.formatElapsedTime(seconds)
        txtTimerDisplay.text = formatedTime
        progress(100)
    }

    fun progress(value: Int) {
        progressBar.progress = value
    }

    override fun showTimerCanceled() {
        btnTimerAction.setText(R.string.button_label_start)

    }

    override fun showTimerFinished() {
        btnTimerAction.setText(R.string.button_label_start)

        sfxManager.playFinish()

    }

    override fun showTimerStarted() {
        btnTimerAction.setText(R.string.button_label_cancel)
        sfxManager.playStart()
    }

    override fun showSetupChanged(tag: Int) {
        uncheckAll()
        checkThis(tag)
    }

    fun changeSize(view: View) {

        presenter.setup(view.tag as Int)
    }

    override fun showStateChanged(newStatus: TimerStatus) {
        print(newStatus)
        if (newStatus == TimerStatus.ready) {
            btnTimerAction.setIconResource(R.drawable.ic_play_arrow_black_24dp)
            enableAll()

        } else {
            btnTimerAction.setIconResource(R.drawable.ic_stop_black_24dp)
            disableAll()
        }
    }

    private fun disableAll() {
        sizeButtons.forEach {
            it.isEnabled = false
        }
    }

    private fun enableAll() {
        sizeButtons.forEach {
            it.isEnabled = true
        }

    }

    private fun uncheckAll() {
        sizeButtons.forEach {
            it.isChecked = false
        }
    }

    private fun checkThis(tag: Int) {
        getSizedButtonByTagId(tag).isChecked = true
    }

    private fun getSizedButtonByTagId(id: Int): MaterialButton {
        return this.sizeButtons.first {
            it.tag == id
        }

    }


}
