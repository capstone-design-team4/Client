package com.capstone.jeonshimclient

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class MainActivity : FragmentActivity() {
    private var nowFragmentNumber = 0
    var drRequestInfo : DrRequestInfo? = null   // DrRequestInfo 객체
    val api:APIS = APIS.create()
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun drRequestInfoDay(day: Int){
        api.getDrRequestInfoDay(day).enqueue(object : Callback<DrRequestInfo> {
            override fun onResponse(call: Call<DrRequestInfo>, response: Response<DrRequestInfo>) {
                val body = response.body()
                if (body != null && body.toString() != "[]") {
                    drRequestInfo = DrRequestInfo(body.requestId, body.requestStartTime, body.requestEndTime, body.amount, body.user1Flag, body.user2Flag, body.user3Flag, body.decisionFlag)
                }
            }

            override fun onFailure(call: Call<DrRequestInfo>, t: Throwable) {
                Log.d("log", t.message.toString())
                Log.d("log", "fail")
            }
        })
    }
}