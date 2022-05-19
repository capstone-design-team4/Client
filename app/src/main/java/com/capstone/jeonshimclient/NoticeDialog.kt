package com.capstone.jeonshimclient

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

open class NoticeDialog(context: Context) {
    private val dialog = Dialog(context)

    fun setDig(context: Context){
        var drTime = "13:00 ~ 14:00"
        var drKwh = 123.3F

        dialog.show()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(androidx.core.R.layout.custom_dialog)

        //Dialog 크기 설정
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(false)
        // dialog.setCancelable(true)

        dialog.setContentView(R.layout.dialog_notice)

        val btnDone = dialog.findViewById<Button>(R.id.bt_check_notice)
        val txtTime = dialog.findViewById<TextView>(R.id.text_drtime)
        val txtKwh = dialog.findViewById<TextView>(R.id.text_drkwh)

        txtTime.text = drTime
        txtKwh.text = "${drKwh} kWh"

        btnDone.setOnClickListener{
            onClickedListener.onClicked(txtTime.text.toString(), txtKwh.text.toString())
            dialog.dismiss()
        }
    }
    interface ButtonClickListener{
        fun onClicked(drtime: String, drkwh: String)
    }
    private lateinit var onClickedListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener){
        onClickedListener = listener
    }
}


