package dev.alessi.chunk.pomodoro.timer.android


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mehdi.sakout.aboutpage.AboutPage

/**
 * A simple [Fragment] subclass.
 */
class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val ap = AboutPage(this.context).setDescription("About Page")


            .create()


        return ap
    }


}
