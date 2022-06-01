package com.capstone.jeonshimclient

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.WindowManager
import android.widget.CalendarView
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.temporal.ChronoField

open class CalendarDialog(context: Context) {
    private val dialog = Dialog(context)
    private var dialogYear: Int = 0
    private var dialogMonth: Int = 0
    private var dialogDay: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    fun calenderDialog(context: Context, selectedYear: Int, selectedMonth: Int, selectedDay: Int){
        dialogYear = selectedYear
        dialogMonth = selectedMonth
        dialogDay = selectedDay

        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(androidx.core.R.layout.custom_dialog)

        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_calendar)

        val calenderDialog: CalendarView = dialog.findViewById(R.id.calendarView)
        calenderDialog.date = LocalDate.now().toString().toLong()

        calenderDialog.minDate = SimpleDateFormat("yyyyMMdd").parse("20200101").time

        // 캘린더 뷰의 날짜를 클릭 했을 때 발생될 이벤트
        calenderDialog.setOnDateChangeListener{view, year, month, dayOfMonth ->
            dialogYear = year
            dialogMonth = month
            dialogDay = dayOfMonth
        }
    }
    fun getYear(): Int{
        return dialogYear
    }
    fun getMonth(): Int{
        return dialogMonth
    }
    fun getDay(): Int{
        return dialogDay
    }
}