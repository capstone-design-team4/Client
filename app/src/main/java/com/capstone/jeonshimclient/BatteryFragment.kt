package com.capstone.jeonshimclient
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.john.waveview.WaveView
import kotlinx.android.synthetic.main.fragment_battery.*
import kotlin.concurrent.thread

class BatteryFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_charging)

        thread(start = true){
            var i = 1
            while(i<=500){
                i+=10
                progressBar.progress = i
                Thread.sleep(10)
            }
        }
    }
}