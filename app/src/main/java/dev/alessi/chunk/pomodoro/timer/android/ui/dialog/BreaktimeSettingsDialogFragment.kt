package dev.alessi.chunk.pomodoro.timer.android.ui.dialog

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerSharedViewModel
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerFragment

class BreaktimeSettingsDialogFragment : DialogFragment(),
    TextWatcher {

    private lateinit var inputTextMinutes: AppCompatEditText
    private lateinit var inputLayoutTextMinutes: TextInputLayout
    private lateinit var positiveButton: Button
    private lateinit var mTimerSharedModel: TimerSharedViewModel
    private lateinit var mRestoreDefaultButton: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {

        mTimerSharedModel = activity?.run {
            ViewModelProviders.of(this)[TimerSharedViewModel::class.java]
        } ?: throw IllegalStateException("Invalid activity")

        super.onCreate(savedInstanceState)
    }


    override fun afterTextChanged(s: Editable?) {
        validateForm()

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }


    private fun onRestoreDefaultClick(view: View){
        restoreDefault()
    }

    private fun restoreDefault(){
        val default = TimerFragment.DEFAULT_TIME_BREAK
        inputTextMinutes.setText(default.toString())

    }

    private fun saveBreakTime() {
        val minutes: Int = inputTextMinutes.text.toString().toInt()
        mTimerSharedModel.setBreaktime(minutes)

    }

    private fun getPreferenceManager(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun validateForm() {
        val inputMinutes = inputTextMinutes.text.toString()
        if (inputMinutes.isEmpty()) {
            inputLayoutTextMinutes.isErrorEnabled = true
            inputLayoutTextMinutes.error = getString(R.string.message_error_input_break_time_empty)
            positiveButton.visibility = View.INVISIBLE
        } else if (inputMinutes.toInt() <= 0) {
            inputLayoutTextMinutes.isErrorEnabled = true
            inputLayoutTextMinutes.error =
                getString(R.string.message_error_input_break_time_minutes_less_equal_zero)
            positiveButton.visibility = View.INVISIBLE
        } else {
            inputLayoutTextMinutes.isErrorEnabled = false
            positiveButton.visibility = View.VISIBLE
        }

    }




    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = activity?.let {
            val builder = MaterialAlertDialogBuilder(
                this.activity,
                R.style.AlertDialogTheme
            )

            builder.setTitle(R.string.dialog_title_interval_time)
            builder.setMessage(R.string.dialog_message_set_interval_time)


            val view =
                LayoutInflater.from(this.activity)
                    .inflate(R.layout.dialog_input_break_time, null)

            inputTextMinutes = view.findViewById(R.id.inputTextNewBreakTime)
            inputLayoutTextMinutes = view.findViewById(R.id.inputLayoutNewBreakTime)
            inputTextMinutes.addTextChangedListener(this)

            mRestoreDefaultButton = view.findViewById(R.id.btnRestoreDefaults)
            mRestoreDefaultButton.setOnClickListener(::onRestoreDefaultClick)

            builder.setView(view)

            builder.setPositiveButton(
                android.R.string.ok
            ) { _, _ ->
                saveBreakTime()


            }.setNegativeButton(android.R.string.no) { _, _ ->
                run {
                    dialog?.cancel()
                }
            }

            builder.create()
            builder.show()

        } ?: throw IllegalStateException("activity cannot be null")
        positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

        val p = getPreferenceManager()
        val minutes = p.getInt(TimerFragment.KEY_LAST_BREAKTIME, 10).toString()

        inputTextMinutes.setText(minutes)

        validateForm()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        inputTextMinutes.clearFocus()
        return dialog
    }

}