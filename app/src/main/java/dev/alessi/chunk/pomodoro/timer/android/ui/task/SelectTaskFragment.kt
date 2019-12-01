package dev.alessi.chunk.pomodoro.timer.android.ui.task


import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
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
import kotlinx.android.synthetic.main.fragment_select_task.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass.
 */
class SelectTaskFragment : Fragment() {

    lateinit var mAdapter: SelectTaskRecyclerAdapter

    lateinit var mTaskRepository: TaskRepository

    private val scope = CoroutineScope(Dispatchers.Main)

    private lateinit var mSharedViewModel: TaskSharedViewModel
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        mSharedViewModel = activity?.run {
            ViewModelProviders.of(this)[TaskSharedViewModel::class.java]
        } ?: throw IllegalStateException("Invalid activity")

        activity?.setTitle(R.string.title_select_task)

        activity?.run {
            mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Throwable("invalid activity")

        mainViewModel.updateTitle(getString(R.string.title_select_task))

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.select_task_menu, menu)

        val searchManager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.action_filter_task).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        searchView.maxWidth = Integer.MAX_VALUE

        searchView.setOnClickListener{view -> }

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mAdapter.filter.filter(newText)
                return false
            }

        })


        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.action_filter_task -> return true
        }

        return super.onOptionsItemSelected(item)
    }

    companion object{
        const val extra_param_task_id = "extra_param_task_id"

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_task, container, false)
    }

    override fun onAttach(context: Context) {
        mTaskRepository = (activity?.applicationContext as RepositoryProvider).getTaskRepository()
        super.onAttach(context)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mSharedViewModel.getTaskname().observe(viewLifecycleOwner, Observer {
            updateItems()

        })

        super.onActivityCreated(savedInstanceState)
    }


    private fun onItemSelect(task: Task){
        mSharedViewModel.setTask(task)
        navigateBack()
    }

    /*private fun onDescriptionClick(task: Task){
        val args = Bundle()
        args.putInt(extra_param_task_id, task.uid!!)

        findNavController().navigate(R.id.addTaskDialog, args)
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mAdapter = SelectTaskRecyclerAdapter(arrayListOf(), ::onItemSelect, ::onOpenTaskInfoClick)
        recycler_view_select_task.adapter = mAdapter
        val layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        recycler_view_select_task.layoutManager =
            layoutManager

//        recycler_view.addItemDecoration(DividerItemDecoration(recycler_view.context, layoutManager.orientation))

        updateItems()

        fabAddTask.setOnClickListener(::actionOpenAddTaskDialog)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun onOpenTaskInfoClick(task: Task) {
        val args = Bundle()
        args.putInt(extra_param_task_id, task.uid!!)
        findNavController().navigate(R.id.taskStatsFragment, args)
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
            recycler_view_select_task?.scrollToPosition(0)

        }

    }


}
