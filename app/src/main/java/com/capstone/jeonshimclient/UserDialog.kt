package com.capstone.jeonshimclient

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import kotlinx.android.synthetic.main.dialog_user.*

open class UserDialog(context: Context, _item1: Int, _item2: String, _item3: Float, _item4: Float) {
    // context는 그래프 띄울 때 넣을 용도
    private val dialog = Dialog(context)
    val item1 = _item1
    val item2 = _item2
    val item3 = _item3
    val item4 = _item4

    fun userDig(context: Context){

        dialog.text_edit_dialog_user.text = "${item1} 원"
        dialog.text_edit_dialog_user2.text = item2
        dialog.text_edit_dialog_user3.text = "${item3} kWh"
        dialog.text_edit_dialog_user4.text = "${item4} kWh"

        dialog.show()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(androidx.core.R.layout.custom_dialog)

        //Dialog 크기 설정
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        dialog.setContentView(R.layout.dialog_user)
    }
}