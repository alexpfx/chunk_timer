package dev.alessi.chunk.pomodoro.timer.android.ui.task


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
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.repository.TaskRepository
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

    override fun onAttach(context: Context) {
        mTaskRepository = (activity?.applicationContext as RepositoryProvider).getTaskRepository()
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mAdapter = TaskRecyclerAdapter(arrayListOf())
        recycler_view.adapter = mAdapter
        recycler_view.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)

        updateItems()

        super.onViewCreated(view, savedInstanceState)
    }




    private fun updateItems() {
        scope.launch {
            val items = withContext(Dispatchers.IO) {
                mTaskRepository.loadAllTasks()
            }
            mAdapter.updateItems(items.toList())
        }


    }


}
