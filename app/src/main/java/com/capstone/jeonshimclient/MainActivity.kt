package com.capstone.jeonshimclient

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : FragmentActivity() {
    private var nowFragmentNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var api: APIS = APIS.create()

        // 하단 탭이 눌렸을 때 화면을 전환하기 위해선 이벤트 처리하기 위해 BottomNavigationView 객체 생성
        var bnv_main = findViewById<BottomNavigationView>(R.id.bnv_main)

        // OnNavigationItemSelectedListener를 통해 탭 아이템 선택 시 이벤트를 처리
        // navi_menu.xml 에서 설정했던 각 아이템들의 id를 통해 알맞은 프래그먼트로 변경하게 한다.
        bnv_main.run {
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.first -> {
                        // 다른 프래그먼트 화면으로 이동하는 기능
                        if (nowFragmentNumber != 1) {
                            nowFragmentNumber = 1
                            val reductionStatusFragment = ReductionStatusFragment()
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.fl_container, reductionStatusFragment).commit()
                        }
                    }
                    R.id.second -> {
                        if (nowFragmentNumber != 2) {
                            nowFragmentNumber = 2
                            val graphFragment = GraphFragment()
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.fl_container, graphFragment).commit()
                        }
                    }
                    R.id.third -> {
                        if (nowFragmentNumber != 3) {
                            nowFragmentNumber = 3
                            val userFragment = UserFragment()
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.fl_container, userFragment).commit()
                        }
                    }
                }
                true
            }
            selectedItemId = R.id.first
        }

    }

    fun changeFragment(index: Int) {
        when (index) {
            1 -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fl_container, DRListFragment())
                    .commit()
            }
            2 -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fl_container, ReductionStatusFragment())
                    .commit()
            }
        }
    }

}