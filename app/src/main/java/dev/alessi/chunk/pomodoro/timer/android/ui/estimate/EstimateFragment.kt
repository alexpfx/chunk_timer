package dev.alessi.chunk.pomodoro.timer.android.ui.estimate


import android.content.ClipData
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.JustifyContent
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
import dev.alessi.chunk.pomodoro.timer.android.domain.SizeValue
import dev.alessi.chunk.pomodoro.timer.android.ui.task.SelectTaskFragment
import dev.alessi.chunk.pomodoro.timer.android.ui.task.TaskViewModel
import dev.alessi.chunk.pomodoro.timer.android.util.RuntimeViewFactory
import dev.alessi.chunk.pomodoro.timer.android.util.ViewUtils
import dev.alessi.chunk.pomodoro.timer.android.util.debug
import dev.alessi.chunk.pomodoro.timer.android.util.toDip
import kotlinx.android.synthetic.main.fragment_estimate.*


/**
 * A simple [Fragment] subclass.
 */

class EstimateFragment : Fragment() {


    private lateinit var sizeMap: Map<Int, SizeValue>
    lateinit var rlInfoContent: FrameLayout
    lateinit var imageDragIconInfo: ImageView
    lateinit var textDragInfo: TextView

    lateinit var mEstimativeViewModel: EstimateViewModel
    lateinit var mLoadTaskViewModel: TaskViewModel
    lateinit var mTask: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        sizeMap =
            SizeValue.loadMapFromPreferences(PreferenceManager.getDefaultSharedPreferences(this.context))

        mEstimativeViewModel = ViewModelProviders.of(this)[EstimateViewModel::class.java]

        mLoadTaskViewModel = activity?.run {
            ViewModelProviders.of(this)[TaskViewModel::class.java]
        } ?: throw IllegalArgumentException("activity nula")

        super.onCreate(savedInstanceState)

    }

    private val onAddNewEstimativeObserver = Observer<WorkUnit> {
        val tv = RuntimeViewFactory.createEstimativeView(context!!, it.sizeId, it.timeMinutes, orderInLayout = it.sizeId)

        flexbox_estimative.addView(tv)

        val t = RuntimeViewFactory.createEstimativeView(context!!, it.sizeId, it.timeMinutes, orderInLayout = it.sizeId, info = "2x")

        flexbox_totalizer.addView(t)


        updateFlexbox()

        if (!asAdded){
            val tot = RuntimeViewFactory.createTotalizerText(context!!, "Total:", "2h 20m")
            flexbox_totalizer.addView(tot)
            asAdded = true
        }




    }

    var asAdded = false

    private val onAllEstimatives = Observer<List<WorkUnit>>{ list ->
        list.forEach{
            debug("pegou estimativa: $it")
        }
    }

    private val onTaskLoaded = Observer<Task> {
        mTask = it
        updateUi()
    }

    private fun updateUi() {
        txtTaskDesc.text = mTask.description
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        setupObservers()

        super.onActivityCreated(savedInstanceState)
    }

    private fun setupObservers() {
        mEstimativeViewModel.onNewEstimative.observe(viewLifecycleOwner, onAddNewEstimativeObserver)
        mLoadTaskViewModel.taskLoadedObserver.observe(viewLifecycleOwner, onTaskLoaded)
        mEstimativeViewModel.onAllEstimativesLoaded.observe(viewLifecycleOwner, onAllEstimatives)


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

                imageDragIconInfo.setImageDrawable((view as ImageView).drawable.constantState?.newDrawable())
                val sizeValue = view.tag as SizeValue

                textDragInfo.text =
                    getString(R.string.label_format_abbrev_minutes, sizeValue.minutes)


                val shadowBuilder = ViewUtils.createShadowBuilder(
                    rlInfoContent,
                    view,
                    fingerOffset = Point(view.width, view.height)
                )


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


    private fun addToEstimative(sizeValue: SizeValue) {
        mEstimativeViewModel.storeEstimative(
            WorkUnit(
                timeMinutes = sizeValue.minutes,
                sizeId = sizeValue.index,
                task = mTask,
                taskId = mTask.uid!!
            )
        )




    }

//    private fun hasChildren(tv: FrameLayout): Boolean {
//        flexbox_estimative.children.forEach {  }
//    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val taskId = arguments?.getInt(SelectTaskFragment.extra_param_task_id, -1)
            ?: throw IllegalArgumentException("Não passou a task")

        mEstimativeViewModel.loadAllEstimatives(taskId)

        updateFlexbox()


        mLoadTaskViewModel.loadTask(taskId)


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
                it.setImageDrawable(RuntimeViewFactory.getSizeDrawable(sizeValue.index, context!!))


            }
        }

        flexbox_estimative.setOnDragListener(onDrag)
    }

    private fun updateFlexbox() {

        setMinFlexboxHeight(flexbox_totalizer, 72)
        setMinFlexboxHeight(flexbox_estimative)

        if (flexbox_totalizer.childCount > 4){
            flexbox_totalizer.justifyContent = JustifyContent.SPACE_BETWEEN
        }else{
            flexbox_totalizer.justifyContent = JustifyContent.FLEX_START
        }

    }

    private fun setMinFlexboxHeight(flex: FlexboxLayout, minHeight: Int = 100) {
        if (flex.childCount == 0) {
            flex.layoutParams.height = minHeight.toDip(context!!)
        } else {
            flex.layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT

        }
    }


}
