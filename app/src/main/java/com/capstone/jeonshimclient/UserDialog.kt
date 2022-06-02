package com.capstone.jeonshimclient

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.dialog_user.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

open class UserDialog(context: Context, intent: Intent) {
    private lateinit var api: APIS
    val STARTHOUR = 0
    val ENDHOUR = 24
    private var completeAPI: Boolean = false
    // context는 그래프 띄울 때 넣을 용도
    private val dialog = Dialog(context)

    val item1 = intent.getIntExtra("item1", 0)
    val item2 = intent.getStringExtra("item2")
    val item3 = intent.getFloatExtra("item3", 0F)
    val item4 = intent.getFloatExtra("item4", 0F)

    fun userDig(context: Context) {

        dialog.show()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(androidx.core.R.layout.custom_dialog)

        //Dialog 크기 설정
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

//        val bt = dialog.findViewById<Button>(R.id.bt_out)
//        bt.setOnClickListener{
//            dialog.dismiss()
//        }

        dialog.setContentView(R.layout.dialog_user)

        dialog.bt_out.setOnClickListener {
            dialog.dismiss()
        }
        dialog.text_edit_dialog_user1.text = "$item1 원"
        dialog.text_edit_dialog_user2.text = item2
        dialog.text_edit_dialog_user3.text = "$item3 kWh"
        dialog.text_edit_dialog_user4.text = "$item4 kWh"
    }

    fun userDialogGraph(context: Context){
        val user_entries = ArrayList<BarEntry>()
        user_entries.add(BarEntry(1.2f,20.0f))
        user_entries.add(BarEntry(2.2f,40.0f))
        user_entries.add(BarEntry(3.2f,30.0f))
        user_entries.add(BarEntry(4.2f,40.0f))
        user_entries.add(BarEntry(5.2f,70.0f))
        user_entries.add(BarEntry(6.2f,20.0f))
        user_entries.add(BarEntry(7.2f,40.0f))

        val userUsageChart = dialog.findViewById<BarChart>(R.id.usagedaychart)

        userUsageChart.run {
            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
            setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 지정
            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawBarShadow(false) //그래프의 그림자
            setDrawGridBackground(false)//격자구조 넣을건지
            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                axisMaximum = 101f //100 위치에 선을 그리기 위해 101f로 맥시멈값 설정
                axisMinimum = 0f // 최소값 0
                granularity = 25f // 50 단위마다 선을 그리려고 설정.
                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                setDrawGridLines(true) //격자 라인 활용
                setDrawAxisLine(false) // 축 그리기 설정
                axisLineColor = ContextCompat.getColor(context,R.color.gray_1) // 축 색깔 설정
                gridColor = ContextCompat.getColor(context,R.color.gray_1) // 축 아닌 격자 색깔 설정
                textColor = ContextCompat.getColor(context,R.color.gray_1) // 라벨 텍스트 컬러 설정
                textSize = 13f //라벨 텍스트 크기
            }
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다.
                granularity = 1f // 1 단위만큼 간격 두기
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
                textColor = ContextCompat.getColor(context,R.color.gray_1) //라벨 색상
                textSize = 12f // 텍스트 크기
                valueFormatter = XAxisFormatter() // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
            }
            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
            legend.isEnabled = false
        }


        val user_set = BarDataSet(user_entries,"사용량") // 데이터셋 초기화

        user_set.color = ContextCompat.getColor(context,R.color.blue_back)

        val dataSet :ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(user_set)

        val user_data = BarData(dataSet)
        user_data.barWidth = 0.3f //막대 너비 설정
        userUsageChart.run {
            this.data = user_data //차트의 데이터를 data로 설정해줌.
            setFitBars(true)
            invalidate()
        }
    }

    inner class XAxisFormatter : ValueFormatter() {
        private val days = arrayOf("13시","14시","15시","16시","17시","18시","19시")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()-1) ?: value.toString()
        }
    }
}