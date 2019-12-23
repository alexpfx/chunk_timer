package dev.alessi.chunk.pomodoro.timer.android.ui.estimate


import android.content.ClipData
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.SizeTimeCountTO
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
import dev.alessi.chunk.pomodoro.timer.android.domain.SizeValue
import dev.alessi.chunk.pomodoro.timer.android.ui.task.SelectTaskFragment
import dev.alessi.chunk.pomodoro.timer.android.ui.task.TaskViewModel
import dev.alessi.chunk.pomodoro.timer.android.util.RuntimeViewFactory
import dev.alessi.chunk.pomodoro.timer.android.util.ViewUtils
import dev.alessi.chunk.pomodoro.timer.android.util.ViewUtils.Companion.getSizeTimeCountTOFromTag
import dev.alessi.chunk.pomodoro.timer.android.util.toFormatedTime
import kotlinx.android.synthetic.main.fragment_estimate.*


/**
 * A simple [Fragment] subclass.
 */


class EstimateFragment : Fragment(), EstimativeActionListeners {
    companion object {
        const val tagSummary = "summaryButtons"
    }

    private lateinit var sizeMap: Map<Int, SizeValue>
    lateinit var rlInfoContent: FrameLayout
    lateinit var imageDragIconInfo: ImageView
    lateinit var textDragInfo: TextView

    lateinit var mEstimativeViewModel: EstimateViewModel
    lateinit var mLoadTaskViewModel: TaskViewModel
    lateinit var mTask: Task
    lateinit var mEstimativeAdapter: EstimateAdapter


    private var mMinutesEditable = 0

    private val editModeIcon: Drawable by lazy {
        ViewUtils.getDrawable(context!!, R.drawable.ic_mode_edit_black_24dp)
    }

    private val checkIcon: Drawable by lazy {
        ViewUtils.getDrawable(context!!, R.drawable.ic_check_black_24dp)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        sizeMap =
            SizeValue.loadMapFromPreferences(PreferenceManager.getDefaultSharedPreferences(this.context))

        mEstimativeViewModel = ViewModelProviders.of(this)[EstimateViewModel::class.java]

        mLoadTaskViewModel = activity?.run {
            ViewModelProviders.of(this)[TaskViewModel::class.java]
        } ?: throw IllegalArgumentException("activity nula")

        mEstimativeAdapter =
            EstimateAdapter(this)
        super.onCreate(savedInstanceState)

    }


    override val onBtnPlus = View.OnClickListener {
        val sizeTO = getSizeTimeCountTOFromTag(it)

        addToEstimative(sizeTO)

        updateTotalTime()


    }


    override val onBtnMinus = View.OnClickListener {
        val sizeTO = getSizeTimeCountTOFromTag(it)

        removeFromEstimative(sizeTO)

        updateTotalTime()


    }

    override val onBtnDelete = View.OnClickListener {
        val sizeTO = getSizeTimeCountTOFromTag(it)

        mEstimativeViewModel.removeAllEstimatives(
            WorkUnit(
                timeMinutes = sizeTO.minutes,
                sizeId = sizeTO.index,
                task = mTask,
                taskId = mTask.uid!!,
                estimative = 1
            )
        )



    }


    private val onAllEstimatives = Observer<List<SizeTimeCountTO>> { list ->
        mEstimativeAdapter.setItems(list)
        mMinutesEditable = list.fold(0, { total, next ->
            total + (next.timeMinutes * next.count)
        })

        updateTotalTime()
        updateDragHereMessage()

    }

    private val onAllEstimativesFor = Observer<SizeTimeCountTO> { entry ->
        mEstimativeAdapter.setItem(entry)
        println("onAllEstimativesFor $entry")

        updateTotalTime()
        updateDragHereMessage()

    }

    private fun updateTotalTime() {
        txt_estimative_total.text = mMinutesEditable.toFormatedTime()
    }

    private val onTaskLoaded = Observer<Task> {
        mTask = it
        updateUi()
    }

    private fun updateUi() {
        txt_task_desk.text = mTask.description
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        setupObservers()

        super.onActivityCreated(savedInstanceState)
    }

    private fun setupObservers() {
        mLoadTaskViewModel.taskLoadedObserver.observe(viewLifecycleOwner, onTaskLoaded)
        mEstimativeViewModel.onAllEstimativesLoaded.observe(viewLifecycleOwner, onAllEstimatives)
        mEstimativeViewModel.onAllEstimativesFor.observe(viewLifecycleOwner, onAllEstimativesFor)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_estimate, container, false)
    }


    private val onSizeButtonTouch = View.OnTouchListener { view, event ->
        when (event.action) {


            MotionEvent.ACTION_DOWN -> {
                val data = ClipData.newPlainText("Label", "Teste")

                val shadowBuilder = View.DragShadowBuilder(view)


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(data, shadowBuilder, view, View.DRAG_FLAG_OPAQUE)
                } else {
                    view.startDrag(data, shadowBuilder, view, 0)
                }

                view.visibility = View.INVISIBLE

                return@OnTouchListener true
            }

        }

        false
    }

    private val onEditModeClick = View.OnClickListener {
        val isEditMode = mEstimativeAdapter.toggleEditMode()

        if (!isEditMode){
            mEstimativeViewModel.loadAllEstimatives(mTask.uid!!)
        }
        updateEditModeIcon(isEditMode)

    }

    private fun updateEditModeIcon(editMode: Boolean) {
        if (!editMode) {
            btn_edit_mode.setImageDrawable(editModeIcon)
        } else {
            btn_edit_mode.setImageDrawable(checkIcon)
        }
    }

    private val onDrag = View.OnDragListener { targetView: View, dragEvent: DragEvent ->
        val originView = dragEvent.localState as View

        when (dragEvent.action) {

            DragEvent.ACTION_DRAG_ENDED -> {
                originView.visibility = View.VISIBLE

            }
            DragEvent.ACTION_DROP -> {
                val sizeValue = originView.tag as SizeValue
                addToEstimative(sizeValue)
            }

        }
        return@OnDragListener true


    }

    private fun removeFromEstimative(sizeValue: SizeValue) {
        mEstimativeViewModel.removeEstimative(
            WorkUnit(
                timeMinutes = sizeValue.minutes,
                sizeId = sizeValue.index,
                task = mTask,
                taskId = mTask.uid!!,
                estimative = 1
            )
        )

        mMinutesEditable -= sizeValue.minutes


    }

    private fun addToEstimative(sizeValue: SizeValue) {
        mEstimativeViewModel.storeEstimative(
            WorkUnit(
                timeMinutes = sizeValue.minutes,
                sizeId = sizeValue.index,
                task = mTask,
                taskId = mTask.uid!!,
                estimative = 1
            )
        )

        mMinutesEditable += sizeValue.minutes


    }

//    private fun hasChildren(tv: FrameLayout): Boolean {
//        flexbox_estimative.children.forEach {  }
//    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val taskId = arguments?.getInt(SelectTaskFragment.extra_param_task_id, -1)
            ?: throw IllegalArgumentException("Não passou a task")

        mLoadTaskViewModel.loadTask(taskId)
        val p = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f)
        p.gravity = Gravity.CENTER

        for (value in sizeMap.values) {
            val inflated = RuntimeViewFactory.inflateEstimativeButton(
                context!!,
                value,
                onTouchListener = onSizeButtonTouch
            )
            linearLayout_estimativeMenu.addView(inflated, p)
        }

        btn_edit_mode.setOnClickListener(onEditModeClick)


        recyclerEstimatives.setOnDragListener(onDrag)
        recyclerEstimatives.adapter = mEstimativeAdapter
        recyclerEstimatives.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)


        mEstimativeViewModel.loadAllEstimatives(taskId)


        updateDragHereMessage()

        rlInfoContent = LayoutInflater.from(context).inflate(
            R.layout.layout_drag_info,
            null,
            false
        ) as FrameLayout


        imageDragIconInfo = rlInfoContent.findViewById(R.id.img_drag_icon_info)
        textDragInfo = rlInfoContent.findViewById(R.id.text_drag_info)

        val constraintLayout = (view as ScrollView).children.first()

        (constraintLayout as ViewGroup).children.forEach {

            if (it is ImageView && it.tag != null) {
                it.setOnTouchListener(onSizeButtonTouch)

                val index = (it.tag as String).toInt()
                val sizeValue = sizeMap[index]
                it.tag = sizeValue!!
                it.setImageDrawable(ViewUtils.getSizeDrawable(sizeValue.index, context!!))
            }
        }

    }

    private fun updateDragHereMessage() {
        label_message_drag_here.visibility =
            if (mEstimativeAdapter.itemCount > 0) View.INVISIBLE else View.VISIBLE


    }


}
