package dev.alessi.chunk.pomodoro.timer.android.ui.task


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import kotlinx.android.synthetic.main.fragment_task.*

/**
 * A simple [Fragment] subclass.
 */
class TaskFragment : Fragment() {


    lateinit var mAdapter:  TaskRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mAdapter = TaskRecyclerAdapter(createFakeItems())
        recycler_view.adapter = mAdapter
        recycler_view.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun createFakeItems(): List<Task> {
        return arrayListOf(Task(1, "teste"), Task(2, "teste 2 "))
    }


}
