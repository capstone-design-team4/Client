package com.capstone.jeonshimclient

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime

class MainActivity : FragmentActivity() {
    private var nowFragmentNumber = 0
    var drRequestInfo: DrRequestInfo? = null   // DrRequestInfo 객체
    private val api: APIS = APIS.create()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val reductionStatusFragment = ReductionStatusFragment()
        val graphFragment = GraphFragment()
        val userFragment = UserFragment()
        val batteryFragment = BatteryFragment()

        // 하단 탭이 눌렸을 때 화면을 전환하기 위해선 이벤트 처리하기 위해 BottomNavigationView 객체 생성
        val bnv_main = findViewById<BottomNavigationView>(R.id.bnv_main)

        // DR 요청을 받아옴
        drRequestInfoDay(LocalDate.now().dayOfMonth)

        // OnNavigationItemSelectedListener를 통해 탭 아이템 선택 시 이벤트를 처리
        // navi_menu.xml 에서 설정했던 각 아이템들의 id를 통해 알맞은 프래그먼트로 변경하게 한다.
        bnv_main.run {
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.first -> {
                        // 다른 프래그먼트 화면으로 이동하는 기능
                        if (nowFragmentNumber != 1) {
                            nowFragmentNumber = 1
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.fl_container, reductionStatusFragment).commit()
                        }
                    }
                    R.id.second -> {
                        if (nowFragmentNumber != 2) {
                            nowFragmentNumber = 2
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.fl_container, graphFragment).commit()
                        }
                    }
                    R.id.third -> {
                        if (nowFragmentNumber != 3) {
                            nowFragmentNumber = 3
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.fl_container, userFragment).commit()
                        }
                    }
                    R.id.fourth -> {
                        if (nowFragmentNumber != 4) {
                            nowFragmentNumber = 4
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.fl_container, batteryFragment).commit()
                        }
                    }
                }
                true
            }
            selectedItemId = R.id.first
        }

        val con = this
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                if (drRequestInfo != null) {
                    val starttime = LocalDateTime.parse(drRequestInfo?.requestStartTime)
                    val endtime = LocalDateTime.parse(drRequestInfo?.requestEndTime)

                    if (LocalDateTime.now().hour == starttime.hour - 1) {
                        val startTime_string =
                            starttime.hour.toString() + ":" + starttime.minute.toString()
                        val endTime_string =
                            endtime.hour.toString() + ":" + endtime.minute.toString()

                        Log.d("abc3 : ", startTime_string)
                        Log.d("abc4 : ", endTime_string)

                        val intent = Intent()
                        intent.putExtra("startTime", startTime_string)
                        intent.putExtra("endTime", endTime_string)
                        intent.putExtra("kwh", drRequestInfo?.amount)

                        withContext(Dispatchers.Main) {
                            while (nowFragmentNumber != 0)
                                delay(300)
                            val noticeDialog = NoticeDialog(con, intent)
                            noticeDialog.setDig(con, intent)

                            val abc = findViewById<TextView>(R.id.drTime)
                            val abc2 = findViewById<TextView>(R.id.drkWh)
                            noticeDialog.setOnClickedListener(object :
                                NoticeDialog.ButtonClickListener {
                                override fun onClicked(drtime: String, drkwh: String) {
                                    abc.text = drtime
                                    abc2.text = drkwh
                                }
                            })
                        }

                        this.cancel()
                    }
                }
                delay(5000)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun drRequestInfoDay(day: Int) {
        val date = LocalDateTime.of(LocalDateTime.now().year, LocalDateTime.now().month, day, 0, 0)
        Log.d("log", "date: $date")

        api.getDrRequestInfoDay(date.toString()).enqueue(object : Callback<DrRequestInfo> {
            override fun onResponse(call: Call<DrRequestInfo>, response: Response<DrRequestInfo>) {
                val body = response.body()
                Log.d("abc_ :", "${body}")
                if (body != null && body.toString() != "[]") {
                    drRequestInfo = DrRequestInfo(
                        body.requestId,
                        body.requestStartTime,
                        body.requestEndTime,
                        body.amount,
                        body.user1Flag,
                        body.user2Flag,
                        body.user3Flag,
                        body.decisionFlag
                    )
                }
            }

            override fun onFailure(call: Call<DrRequestInfo>, t: Throwable) {
                Log.d("log", t.message.toString())
                Log.d("log", "fail")
            }
        })
    }
}