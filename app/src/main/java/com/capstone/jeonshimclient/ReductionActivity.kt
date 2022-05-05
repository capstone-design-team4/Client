package com.capstone.jeonshimclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import kotlinx.android.synthetic.main.activity_reduction.*

class ReductionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reduction)

        // 다이얼로그
        val dialog = DialogSetting(this)

        button_DR설정.setOnClickListener {
            dialog.setDig()
        }

        dialog.setOnClickedListener(object : DialogSetting.ButtonClickListener{
            override fun onClicked(drname: String){
                print_dr_name.text = Editable.Factory.getInstance().newEditable(drname)
            }
        })
    }
}