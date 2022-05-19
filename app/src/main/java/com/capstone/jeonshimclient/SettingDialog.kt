package com.capstone.jeonshimclient

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText

open class SettingDialog(context: Context) {
    private val dialog = Dialog(context)

    fun setDig(context: Context){
        dialog.show()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(androidx.core.R.layout.custom_dialog)

        //Dialog 크기 설정
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        dialog.setContentView(R.layout.dialog_setting)

        val edit_dr = dialog.findViewById<EditText>(R.id.input_drname)
        val btnDone = dialog.findViewById<Button>(R.id.check)
        val btnCancel = dialog.findViewById<Button>(R.id.cancle)

        btnDone.setOnClickListener{
            onClickedListener.onClicked(edit_dr.text.toString())
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }
    interface ButtonClickListener{
        fun onClicked(drname: String)
    }
    private lateinit var onClickedListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener){
        onClickedListener = listener
    }
}



