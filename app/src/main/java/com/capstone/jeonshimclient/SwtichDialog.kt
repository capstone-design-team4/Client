package com.capstone.jeonshimclient

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.WindowManager
import android.widget.Switch
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_switch.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SwitchDialog(
    context: Context,
    relayIsUsing1: Boolean,
    relayIsUsing2: Boolean,
    relayIsUsing3: Boolean,
    apis: APIS
) {
    private var relay1: Boolean = relayIsUsing1
    private var relay2: Boolean = relayIsUsing2
    private var relay3: Boolean = relayIsUsing3
    private var api = apis
    private val dialog = Dialog(context)

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    fun switchDig(context: Context) {
        dialog.show()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(androidx.core.R.layout.custom_dialog)

        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCanceledOnTouchOutside(false)
        // dialog.setCancelable(true)

        dialog.setContentView(R.layout.dialog_switch)

        val switch1: Switch = dialog.switch1
        val switch2: Switch = dialog.switch2
        val switch3: Switch = dialog.switch3

        switch1.isChecked = relay1
        switch2.isChecked = relay2
        switch3.isChecked = relay3

        dialog.switch1.setOnCheckedChangeListener { _, isChecked ->
            relay1 = isChecked == true
        }

        dialog.switch2.setOnCheckedChangeListener { _, isChecked ->
            relay2 = isChecked == true
        }

        dialog.switch3.setOnCheckedChangeListener { _, isChecked ->
            relay3 = isChecked == true
        }

        dialog.bt_OK.setOnClickListener {
            for (i in 1..3) {
                Log.d("log", "$i 번째 !!!")
                var relayData: RelayController? = null
                when (i) {
                    1 -> relayData = RelayController(1, relay1)
                    2 -> relayData = RelayController(3, relay2)
                    3 -> relayData = RelayController(5, relay3)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    var charge = 30
                    var flag = false
                    api.getBatteryCharge().enqueue(object : Callback<BatteryCharge> {
                        override fun onResponse(
                            call: Call<BatteryCharge>,
                            response: Response<BatteryCharge>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("log", "성공")
                                Log.d("log", response.body()!!.toString())
                                charge = response.body()!!.charge
                            }
                            flag = true
                        }

                        override fun onFailure(call: Call<BatteryCharge>, t: Throwable) {
                            // 실패
                            Log.d("log", t.message.toString())
                            Log.d("log", "fail")
                            flag = true
                        }
                    })
                    while (!flag) {
                        delay(100)
                    }
                    if (charge <= 30) {
                        // 20이하라서 토스트 띄워야 함
                        withContext(Dispatchers.Main){
                            Toast.makeText(context, "배터리 저장량이 30% 이하힙니다", Toast.LENGTH_SHORT).show()
                        }
                    } else
                        api.postRelayController(relayData!!).enqueue(object : Callback<PostResult> {
                            override fun onResponse(
                                call: Call<PostResult>,
                                response: Response<PostResult>
                            ) {
                                Log.d("log", response.toString())
                                Log.d("log", response.body().toString())
                            }

                            override fun onFailure(call: Call<PostResult>, t: Throwable) {
                                // 실패
                                Log.d("log", t.message.toString())
                                Log.d("log", "fail")
                            }
                        })
                }
            }
            dialog.dismiss()
        }

        dialog.bt_cancle.setOnClickListener {
            dialog.dismiss()
        }
    }
}