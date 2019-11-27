package dev.alessi.chunk.pomodoro.timer.android.ui.task_stats


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.RepositoryProvider
import dev.alessi.chunk.pomodoro.timer.android.database.SizeIndex
import dev.alessi.chunk.pomodoro.timer.android.repository.TaskRepository
import dev.alessi.chunk.pomodoro.timer.android.ui.task.SelectTaskFragment
import kotlinx.android.synthetic.main.fragment_task_stats.*
import java.util.*

/**
 * TODO alterar nome para task info alguma coisa ou task summary alguma coisa
 */
class TaskStatsFragment : Fragment() {


    lateinit var mAdapter: TaskStatsRecyclerAdapter
    lateinit var mTaskRepository: TaskRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        recycler_view_task_stats.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        mAdapter = TaskStatsRecyclerAdapter(createFakeItems())
        recycler_view_task_stats.adapter = mAdapter
        if (arguments != null){
            val taskId = arguments?.getInt(SelectTaskFragment.extra_param_task_id, -1)


        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        mTaskRepository = (activity?.applicationContext as RepositoryProvider).getTaskRepository()
        super.onAttach(context)
    }

    private fun createFakeItems(): ArrayList<PeriodSummaryTO> {
        return arrayListOf(PeriodSummaryTO("Hoje", "23h 15m", sizeMap = mutableMapOf(
            SizeIndex.PP to 0,
            SizeIndex.P to 0,
            SizeIndex.M to 0,
            SizeIndex.G to 0,
            SizeIndex.GG to 0
        )))
    }


}
