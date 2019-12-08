package dev.alessi.chunk.pomodoro.timer.android.ui.task_stats


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.ui.MainSharedViewModel
import dev.alessi.chunk.pomodoro.timer.android.ui.task.SelectTaskFragment
import kotlinx.android.synthetic.main.fragment_select_task.*
import kotlinx.android.synthetic.main.fragment_task_stats.*
import kotlinx.android.synthetic.main.layout_content_empty.*


/**
 * TODO alterar nome para task info alguma coisa ou task summary alguma coisa
 */
class TaskStatsFragment : Fragment() {

    lateinit var mAdapter: TaskStatsRecyclerAdapter

    lateinit var mSummariesViewModel: LoadPeriodSummariesViewModel
    lateinit var mainSharedViewModel: MainSharedViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.run {
            mainSharedViewModel = ViewModelProviders.of(this).get(MainSharedViewModel::class.java)
        } ?: throw Throwable("invalid activity")

        mSummariesViewModel =
            ViewModelProviders.of(this).get(LoadPeriodSummariesViewModel::class.java)

        mainSharedViewModel.updateTitle(getString(R.string.title_task_stats))

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_stats, container, false)
    }


    private fun initViewModelsListeners() {
        mSummariesViewModel.onPeriodsFromTaskLoadedObserver.observe(viewLifecycleOwner, Observer {
            setNoContent()
            if (!it.isAllEmpty()) {
                mAdapter = TaskStatsRecyclerAdapter(it.periods)
                recycler_view_task_stats.swapAdapter(mAdapter, true)
                setHasContent()
            }
        })

    }

    private fun setNoContent() {
        recycler_view_task_stats.visibility = View.INVISIBLE
        label_content_empty.visibility = View.VISIBLE
    }

    private fun setHasContent() {
        recycler_view_task_stats.visibility = View.VISIBLE
        label_content_empty.visibility = View.INVISIBLE

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recycler_view_task_stats.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        recycler_view_task_stats.adapter = TaskStatsRecyclerAdapter(arrayListOf())


        if (arguments != null) {
            val taskId = arguments?.getInt(SelectTaskFragment.extra_param_task_id, -1)!!
            mSummariesViewModel.loadAndSummarize(Task(taskId))
        }

        initViewModelsListeners()

        super.onViewCreated(view, savedInstanceState)
    }


}
