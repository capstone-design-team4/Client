package com.capstone.jeonshimclient

import android.annotation.SuppressLint
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.john.waveview.WaveView
import kotlinx.android.synthetic.main.fragment_battery.*
import kotlinx.coroutines.*
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class BatteryFragment : Fragment() {
    private val api = APIS.create()
    var batteryCharge = 0
    var batteryChargeOld = 0
    lateinit var jobMain: Job
    lateinit var jobIO: Job
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battery, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_charging)
        var textView = view.findViewById<TextView>(R.id.fragment_battery_textView1)
        jobMain = CoroutineScope(Dispatchers.Main).launch {
            delay(200)
            while(true){
                Log.d("log", "MAIN $batteryCharge")
                while (batteryChargeOld > batteryCharge) {
                    Log.d("log", "main1 $batteryChargeOld")
                    batteryChargeOld--
                    progressBar.progress = batteryChargeOld * 5
                    delay(10)
                }
                while (batteryChargeOld < batteryCharge) {
                    Log.d("log", "main2 $batteryChargeOld")
                    batteryChargeOld++
                    progressBar.progress = batteryChargeOld * 5
                    delay(10)
                }
                textView.text = "$batteryCharge%"
                delay(5000)
            }
        }
        jobIO = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                Log.d("log", "IO $batteryCharge")
                getBatteryCharge()
                delay(5000)
            }
        }
//        thread(start = true) {
//            var i = 1
//            while (i <= 500) {
//                i += 10
//                progressBar.progress = i
//                Thread.sleep(10)
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        jobMain.cancel()
        jobIO.cancel()
    }

    fun getBatteryCharge() {
        api.getBatteryCharge().enqueue(object : Callback<BatteryCharge> {
            override fun onFailure(call: retrofit2.Call<BatteryCharge>, t: Throwable) {
                Log.d("log", t.message.toString())
                Log.d("log", "fail")
            }

            override fun onResponse(
                call: retrofit2.Call<BatteryCharge>,
                response: Response<BatteryCharge>
            ) {
                val body = response.body()
                if (body != null && body.toString() != "[]") {
                    batteryCharge = body.charge
                }
            }
        })
    }
}