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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.task.addedit.AddEditTaskSharedViewModel
import dev.alessi.chunk.pomodoro.timer.android.ui.MainSharedViewModel
import dev.alessi.chunk.pomodoro.timer.android.ui.SelectTaskSharedViewModel
import dev.alessi.chunk.pomodoro.timer.android.ui.task_stats.LoadPeriodSummariesViewModel
import dev.alessi.chunk.pomodoro.timer.android.util.bool
import kotlinx.android.synthetic.main.fragment_select_task.*
import kotlinx.android.synthetic.main.layout_content_empty.*


class SelectTaskFragment : Fragment() {

    lateinit var mAdapter: SelectTaskRecyclerAdapter

    private lateinit var mSummariesViewModel: LoadPeriodSummariesViewModel

    private lateinit var mSelectTaskViewModel: SelectTaskSharedViewModel

    private lateinit var mainSharedViewModel: MainSharedViewModel

    private lateinit var mTaskViewModel: TaskViewModel

    private lateinit var mAddEditTaskSharedViewModel: AddEditTaskSharedViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        mTaskViewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)

        mAddEditTaskSharedViewModel = activity?.run {
            ViewModelProviders.of(this)[AddEditTaskSharedViewModel::class.java]
        } ?: throw IllegalStateException("Invalid activity")


        mSummariesViewModel =
            ViewModelProviders.of(this).get(LoadPeriodSummariesViewModel::class.java)

        mSelectTaskViewModel = activity?.run {
            ViewModelProviders.of(this)[SelectTaskSharedViewModel::class.java]
        } ?: throw IllegalStateException("Invalid activity")

        activity?.setTitle(R.string.title_select_task)

        activity?.run {
            mainSharedViewModel = ViewModelProviders.of(this).get(MainSharedViewModel::class.java)
        } ?: throw Throwable("invalid activity")

        mainSharedViewModel.updateTitle(getString(R.string.title_select_task))

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.select_task_menu, menu)

        val searchManager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.action_filter_task).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        searchView.maxWidth = Integer.MAX_VALUE

        searchView.setOnClickListener { view -> }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

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
        when (id) {
            R.id.action_filter_task -> return true
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val extra_param_task_id = "extra_param_task_id"

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_task, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {


        initViewModelsListeners()


        super.onActivityCreated(savedInstanceState)
    }


    private val onArchiveActionObserver = Observer<Task> { task ->
        val contextView = view?.findViewById<View>(R.id.recycler_view_select_task)!!
        if (task.archived.bool()) {
            Snackbar.make(contextView, R.string.message_task_archived, Snackbar.LENGTH_LONG)
                .setAction(R.string.message_action_undo) {
                    mTaskViewModel.unarchiveTask(task)
                }.show()
        } else {
            Snackbar.make(contextView, R.string.message_task_unarchived, Snackbar.LENGTH_SHORT)
                .show()
            mSummariesViewModel.loadAllAndSummarize()
        }
    }


    private fun onItemSelect(task: SelectTaskTO) {
        mSelectTaskViewModel.selectTask(task.task)
        navigateBack()

    }


    private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            if (mAdapter.itemCount > 0) {
                setHasContent()
            } else {
                setNoContent()
            }

        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {

            onChanged()
        }


    }

    override fun onStart() {
        mAdapter.registerAdapterDataObserver(adapterDataObserver)
        super.onStart()
    }

    override fun onStop() {

        mAdapter.unregisterAdapterDataObserver(adapterDataObserver)

        super.onStop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mAdapter = SelectTaskRecyclerAdapter(
            arrayListOf(),
            ::onItemSelect,
            ::onOpenTaskInfoClick,
            ::onArchiveClick
        )

        initViewModelsListeners()

        recycler_view_select_task.adapter = mAdapter
        val layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        recycler_view_select_task.layoutManager =
            layoutManager


        val dividerItemDecoration =
            DividerItemDecoration(recycler_view_select_task.context, layoutManager.orientation)


//        recycler_view_select_task.addItemDecoration(dividerItemDecoration)

        mSummariesViewModel.loadAllAndSummarize()


        fabAddTask.setOnClickListener(::actionOpenAddTaskDialog)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViewModelsListeners() {
        mAddEditTaskSharedViewModel.onNewTaskObserver.observe(viewLifecycleOwner, (Observer {
            mSummariesViewModel.loadAllAndSummarize()
        }))

        mTaskViewModel.archiveActionObserver.observe(
            viewLifecycleOwner,
            onArchiveActionObserver
        )

        mSummariesViewModel.onPeriodsLoadedObserver.observe(
            viewLifecycleOwner,
            Observer {
                mAdapter.setItems(it)
            }
        )

    }

    private fun setNoContent() {
        layout_content.visibility = View.INVISIBLE
        label_content_empty.visibility = View.VISIBLE
    }

    private fun setHasContent() {
        layout_content.visibility = View.VISIBLE
        label_content_empty.visibility = View.INVISIBLE

    }


    private fun onArchiveClick(task: Task) {
        mTaskViewModel.archiveTask(task)

    }

    private fun onOpenTaskInfoClick(taskSummaries: SelectTaskTO) {
        val args = Bundle()
        args.putInt(extra_param_task_id, taskSummaries.task.uid!!)
        findNavController().navigate(R.id.taskStatsFragment, args)
    }

    private fun navigateBack() {
        findNavController().navigate(R.id.timerFragment)
    }


    private fun actionOpenAddTaskDialog(view: View) =
        findNavController().navigate(R.id.addTaskDialog)


}
