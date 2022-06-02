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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.LocalTime

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

    private lateinit var switchDialog: SwitchDialog

    lateinit var mainActivity: MainActivity
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSpinner(view)
    }

    // 이번 달 전기 요금 -> get Fee 로 받아올 것
    var userdialog_text1 = 0

    // 현재 사용 전력원    -> relayIsUsing
    var userdialog_text2 = "발전 전력"

    // 이번 달 사용 전력량  -> getUsageMonth
    var userdialog_text3 = 0F

    // 하루 사용 전력량    -> getUsageDay
    var userdialog_text4 = 0F

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
                when (selectedSpinner) {
                    1 -> CoroutineScope(Dispatchers.IO).launch {
                        getFee(1)
                        getRelayIsUsing(1)
                        getUsageDay(1)
                        getUsageMonth(1)
                        delay(1000)
                        userdialog_text1 = fee1
                        userdialog_text2 = if (relayIsUsing1)
                            "발전 전력"
                        else
                            "일반 전력"
                        userdialog_text3 = usageMonthAmount1
                        userdialog_text4 = usageDayAmount1
                    }
                    2 -> CoroutineScope(Dispatchers.IO).launch {
                        getFee(2)
                        getRelayIsUsing(2)
                        getUsageDay(2)
                        getUsageMonth(2)
                        delay(1000)
                        userdialog_text1 = fee2
                        userdialog_text2 = if (relayIsUsing2)
                            "발전 전력"
                        else
                            "일반 전력"
                        userdialog_text3 = usageMonthAmount2
                        userdialog_text4 = usageDayAmount2
                    }
                    3 -> CoroutineScope(Dispatchers.IO).launch {
                        getFee(3)
                        getRelayIsUsing(3)
                        getUsageDay(3)
                        getUsageMonth(3)
                        delay(1000)
                        userdialog_text1 = fee3
                        userdialog_text2 = if (relayIsUsing3)
                            "발전 전력"
                        else
                            "일반 전력"
                        userdialog_text3 = usageMonthAmount3
                        userdialog_text4 = usageDayAmount3
                    }
                }
                delay(1000)
                withContext(Dispatchers.Main){
                    intent.putExtra("item1", userdialog_text1)
                    intent.putExtra("item2", userdialog_text2)
                    intent.putExtra("item3", userdialog_text3)
                    intent.putExtra("item4", userdialog_text4)

                    // UserDialog 띄우기
                    val userDialog = UserDialog(requireContext(), intent)
                    userDialog.userDig(requireContext())
                }
            }
        }
        bt_fragment_user2.setOnClickListener {

            // 코루틴 안에서 실행
            CoroutineScope(Dispatchers.IO).launch {
                for (i in 1..3)
                    getRelayIsUsing(i)
                delay(100)      // 리퀘스트 받아오는데에 시간을 고려해서 딜레이 약간 넣어줌
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

    fun getRelayIsUsing(userId: Int) {
        api.getRelayIsUsing(userId).enqueue(object : Callback<RelayIsUsing> {
            override fun onResponse(call: Call<RelayIsUsing>, response: Response<RelayIsUsing>) {
                val body = response.body()
                if (body != null && body.toString() != "[]") {
                    when (userId) {
                        1 -> relayIsUsing1 = body.relayIsUsing
                        2 -> relayIsUsing2 = body.relayIsUsing
                        3 -> relayIsUsing3 = body.relayIsUsing
                    }
                }
            }

            override fun onFailure(call: Call<RelayIsUsing>, t: Throwable) {
                Log.d("log", t.message.toString())
                Log.d("log", "fail")
            }
        })
    }

    fun getFee(userId: Int) {
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
            }

            override fun onFailure(call: Call<Fee>, t: Throwable) {
                Log.d("log", t.message.toString())
                Log.d("log", "fail")
            }
        })
    }

    fun getUsageDay(userId: Int) {
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
                    for (i in 0 until count) {
                        val temp = body[i].current * body[i].voltage * 15
                        val targetTime = LocalDateTime.parse(body[i].timeCurrent).hour
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
            }

            override fun onFailure(call: Call<List<Measurement>>, t: Throwable) {
                Log.d("log", t.message.toString())
                Log.d("log", "fail")
            }
        })
    }

    fun getUsageMonth(userId: Int) {
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
            }

            override fun onFailure(call: Call<List<Measurement>>, t: Throwable) {
                Log.d("log", t.message.toString())
                Log.d("log", "fail")
            }
        })
    }

}