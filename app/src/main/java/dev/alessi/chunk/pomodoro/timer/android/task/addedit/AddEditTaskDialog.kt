package dev.alessi.chunk.pomodoro.timer.android.task.addedit

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.util.TextWatcherAdapter
import java.util.*

class AddEditTaskDialog : DialogFragment() {

    private lateinit var mEdtTaskName: TextInputEditText
    private lateinit var mEdtTaskDescription: TextInputEditText


    private lateinit var mInputTaskName: TextInputLayout
    private lateinit var mInputTaskDescription: TextInputLayout
    private lateinit var mPositiveButton: Button
    private var mTask: Task = Task(description = "", uid = null)

    private val mAddEditTaskviewModel: AddEditTaskSharedViewModel by viewModels(::requireActivity)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        val dialog = activity?.let {
            val builder = MaterialAlertDialogBuilder(
                it,
                R.style.AlertDialogTheme
            )

            val titleRes = R.string.title_new_task

            builder.setTitle(titleRes)
            builder.setView(LayoutInflater.from(activity).inflate(R.layout.dialog_add_task, null))
            builder.setPositiveButton(android.R.string.ok) { _, _ -> }


            builder.setNegativeButton(android.R.string.no) { _, _ ->
                run {
                    dialog?.dismiss()
                }
            }


            builder.create()
            builder.show()
        } ?: throw IllegalStateException("activity cannot be null")

        mPositiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        mPositiveButton.setOnClickListener(::onPositiveButtonClick)


        mEdtTaskDescription = dialog.findViewById(R.id.edt_task_description)!!
        mInputTaskDescription = dialog.findViewById(R.id.input_layout_description)!!

        mEdtTaskName = dialog.findViewById(R.id.edt_task_name)!!
        mInputTaskName = dialog.findViewById(R.id.input_layout_task_name)!!

        mEdtTaskName.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable?) {
                mTask.name = s?.toString() ?: ""
                if (mTask.name.isNotEmpty()) {
                    mInputTaskDescription.isErrorEnabled = false
                }
            }
        })

        mEdtTaskDescription.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable?) {
                mTask.description = s?.toString() ?: ""
            }
        })


        if (arguments != null) {
            /*     isEditMode = true
                 dialog.setTitle(R.string.title_edit_task)
                 loadTask(arguments!!.getInt(SelectTaskFragment.extra_param_task_id))*/

        }

        return dialog
    }


    /*private fun loadTask(tid: Int) {
        scope.launch {
            mTask = withContext(Dispatchers.IO) {
                mTaskRepository.loadTask(tid)
            }
            updateUi()
        }

    }*/

    private fun updateUi() {

        mEdtTaskDescription.setText(mTask.description)
    }


    private fun saveTask() {
        mAddEditTaskviewModel.saveTask(
            Task(
                uid = null,
                name = mTask.name,
                description = mTask.description,
                dateCreated = Date()
            )
        )
    }


    private fun onPositiveButtonClick(view: View) {
        if (mTask.description.isEmpty()) {
            mInputTaskDescription.isErrorEnabled = true
            mInputTaskDescription.error = getString(R.string.message_error_input_break_time_empty)

        } else {
            saveTask()
            dismiss()
        }


    }


}