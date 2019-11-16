package dev.alessi.chunk.pomodoro.timer.android.ui.task


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.RepositoryProvider
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.repository.TaskRepository
import dev.alessi.chunk.pomodoro.timer.android.ui.MainViewModel
import dev.alessi.chunk.pomodoro.timer.android.ui.TaskSharedViewModel
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass.
 */
class TaskFragment : Fragment() {

    lateinit var mAdapter: TaskRecyclerAdapter

    lateinit var mTaskRepository: TaskRepository

    private val scope = CoroutineScope(Dispatchers.Main)

    private lateinit var mSharedViewModel: TaskSharedViewModel
    private lateinit var mainViewModel: MainViewModel

    companion object{
        const val extra_param_task_id = "extra_param_task_id"
    }


    override fun onAttach(context: Context) {
        mTaskRepository = (activity?.applicationContext as RepositoryProvider).getTaskRepository()
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSharedViewModel = activity?.run {
            ViewModelProviders.of(this)[TaskSharedViewModel::class.java]
        } ?: throw IllegalStateException("Invalid activity")

        activity?.setTitle(R.string.title_select_task)

        activity?.run {
            mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Throwable("invalid activity")

        mainViewModel.updateTitle(getString(R.string.title_select_task))

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mSharedViewModel.getTaskname().observe(viewLifecycleOwner, Observer {
            updateItems()

        })

        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false)
    }


    private fun onItemSelect(task: Task){
        mSharedViewModel.setTask(task)
        navigateBack()
    }

    private fun onDescriptionClick(task: Task){
        val args = Bundle()
        args.putInt(extra_param_task_id, task.uid!!)

        findNavController().navigate(R.id.addTaskDialog, args)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mAdapter = TaskRecyclerAdapter(arrayListOf(), ::onItemSelect, ::onDescriptionClick)
        recycler_view.adapter = mAdapter
        recycler_view.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)

        updateItems()

        fabAddTask.setOnClickListener(::actionOpenAddTaskDialog)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun navigateBack(){
        findNavController().navigate(R.id.timerFragment)
    }

    private fun actionOpenAddTaskDialog(view: View) {
        findNavController().navigate(R.id.addTaskDialog)
    }


    private fun updateItems() {
        scope.launch {
            val tasks = withContext(Dispatchers.IO) {
                val items = mTaskRepository.loadAllTasks()
                items.forEach {task ->
                    val wItems = mTaskRepository.loadAllFromTask(task.uid!!)
                    task.slices = wItems.toList()
                }
                items
            }
            mAdapter.updateItems(tasks.toList())
            recycler_view?.scrollToPosition(0)

        }

    }


}
