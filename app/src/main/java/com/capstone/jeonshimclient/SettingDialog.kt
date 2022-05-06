package com.capstone.jeonshimclient

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*

open class SettingDialog(context: Context) {
    private val dialog = Dialog(context)

    fun setDig(context: Context){
        dialog.show()

        dialog.setContentView(androidx.core.R.layout.custom_dialog)

        //Dialog 크기 설정
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        dialog.setContentView(R.layout.dialog_setting)

        val edit_dr = dialog.findViewById<EditText>(R.id.input_drname)

        val edit_time1 = dialog.findViewById<Button>(R.id.input_times1)
        val edit_time2 = dialog.findViewById<Button>(R.id.input_times2)

        val edit_reductions = dialog.findViewById<EditText>(R.id.input_reductions)

        val btnDone = dialog.findViewById<Button>(R.id.check)
        val btnCancel = dialog.findViewById<Button>(R.id.cancle)

        edit_time1.setOnClickListener {
            getTime(edit_time1, context)
        }

        edit_time2.setOnClickListener {
            getTime(edit_time2, context)
        }

        btnDone.setOnClickListener{
            onClickedListener.onClicked(edit_dr.text.toString(), edit_reductions.text.toString(),
                edit_time1.text.toString(), edit_time2.text.toString())
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }
    interface ButtonClickListener{
        fun onClicked(drname: String, reductions: String, time1: String, time2: String)
    }
    private lateinit var onClickedListener: ButtonClickListener

    private fun getTime(button: Button, context: Context){

        val cal = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            button.text = SimpleDateFormat("HH:mm").format(cal.time)
        }

        val tt = TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
        tt.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        tt.show()

    }
    fun setOnClickedListener(listener: ButtonClickListener){
        onClickedListener = listener
    }
}



