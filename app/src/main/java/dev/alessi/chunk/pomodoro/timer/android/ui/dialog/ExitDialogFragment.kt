package dev.alessi.chunk.pomodoro.timer.android.ui.dialog

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.alessi.chunk.pomodoro.timer.android.R

class ExitDialogFragment : DialogFragment() {

    private fun exitApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.finishAndRemoveTask()
        } else {
            activity?.finishAffinity()
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(
                it,
                R.style.AlertDialogTheme
            )

            builder.setMessage(R.string.message_exit_app)

            builder.setPositiveButton(
                android.R.string.ok
            ) { _, _ ->
                exitApp()
            }

            builder.setNegativeButton(android.R.string.no) { _, _ ->
                run {
                    dialog?.cancel()
                }
            }


            builder.create()
        } ?: throw IllegalStateException("activity cannot be null")
    }

}