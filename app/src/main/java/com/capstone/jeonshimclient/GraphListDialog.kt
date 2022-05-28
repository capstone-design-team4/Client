package com.capstone.jeonshimclient

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import kotlin.math.log
import kotlin.text.Typography.times

open class GraphListDialog(context: Context) {
    private val dialog = Dialog(context)
    val api = APIS.create()
    val STARTHOUR = 0
    val ENDHOUR = 24
    lateinit var usageTimeHash: HashMap<Int, Float>
    private var totalAmount: Float = 0f

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun graphListDig(
        context: Context,
        household: Int,
        selectedDay: Int,
        drRequestInfo: DrRequestInfo?
    ) {
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
        dialog.setContentView(R.layout.dialog_graph_list)

        val textView: TextView = dialog.findViewById(R.id.pf_graph_name2)
        val textView2: TextView = dialog.findViewById(R.id.pf_graph_name3)

        usageTimeHash = HashMap()
        totalAmount = 0f
        CoroutineScope(Dispatchers.IO).launch {
            usageGraphAPI(household, selectedDay, drRequestInfo)

            withContext(Dispatchers.Main) {
                textView.text = "세대${household}이 감축한 전력량"
                textView2.text = totalAmount.toString() + "kwh"
                listGraph(context)
            }
        }

    }

    private fun listGraph(context: Context) {
        Log.d("log", "${usageTimeHash.size}")
        val user_entries = ArrayList<BarEntry>()

        var x = 1f
        var y: Float
        for (key in STARTHOUR..ENDHOUR) {
            if (usageTimeHash.containsKey(key)) {
                y = usageTimeHash[key]!!
                user_entries.add(BarEntry(x++, y))
            }
        }

        val userChart = dialog.findViewById<BarChart>(R.id.userdrresultchart)

        userChart.run {
            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
            setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 지정
            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawBarShadow(false) // 그래프의 그림자
            setDrawGridBackground(false)// 격자구조 넣을건지
            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                axisMaximum = 1001f //100 위치에 선을 그리기 위해 101f로 맥시멈값 설정
                axisMinimum = 0f // 최소값 0
                granularity = 100f // 50 단위마다 선을 그리려고 설정.
                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                setDrawGridLines(true) //격자 라인 활용
                setDrawAxisLine(true) // 축 그리기 설정
                axisLineColor = ContextCompat.getColor(context, R.color.gray_1) // 축 색깔 설정
                gridColor = ContextCompat.getColor(context, R.color.gray_1) // 축 아닌 격자 색깔 설정
                textColor = ContextCompat.getColor(context, R.color.gray_1) // 라벨 텍스트 컬러 설정
                textSize = 13f //라벨 텍스트 크기
            }
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다.
                granularity = 1f // 1 단위만큼 간격 두기
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
                textColor = ContextCompat.getColor(context, R.color.gray_1) // 라벨 색상
                textSize = 12f // 텍스트 크기
                valueFormatter = XAxisFormatter() // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
            }
            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
            legend.isEnabled = false
        }


        val user_set = BarDataSet(user_entries, "사용량") // 데이터셋 초기화

        user_set.color = ContextCompat.getColor(context, R.color.blue_back)

        val dataSet: ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(user_set)

        val user_data = BarData(dataSet)
        user_data.barWidth = 0.3f //막대 너비 설정
        userChart.run {
            this.data = user_data //차트의 데이터를 data로 설정해줌.
            setFitBars(true)
            invalidate()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun usageGraphAPI(household: Int, selectedDay: Int, drRequestInfo: DrRequestInfo?) {
        val call = api.getUsageAtDrTime(drRequestInfo?.requestId.toString(), household.toString())
        try {
            val execute = call.execute()
            val body = execute.body()
            Log.d("log", "getMeasurementUsageDay$household 1 :$execute")
            Log.d("log", "getMeasurementUsageDay$household 2 :" + body.toString())
            Log.d(
                "log", "getMeasurementUsageDay$household 3 :" + body?.count().toString()
            )
            if (body.toString() != "[]" && body != null) {
                val count = body.count()
                for (index in 0 until count) {
                    val targetTime =
                        LocalDateTime.parse(body[index].timeCurrent).toLocalTime().hour

                    if (targetTime < STARTHOUR || targetTime > ENDHOUR) continue

                    val amount = body[index].current * body[index].voltage * 15
                    totalAmount += amount
                    if (!usageTimeHash.containsKey(targetTime))
                        usageTimeHash[targetTime] = amount
                    else
                        usageTimeHash[targetTime] = amount + usageTimeHash.getValue(targetTime)
                }
            }
        } catch (e: Exception) {
            Log.d("log", "GraphListDialog Exception!")
        }
    }

    inner class XAxisFormatter : ValueFormatter() {
        private val times = ArrayList<String>()
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            for (i in STARTHOUR..ENDHOUR) {
                if (usageTimeHash.containsKey(i))
                    times.add("$i:00")
            }
            return times.getOrNull(value.toInt() - 1) ?: value.toString()
        }
    }
}