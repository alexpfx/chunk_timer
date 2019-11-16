package dev.alessi.chunk.pomodoro.timer.android.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.RepositoryProvider
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.repository.TaskRepository
import dev.alessi.chunk.pomodoro.timer.android.ui.TaskSharedViewModel
import dev.alessi.chunk.pomodoro.timer.android.ui.task.TaskFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class AddEditTaskDialog : DialogFragment(), TextWatcher {

    private val scope = CoroutineScope(Dispatchers.Main)
    lateinit var mSharedViewModel: TaskSharedViewModel
    lateinit var mEdtTask: TextInputEditText
    lateinit var mInputTask: TextInputLayout
    lateinit var mPositiveButton: Button
    var mTask: Task = Task(description = "", uid = null)
    lateinit var mTaskRepository: TaskRepository
    var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        mTaskRepository = activity?.applicationContext.run {
            (this as RepositoryProvider).getTaskRepository()
        }

        mSharedViewModel = activity?.run {
            ViewModelProviders.of(this)[TaskSharedViewModel::class.java]
        } ?: throw IllegalStateException("Invalid activity")

        super.onCreate(savedInstanceState)
    }


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

        mEdtTask = dialog.findViewById(R.id.edtTaskName)!!
        mInputTask = dialog.findViewById(R.id.inputLayoutTaskname)!!

        mEdtTask.addTextChangedListener(this)


        if (arguments != null) {
            isEditMode = true
            dialog.setTitle(R.string.title_edit_task)
            loadTask(arguments!!.getInt(TaskFragment.extra_param_task_id))

        }

        return dialog
    }



    private fun loadTask(tid: Int) {
        scope.launch {
            mTask = withContext(Dispatchers.IO) {
                mTaskRepository.loadTask(tid)
            }
            updateUi()
        }

    }

    private fun updateUi()  {

        mEdtTask.setText(mTask.description)
    }


    private fun saveTask() {

        scope.launch {
            withContext(Dispatchers.IO) {
                if (!isEditMode){
                    mTaskRepository.storeTask(Task(uid = null, description = mTask.description, dateCreated = Date()))
                }else{
                    mTaskRepository.updateTask(mTask)
                }
            }
            mSharedViewModel.setTaskname(mTask.description)

        }


    }

    override fun afterTextChanged(s: Editable?) {
        mTask.description = s?.toString() ?: ""
        mInputTask.isErrorEnabled = false
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    private fun onPositiveButtonClick(view: View) {
        if (mTask.description.isEmpty()) {
            mInputTask.isErrorEnabled = true
            mInputTask.error = getString(R.string.message_error_input_break_time_empty)

        } else {
            saveTask()
            dismiss()
        }


    }


}