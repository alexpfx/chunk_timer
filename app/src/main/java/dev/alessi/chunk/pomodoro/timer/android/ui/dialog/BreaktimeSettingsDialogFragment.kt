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
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.ui.ConfigTimersSharedViewModel
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerFragment

const val DEFAULT_JSON_BREAKS = "[5,10,20]"

class BreaktimeSettingsDialogFragment : DialogFragment(),
    TextWatcher {

    private lateinit var inputTextMinutes: AppCompatEditText
    private lateinit var inputLayoutTextMinutes: TextInputLayout
    private lateinit var positiveButton: Button
    private val mConfigTimersSharedModel: ConfigTimersSharedViewModel by viewModels(::requireActivity)
    private lateinit var mRestoreDefaultButton: MaterialButton
    private var mGson: Gson = Gson()
    private var mBreaktimes = listOf(10, 15, 20)

    override fun afterTextChanged(s: Editable?) {
        validateForm()

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }


    private fun onRestoreDefaultClick(view: View) {
        restoreDefault()
    }

    private fun restoreDefault() {
        val default = TimerFragment.DEFAULT_TIME_BREAK
        inputTextMinutes.setText(default.toString())

    }

    private fun saveBreakTime() {
        val it: Int = inputTextMinutes.text.toString().toInt()


        val i = mBreaktimes.indexOf(it)
        mBreaktimes = if (i == -1)
            arrayListOf(it) + mBreaktimes.dropLast(1)
        else
            arrayListOf(it) + mBreaktimes.minus(it)

        val p = getPrefences()

        p.edit().putString(TimerFragment.KEY_BREAKTIME_JSON_ARRAY, mGson.toJson(mBreaktimes)).apply()



    }


    private fun getPrefences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun validateForm() {
        val inputMinutes = inputTextMinutes.text.toString()
        when {
            inputMinutes.isEmpty() -> {
                inputLayoutTextMinutes.isErrorEnabled = true
                inputLayoutTextMinutes.error =
                    getString(R.string.message_error_input_break_time_empty)
                positiveButton.visibility = View.INVISIBLE
            }
            inputMinutes.toInt() <= 0 -> {
                inputLayoutTextMinutes.isErrorEnabled = true
                inputLayoutTextMinutes.error =
                    getString(R.string.message_error_input_break_time_minutes_less_equal_zero)
                positiveButton.visibility = View.INVISIBLE
            }
            else -> {
                inputLayoutTextMinutes.isErrorEnabled = false
                positiveButton.visibility = View.VISIBLE
            }
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
                mConfigTimersSharedModel.setBreaktimes(mBreaktimes)

            }.setNegativeButton(android.R.string.no) { _, _ ->
                run {
                    dialog?.cancel()
                }
            }

            builder.create()
            builder.show()

        } ?: throw IllegalStateException("activity cannot be null")
        positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

        loadBreaktimes()
//        val lastIndex = p.getInt(TimerFragment.KEY_LAST_BREAKTIME_INDEX, 0)

        val minutes = mBreaktimes[0]

        inputTextMinutes.setText(minutes.toString())

        validateForm()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        inputTextMinutes.clearFocus()
        return dialog
    }

    private fun loadBreaktimes() {
        val p = getPrefences()
        val json = p.getString(TimerFragment.KEY_BREAKTIME_JSON_ARRAY, DEFAULT_JSON_BREAKS)
        mBreaktimes = mGson.fromJson(json, Array<Int>::class.java).toList()
    }

}
