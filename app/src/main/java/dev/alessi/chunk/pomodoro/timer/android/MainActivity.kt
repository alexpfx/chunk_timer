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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

    override fun showTick(timeToFinish: Long) {
        val formatedTime = DateUtils.formatElapsedTime(timeToFinish)
        txtTimerDisplay.text = formatedTime
    }

    override fun showTimerSetted(seconds: Long) {
        val formatedTime = DateUtils.formatElapsedTime(seconds)
        txtTimerDisplay.text = formatedTime
    }

    override fun showTimerCanceled() {
        btnTimerAction.setText(R.string.button_label_start)
    }

    override fun showTimerFinished() {
        btnTimerAction.setText(R.string.button_label_start)
    }

    override fun showTimerStarted() {
        btnTimerAction.setText(R.string.button_label_cancel)
    }

    override fun showSetupChanged(tag: Int) {
        uncheckAll()
        checkThis(tag)
    }

    fun changeSize(view: View) {
        presenter.setup(view.tag as Int)
    }

    override fun showStateChanged(newState: TimerState) {
        print(newState)
        if (newState == TimerState.ready) {
            enableAll()
        } else {
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
