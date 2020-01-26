package dev.alessi.chunk.pomodoro.timer.android.ui.slice_history


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.ui.MainActivityControlViewModel
import dev.alessi.chunk.pomodoro.timer.android.ui.slice_history.adapter.OnFilterChangedListener
import dev.alessi.chunk.pomodoro.timer.android.ui.slice_history.adapter.SliceHistoryAdapter
import dev.alessi.chunk.pomodoro.timer.android.ui.task.SelectTaskFragment
import dev.alessi.chunk.pomodoro.timer.android.ui.task_stats.LoadPeriodSummariesViewModel
import kotlinx.android.synthetic.main.fragment_task_slice_history.*
import kotlinx.android.synthetic.main.layout_content_empty.*


/**
 * TODO alterar nome para task info alguma coisa ou task summary alguma coisa
 */
class TaskSliceHistoryFragment : Fragment(R.layout.fragment_task_slice_history), OnFilterChangedListener {

    private val mSummariesViewModel: LoadPeriodSummariesViewModel by viewModels (::requireParentFragment)
    private val mMainActivityControlViewModel: MainActivityControlViewModel by viewModels (::requireActivity)


    private var mTaskId: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        mMainActivityControlViewModel.updateTitle(getString(R.string.title_task_stats))


        super.onCreate(savedInstanceState)
    }


    private fun initViewModelsListeners() {
        val sliceHistoryAdapter = SliceHistoryAdapter(this)
        recycler_slice_history.adapter = sliceHistoryAdapter

        mSummariesViewModel.onPeriodsFromTaskLoadedObserver.observe(viewLifecycleOwner, Observer {
            setNoContent()
            if (it.isNotEmpty()) {
                sliceHistoryAdapter.setItems(it)
                setHasContent()
            }
        })
    }

    private fun setNoContent() {
//        recycler_view_task_stats.visibility = View.INVISIBLE
        label_content_empty.visibility = View.VISIBLE
    }

    private fun setHasContent() {
//        recycler_view_task_stats.visibility = View.VISIBLE
        label_content_empty.visibility = View.INVISIBLE

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler_slice_history.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)

        initViewModelsListeners()


        if (arguments != null) {
            mTaskId = arguments?.getInt(SelectTaskFragment.extra_param_task_id, -1)!!


            mSummariesViewModel.loadAndSummarize(Task(mTaskId))
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onFilterChanged() {
        mSummariesViewModel.loadAndSummarize(Task(mTaskId))
    }


}
