package com.capstone.jeonshimclient

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText

open class DialogSetting(context: Context) {
    private val dialog = Dialog(context)

    fun setDig(){
        dialog.show()

        dialog.setContentView(androidx.core.R.layout.custom_dialog)

        //Dialog 크기 설정
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        dialog.setContentView(R.layout.setting_dialog)

        val edit = dialog.findViewById<EditText>(R.id.input_drname)
        val btnDone = dialog.findViewById<Button>(R.id.check)
        val btnCancel = dialog.findViewById<Button>(R.id.cancle)

        btnDone.setOnClickListener{
            onClickedListener.onClicked(edit.text.toString())
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



