package com.capstone.jeonshimclient

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import kotlinx.android.synthetic.main.dialog_switch.*

class SwtichDialog(context: Context) {
    private val dialog = Dialog(context)


    fun switchDig(context: Context){

        dialog.show()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(androidx.core.R.layout.custom_dialog)

        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(false)
        // dialog.setCancelable(true)

        dialog.setContentView(R.layout.dialog_switch)

        // 서버에서 값 넣어주기
//        dialog.switch1.isChecked = true;
//        dialog.switch2.isChecked = false;
//        dialog.switch3.isChecked = true;

        dialog.switch1.setOnCheckedChangeListener{buttonView, isChecked ->
            // 서버로 값 넣어주기
        }

        dialog.switch2.setOnCheckedChangeListener{buttonView, isChecked ->
            // 서버로 값 넣어주기
        }

        dialog.switch3.setOnCheckedChangeListener{buttonView, isChecked ->
            // 서버로 값 넣어주기
        }

        dialog.bt_OK.setOnClickListener {
            dialog.dismiss()
        }

        dialog.bt_cancle.setOnClickListener {
            // 넣었던 값 수정해주기?
            dialog.dismiss()
        }
    }

}