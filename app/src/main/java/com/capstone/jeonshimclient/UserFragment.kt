package com.capstone.jeonshimclient

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

class UserFragment : Fragment() {
    private val api = APIS.create()
    private var relayIsUsing1 = true
    private var relayIsUsing2 = true
    private var relayIsUsing3 = true
    private var fee1 = 0
    private var fee2 = 0
    private var fee3 = 0
    private var usageDayAmount1 = 0f
    private var usageDayAmount2 = 0f
    private var usageDayAmount3 = 0f
    private var usageMonthAmount1 = 0f
    private var usageMonthAmount2 = 0f
    private var usageMonthAmount3 = 0f
    private var usageHash1 = HashMap<Int, Float>()
    private var usageHash2 = HashMap<Int, Float>()
    private var usageHash3 = HashMap<Int, Float>()
    private var selectedSpinner = 1
    private var counterIO = 4
    private var counterMAIN = 1
    private lateinit var switchDialog: SwitchDialog
    private var userdialog_text1 = 0 // 이번 달 전기 요금
    private var userdialog_text2 = "발전 전력" // 현재 사용 전력원
    private var userdialog_text3 = 0F // 이번 달 사용 전력량
    private var userdialog_text4 = 0F // 하루 사용 전력량

    private lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSpinner(view)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun loadSpinner(view: View) {
        val spinner: Spinner = view.findViewById(R.id.spinner_fragment_user)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            view.context,
            R.array.spinner_array,
            R.layout.spinner_list
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_list)
            spinner.adapter = adapter
        }

        spinner.setSelection(0)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> selectedSpinner = 1
                    1 -> selectedSpinner = 2
                    2 -> selectedSpinner = 3
                }
                spinner.onItemSelectedListener = this
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        bt_fragment_user.setOnClickListener {
            val intent = Intent(requireContext(), UserDialog::class.java)
            CoroutineScope(Dispatchers.IO).launch {
                counterMAIN = 1
                when (selectedSpinner) {
                    1 -> CoroutineScope(Dispatchers.IO).launch {
                        getAll(1)
                        userdialog_text1 = ((usageMonthAmount1 / 1000) * 93).toInt() + 910
                        userdialog_text2 = if (relayIsUsing1)
                            "발전 전력"
                        else
                            "일반 전력"
                        userdialog_text3 = usageMonthAmount1
                        userdialog_text4 = usageDayAmount1

                        counterMAIN -= 1
                    }
                    2 -> CoroutineScope(Dispatchers.IO).launch {
                        getAll(2)
                        userdialog_text1 = ((usageMonthAmount2 / 1000) * 93).toInt() + 910
                        userdialog_text2 = if (relayIsUsing2)
                            "발전 전력"
                        else
                            "일반 전력"
                        userdialog_text3 = usageMonthAmount2
                        userdialog_text4 = usageDayAmount2

                        counterMAIN -= 1
                    }
                    3 -> CoroutineScope(Dispatchers.IO).launch {
                        getAll(3)
                        userdialog_text1 = ((usageMonthAmount3 / 1000) * 93).toInt()
                        userdialog_text2 = if (relayIsUsing3)
                            "발전 전력"
                        else
                            "일반 전력"
                        userdialog_text3 = usageMonthAmount3
                        userdialog_text4 = usageDayAmount3

                        counterMAIN -= 1
                    }
                }
                withContext(Dispatchers.Main) {
                    while (counterMAIN > 0) {
                        Log.d("log", "delay")
                        delay(10)
                    }

                    intent.putExtra("item1", userdialog_text1)
                    intent.putExtra("item2", userdialog_text2)
                    intent.putExtra("item3", userdialog_text3)
                    intent.putExtra("item4", userdialog_text4)

                    // UserDialog 띄우기
                    val userDialog = UserDialog(requireContext(), intent)
                    when (selectedSpinner) {
                        1 -> userDialog.userDig(requireContext(), usageHash1)
                        2 -> userDialog.userDig(requireContext(), usageHash2)
                        3 -> userDialog.userDig(requireContext(), usageHash3)
                    }

                }
            }
        }
        bt_fragment_user2.setOnClickListener {
            // 코루틴 안에서 실행
            CoroutineScope(Dispatchers.IO).launch {
                for (i in 1..3)
                    getRelayIsUsing(i)
                delay(300)      // 리퀘스트 받아오는데에 시간을 고려해서 딜레이 약간 넣어줌
                withContext(Dispatchers.Main) {
                    switchDialog =
                        SwitchDialog(
                            requireContext(),
                            relayIsUsing1,
                            relayIsUsing2,
                            relayIsUsing3,
                            api
                        )
                    switchDialog.switchDig(requireContext())
                }
            }
        }
    }

    // 사용자 정보를 받기 위한 모든 get
    // counter를 4로 맞춰놓고 비동기 방식으로 IO를 진행
    // 통신이 끝나고 dialog 를 띄워야 하므로 counter 가 0이 될 때까지 딜레이를 주어야 함
    private suspend fun getAll(userId: Int) {
        counterIO = 4
        getFee(userId)
        getRelayIsUsing(userId)
        getUsageDay(userId)
        getUsageMonth(userId)
        while (counterIO > 0) {
            Log.d("log", "delay counter = $counterIO")
            delay(10)
        }
    }

    private fun getRelayIsUsing(userId: Int) {
        api.getRelayIsUsing(userId).enqueue(object : Callback<RelayIsUsing> {
            override fun onResponse(call: Call<RelayIsUsing>, response: Response<RelayIsUsing>) {
                val body = response.body()
                if (body != null && body.toString() != "[]") {
                    when (userId) {
                        1 -> {
                            Log.d("log", body.relayIsUsing.toString())
                            relayIsUsing1 = body.relayIsUsing
                        }
                        2 -> {
                            Log.d("log", body.relayIsUsing.toString())
                            relayIsUsing2 = body.relayIsUsing
                        }
                        3 -> {
                            Log.d("log", body.relayIsUsing.toString())
                            relayIsUsing3 = body.relayIsUsing
                        }
                    }
                }
                counterIO -= 1    // 동기화를 위해 존재함 counter == 0 일때 모든 get이 끝남을 의미
            }

            override fun onFailure(call: Call<RelayIsUsing>, t: Throwable) {
                Log.d("log", t.message.toString())
                Log.d("log", "fail")
                counterIO -= 1    // 동기화를 위해 존재함 counter == 0 일때 모든 get이 끝남을 의미
            }
        })
    }

    private fun getFee(userId: Int) {
        api.getFee(userId).enqueue(object : Callback<Fee> {
            override fun onResponse(call: Call<Fee>, response: Response<Fee>) {
                val body = response.body()
                if (body != null && body.toString() != "[]") {
                    when (userId) {
                        1 -> fee1 = body.fee
                        2 -> fee2 = body.fee
                        3 -> fee3 = body.fee
                    }
                }
                counterIO -= 1    // 동기화를 위해 존재함 counter == 0 일때 모든 get이 끝남을 의미
            }

            override fun onFailure(call: Call<Fee>, t: Throwable) {
                Log.d("log", t.message.toString())
                Log.d("log", "fail")
                counterIO -= 1    // 동기화를 위해 존재함 counter == 0 일때 모든 get이 끝남을 의미
            }
        })
    }

    private fun getUsageDay(userId: Int) {
        api.getUsageDay(userId).enqueue(object : Callback<List<Measurement>> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<List<Measurement>>,
                response: Response<List<Measurement>>
            ) {
                val body = response.body()
                if (body != null && body.toString() != "[]") {
                    val count = body.count()
                    var amount = 0f
                    when (userId) {
                        1 -> usageHash1.clear()
                        2 -> usageHash2.clear()
                        3 -> usageHash3.clear()
                    }
                    for (i in 0 until count) {
                        val temp = body[i].current * body[i].voltage * 15
                        val targetTime = LocalDateTime.parse(body[i].timeCurrent).hour
                        Log.d("log", "targetTime = $targetTime")
                        amount += temp
                        when (userId) {
                            1 -> if (!usageHash1.containsKey(targetTime))
                                usageHash1[targetTime] = temp
                            else
                                usageHash1[targetTime] = usageHash1.getValue(targetTime) + temp
                            2 -> if (!usageHash2.containsKey(targetTime))
                                usageHash2[targetTime] = temp
                            else
                                usageHash2[targetTime] = usageHash2.getValue(targetTime) + temp
                            3 -> if (!usageHash3.containsKey(targetTime))
                                usageHash3[targetTime] = temp
                            else
                                usageHash3[targetTime] = usageHash3.getValue(targetTime) + temp
                        }
                    }
                    when (userId) {
                        1 -> usageDayAmount1 = amount
                        2 -> usageDayAmount2 = amount
                        3 -> usageDayAmount3 = amount
                    }
                }
                counterIO -= 1    // 동기화를 위해 존재함 counter == 0 일때 모든 get이 끝남을 의미
            }

            override fun onFailure(call: Call<List<Measurement>>, t: Throwable) {
                Log.d("log", t.message.toString())
                Log.d("log", "fail")
                counterIO -= 1    // 동기화를 위해 존재함 counter == 0 일때 모든 get이 끝남을 의미
            }
        })
    }

    private fun getUsageMonth(userId: Int) {
        api.getUsageMonth(userId).enqueue(object : Callback<List<Measurement>> {
            override fun onResponse(
                call: Call<List<Measurement>>,
                response: Response<List<Measurement>>
            ) {
                val body = response.body()
                if (body != null && body.toString() != "[]") {
                    val count = body.count()
                    var amount = 0f
                    for (i in 0 until count) {
                        var temp = body[i].current * body[i].voltage * 15
                        amount += temp
                    }
                    when (userId) {
                        1 -> usageMonthAmount1 = amount
                        2 -> usageMonthAmount2 = amount
                        3 -> usageMonthAmount3 = amount
                    }
                }
                counterIO -= 1    // 동기화를 위해 존재함 counter == 0 일때 모든 get이 끝남을 의미
            }

            override fun onFailure(call: Call<List<Measurement>>, t: Throwable) {
                Log.d("log", t.message.toString())
                Log.d("log", "fail")
                counterIO -= 1    // 동기화를 위해 존재함 counter == 0 일때 모든 get이 끝남을 의미
            }
        })
    }

}