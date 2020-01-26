package dev.alessi.chunk.pomodoro.timer.android.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerFragment.Companion.DEFAULT_JSON_SIZES
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerFragment.Companion.KEY_SIZE_JSON_ARRAY
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerSharedViewModel

class TimerSettingsDialogFragment : AppCompatDialogFragment() {

    private val mTimerSharedModel: TimerSharedViewModel by viewModels(::requireActivity)
    private lateinit var positiveButton: Button
    private lateinit var edts: Set<TextInputEditText>
    private lateinit var mBtnRestoreDefaults: MaterialButton
    private lateinit var neutralButton: Button

    private val gson: Gson = Gson()


    private fun validateNotEmpty(): Boolean {
        for (it in edts) {
            it.error = null

            val text = it.text
            if (text?.isEmpty()!!) {
                it.requestFocus()
                it.error = getString(R.string.message_error_time_empty)
                return false
            }
        }
        return true
    }


    private fun validateSort(): Boolean {
        var lastValue: Int? = null


        for ((index, it) in edts.withIndex()) {
            it.error = null

            val value = it.text.toString().toInt()
            if (lastValue != null && value < lastValue) {
                it.requestFocus()
                it.error = getString(R.string.message_error_required_sorted_sizes)

                if (index == edts.size - 1) {
//                    mSpace.visibility = View.VISIBLE

                }

                return false
            }
            lastValue = value
        }


        return true
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val pm = PreferenceManager.getDefaultSharedPreferences(activity)


        val jsonSizes = pm.getString(KEY_SIZE_JSON_ARRAY, DEFAULT_JSON_SIZES)
        val sizesArray = gson.fromJson(jsonSizes, Array<Int>::class.java)


        val dialog = activity?.let {


            val builder = MaterialAlertDialogBuilder(
                it, R.style.AlertDialogTheme
            )

            builder.setTitle(R.string.dialog_title_sizes)
            builder.setMessage(R.string.dialog_message_sizes)

            builder.setView(R.layout.dialog_setup_timer_sizes)


            builder.setNeutralButton(R.string.button_label_sort) { _, _ -> }
            builder.setPositiveButton(android.R.string.ok) { _, _ -> }

            builder.setNegativeButton(android.R.string.no) { _, _ ->
                run {
                    dialog?.dismiss()
                }
            }

            builder.create()
            builder.show()

        } ?: throw IllegalStateException("activity cannot be null")

        edts = extractViews(dialog)
        updateEdtValues(edts, sizesArray)

        positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener(::onPositiveButtonClick)

        mBtnRestoreDefaults = dialog.findViewById(R.id.btnRestoreDefaults)!!
        mBtnRestoreDefaults.setOnClickListener(::onRestoreDefaultsClick)

        neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
        neutralButton.setOnClickListener(::onSortButtonClick)
        neutralButton.visibility = View.INVISIBLE

//        mSpace = dialog.findViewById(R.id.space)!!

        return dialog
    }

    private fun onRestoreDefaultsClick(view: View) {
        val sizesArray = gson.fromJson(DEFAULT_JSON_SIZES, Array<Int>::class.java)

        updateEdtValues(edts, sizesArray)

    }

    private fun onSortButtonClick(view: View) {
        if (!validateNotEmpty()) {
            return
        }

        updateEdtValues(edts, extractValues(edts))
        positiveButton.visibility = View.VISIBLE
        neutralButton.visibility = View.INVISIBLE
//        mSpace.visibility = View.GONE

    }


    private fun onPositiveButtonClick(view: View) {

        if (!validateNotEmpty()) {
            return
        }

        if (!validateSort()) {
            neutralButton.visibility = View.VISIBLE
            positiveButton.visibility = View.INVISIBLE
            return
        }

        saveSizes()
        dismiss()

    }

    private fun saveSizes() {
        val values = extractValues(edts)
        mTimerSharedModel.setSizes(values.toList())
    }


    private fun updateEdtValues(edts: Set<TextInputEditText>, sizes: Array<Int>) {
        sizes.forEachIndexed { index, value ->
            val edt = edts.elementAt(index)
            edt.setText(value.toString())
        }
    }


    private fun extractValues(edts: Set<TextInputEditText>): Array<Int> {
        return edts.map {
            it.text.toString().toInt()
        }.sorted().toTypedArray()
    }

    private fun extractViews(dialog: AlertDialog): Set<TextInputEditText> {
        val pp = dialog.findViewById<TextInputEditText>(R.id.edtSizePP)!!
        val p = dialog.findViewById<TextInputEditText>(R.id.edtSizeP)!!
        val m = dialog.findViewById<TextInputEditText>(R.id.edtSizeM)!!
        val g = dialog.findViewById<TextInputEditText>(R.id.edtSizeG)!!
        val gg = dialog.findViewById<TextInputEditText>(R.id.edtSizeGG)!!
        return setOf(pp, p, m, g, gg)
//        return setOf(pp)

    }

}