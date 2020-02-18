package dev.alessi.chunk.pomodoro.timer.android.ui.slice_history


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.databinding.FragmentTaskInfoBinding

import dev.alessi.chunk.pomodoro.timer.android.ui.MainActivityControlViewModel
import dev.alessi.chunk.pomodoro.timer.android.ui.slice_history.adapter.OnFilterChangedListener
import dev.alessi.chunk.pomodoro.timer.android.ui.slice_history.adapter.SliceHistoryAdapter
import dev.alessi.chunk.pomodoro.timer.android.ui.task.SelectTaskFragment
import dev.alessi.chunk.pomodoro.timer.android.ui.task_stats.LoadPeriodSummariesViewModel

class TaskSliceHistoryFragment : Fragment(R.layout.fragment_task_info), OnFilterChangedListener {

    private var _binding: FragmentTaskInfoBinding? = null
    private val binding get() = _binding!!
    private val mSummariesViewModel: LoadPeriodSummariesViewModel by viewModels ()
    private val mMainActivityControlViewModel: MainActivityControlViewModel by viewModels (::requireActivity)


    private var mTaskId: Int = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentTaskInfoBinding.bind(view!!)

        return view

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        mMainActivityControlViewModel.updateTitle(getString(R.string.title_task_infos))


        super.onCreate(savedInstanceState)
    }


    private fun initViewModelsListeners() {
        val sliceHistoryAdapter = SliceHistoryAdapter(this)
        binding.recyclerSliceHistory.adapter = sliceHistoryAdapter

        mSummariesViewModel.onPeriodsFromTaskLoadedObserver.observe(viewLifecycleOwner, Observer {
            setNoContent()
            if (it.isNotEmpty()) {
                sliceHistoryAdapter.setItems(it)
                setHasContent()
            }
        })
    }

    private fun setNoContent() {

        /*binding.labelContentEmpty.visibility = View.VISIBLE*/
    }

    private fun setHasContent() {

        /*binding.labelContentEmpty.visibility = View.INVISIBLE*/

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerSliceHistory.layoutManager =
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
