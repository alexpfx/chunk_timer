package dev.alessi.chunk.pomodoro.timer.android.ui.task_stats


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.RepositoryProvider
import dev.alessi.chunk.pomodoro.timer.android.database.SizeIndex
import dev.alessi.chunk.pomodoro.timer.android.repository.TaskRepository
import dev.alessi.chunk.pomodoro.timer.android.ui.MainViewModel
import dev.alessi.chunk.pomodoro.timer.android.ui.task.SelectTaskFragment
import kotlinx.android.synthetic.main.fragment_task_stats.*


/**
 * TODO alterar nome para task info alguma coisa ou task summary alguma coisa
 */
class TaskStatsFragment : Fragment() {


    lateinit var mAdapter: TaskStatsRecyclerAdapter
    lateinit var mTaskRepository: TaskRepository
    lateinit var mSummariesViewModel: LoadPeriodSummariesViewModel
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.run {
            mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Throwable("invalid activity")


        mainViewModel.updateTitle(getString(R.string.title_task_stats))

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_stats, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewModels()

        recycler_view_task_stats.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        recycler_view_task_stats.adapter = TaskStatsRecyclerAdapter(arrayListOf())


        if (arguments != null) {
            val taskId = arguments?.getInt(SelectTaskFragment.extra_param_task_id, -1)!!
            mSummariesViewModel.loadAndSummarize(taskId)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViewModels() {
        mSummariesViewModel = activity?.run {
            ViewModelProviders.of(this, object : Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return LoadPeriodSummariesViewModel(mTaskRepository) as T
                }

            })[LoadPeriodSummariesViewModel::class.java]
        } ?: throw Throwable("activity cannot be null")

        mSummariesViewModel.summaries.observe(viewLifecycleOwner, Observer {
            mAdapter = TaskStatsRecyclerAdapter(it)
            recycler_view_task_stats.swapAdapter(mAdapter, true)
        })

    }

    override fun onAttach(context: Context) {
        mTaskRepository = (activity?.applicationContext as RepositoryProvider).getTaskRepository()
        super.onAttach(context)
    }

    private fun createFakeItems(): ArrayList<PeriodSummaryTO> {
        return arrayListOf(PeriodSummaryTO(PeriodSummaryTO.Period.THIS_WEEK, 15, sizeMap = mutableMapOf(
            SizeIndex.PP to 0,
            SizeIndex.P to 0,
            SizeIndex.M to 0,
            SizeIndex.G to 0,
            SizeIndex.GG to 0
        )))
    }


}
