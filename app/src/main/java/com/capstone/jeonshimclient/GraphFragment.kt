package com.capstone.jeonshimclient

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.fragment_graph.*
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GraphFragment : Fragment() {
    @RequiresApi(Build.VERSION_CODES.O)
    private val USERCOUNT: Int = 3
    private lateinit var api: APIS
    private var genTimeHash: HashMap<LocalDate, Int> = HashMap()
    private var usageTimeHash: HashMap<LocalDate, Int> = HashMap()
    private var genMonthHash: HashMap<LocalDate, Float> = HashMap()
    private var usageMonthHash: HashMap<LocalDate, Float> = HashMap()
    private var usagePredictionTimeHash: HashMap<LocalDate, Float> = HashMap()
    private var predictionTimeHash: HashMap<LocalDate, Float> = HashMap()
    private var completeAPI1: Boolean = false
    private var completeAPI2: Boolean = false
    private var nowChart: Int = 1 // 1 or 2 현재 보여주는 chart

    // 그래프 x축 항목
    private val days: ArrayList<LocalDate> = ArrayList()    // 연/월/일
    private val months: ArrayList<LocalDate> = ArrayList()  // 연/월

    // 그래프 x축 항목들 표기법
    private val strArray = ArrayList<String>()
    private val strArrayMonth = ArrayList<String>()

    private var switch1 = 0
    private var switch2 = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("log", "OnCreate")
        // days에 들어가야할 날짜들부터 넣어줌
        var date = LocalDate.now().minusDays(365)
        var date2 = date.month
        for (i in 0..366) {
            days.add(date)
            if(months.isEmpty())
                months.add(LocalDate.of(date.year, date.month, 1))
            else if(months[months.count() - 1].month != date.month)
                months.add(LocalDate.of(date.year, date.month, 1))
            date = date.plusDays(1)

            Log.d("chart", date.toString())
        }
        // x축 항목 만들어주려고 string형 배열
        for (i in 0..366) {
            strArray.add("${days[i].year}/${days[i].monthValue}/${days[i].dayOfMonth}")
        }
        for (value in months) {
            strArrayMonth.add("${value.year}/${value.monthValue}")
        }

        api = APIS.create()
        if (genMonthHash.isEmpty())
            CoroutineScope(Dispatchers.IO).launch {
                Log.d("log", "Coroutine.launch")
                generator_present_expected_Graph_API()
                usage_present_expected_Graph_API()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("log", "OnCreateView")
        return inflater.inflate(R.layout.fragment_graph, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor", "ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("log", "OnViewCreated")
        Log.d("log", "graph")
        CoroutineScope(Dispatchers.Main).launch {
            while (!completeAPI1 || !completeAPI2) {
                delay(100)
            }
            try{
                graphFragmentMain()
            }catch (e: Exception){
                cancel()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun graphFragmentMain() {
        // 어플 시작시 띄우는 그래프 = 발전량 그래프
        generator_present_expected_Graph(requireContext())

        switch1 = 1 // 1 = 발전량 2 = 사용량
        switch2 = 1 // 1 = 일 2 = 월

        bt_start_graph1.setOnClickListener {
            switch1 = 1
            bt_start_graph1.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.background_bs_blue)
            bt_start_graph1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            bt_start_graph2.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.background_bs_gray)
            bt_start_graph2.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_2))

            generator_present_expected_Graph(requireContext())
            text_fragment_graph2.text =
                "일 년 동안 건물에서 발전시킨 전력의 양을 보여줍니다.\n실제로 발전된 전력의 양과 이후에 발전될 전력의 양을 \n동시에 확인할 수 있습니다."

        }
        bt_start_graph2.setOnClickListener {
            switch1 = 2
            bt_start_graph2.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.background_bs_blue)
            bt_start_graph2.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            bt_start_graph1.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.background_bs_gray)
            bt_start_graph1.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_2))

            usage_present_expected_Graph(requireContext())
            text_fragment_graph2.text =
                "일 년 동안 건물 전체에서 사용된 전력의 양을 보여줍니다.\n실제로 사용한 전력의 양과 이후에 사용될 전력의 양을 \n동시에 확인할 수 있습니다.\n( 세대별 사용량은 세대 정보 페이지에서 조회합니다. )"

        }
        bt_start_graph3.setOnClickListener { // 월버튼 누른 상태
            if (switch1 == 1) {
                gen_month_Graph(requireContext())
            } // 발전량이면
            else {
                usage_month_Graph(requireContext())
            }
        }
        bt_start_graph4.setOnClickListener {    // 일버튼 누른 상태
            if (switch1 == 1) {
                generator_present_expected_Graph(requireContext())
            } // 발전량이면
            else {
                usage_present_expected_Graph(requireContext())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generator_present_expected_Graph_API() {
        val call = api.getYearGenData()
        try {
            val execute = call.execute()
            val body = execute.body()
            Log.d("log", "getYearGenData1 :$execute")
            Log.d("log", "getYearGenData2 : ${body.toString()}")
            Log.d(
                "log", "getYearGenData3 : ${body?.count().toString()}"
            )

            if (body.toString() != "[]" && body != null) {
                val count = body.count()
                for (index in 0 until count) {
                    val targetTime =
                        LocalDateTime.parse(body[index].date).toLocalDate()

                    val amount = body[index].amount
                    if (!genTimeHash.containsKey(targetTime))
                        genTimeHash[targetTime] = amount
                    else
                        genTimeHash[targetTime] = amount + genTimeHash.getValue(targetTime)
                }
                for (keyValue in genTimeHash) {
                    val key = LocalDate.of(keyValue.key.year, keyValue.key.month, 1)
                    val value = keyValue.value
                    if (genMonthHash.containsKey(key))
                        genMonthHash[key] = genMonthHash.getValue(key) + value
                    else
                        genMonthHash[key] = value.toFloat()
                }
            }
        } catch (e: Exception) {
            generator_present_expected_Graph_API()
        }

        val call3 = api.getMeasurementGen()
        try {
            val execute = call3.execute()
            val body = execute.body()
            Log.d("log", "getMeasurementGen1 :$execute")
            Log.d("log", "getMeasurementGen2 :" + body.toString())
            Log.d(
                "log", "getMeasurementGen3 :" + body?.count().toString()
            )
            if (body.toString() != "[]" && body != null) {
                val count = body.count()
                for (index in 0 until count) {
                    val targetTime = LocalDateTime.parse(body[index].timeCurrent).toLocalDate()
                    val amount = body[index].current * body[index].voltage * 15
                    if (!genTimeHash.containsKey(targetTime))
                        genTimeHash[targetTime] = amount.toInt()
                    else
                        genTimeHash[targetTime] = amount.toInt() + genTimeHash.getValue(targetTime)
                }
            }
        } catch (e: Exception) {

        }

        val call2 = api.getPredictionGen()
        try {
            val execute2 = call2.execute()
            val body2 = execute2.body()
            Log.d("log", "getPredictionGen1 :$execute2")
            Log.d("log", "getPredictionGen2 :" + body2.toString())
            Log.d(
                "log", "getPredictionGen3 :" + body2?.count().toString()
            )
            if (body2.toString() != "[]" && body2 != null) {
                val count = body2.count()
                for (index in 0 until count) {
                    val targetTime =
                        LocalDateTime.parse(body2[index].period).toLocalDate().plusDays(1)

                    val amount = body2[index].amount * 0.002f

                    if (!predictionTimeHash.containsKey(targetTime))
                        predictionTimeHash[targetTime] = amount

                    else
                        predictionTimeHash[targetTime] =
                            amount + predictionTimeHash.getValue(targetTime)
                }
            }
        } catch (e: Exception) {
            Log.d("log", "Exception 발생!!")
            generator_present_expected_Graph_API()
        }
        completeAPI1 = true
    }

    // 발전량 그래프 띄우는 함수
    @RequiresApi(Build.VERSION_CODES.O)
    fun generator_present_expected_Graph(context: Context) {
        nowChart = 1 // 현재 보여주는 차트 번호
        // 그래프 표시
        val generator_entries_present = ArrayList<BarEntry>()
        val generator_entries_expected = ArrayList<BarEntry>()

        var x = 1f
        var y: Float
        for (i in 0..365) {
            y = if (genTimeHash.containsKey(days[i]))
                genTimeHash[days[i]]!!.toFloat()
            else
                0f
            generator_entries_present.add(BarEntry(x++, y))
            Log.d("log", "gen. y = $y")
        }
        y = if (predictionTimeHash.containsKey(LocalDate.now().plusDays(1)))
            predictionTimeHash[LocalDate.now().plusDays(1)]!!
        else
            0f
        Log.d("log", "gen. 마지막 y = $y")
        generator_entries_expected.add(BarEntry(x, y))


        chart_graphfragment.run {
            isAutoScaleMinMaxEnabled = true
            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
//            setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 지정
            setVisibleXRangeMaximum(90f) // 가로 스크롤 생김 + 스크롤 넘어가기전 표출되는 데이터 값
            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawBarShadow(false) // 그래프의 그림자
            setDrawGridBackground(false)//격자구조 넣을건지

            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                axisMinimum = 4000f // 최소값 0
                granularity = 3f // 25 단위마다 선을 그리려고 설정.
                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                setDrawGridLines(true) //격자 라인 활용
                setDrawAxisLine(false) // 축 그리기 설정
                axisLineColor = ContextCompat.getColor(context, R.color.gray_1) // 축 색깔 설정
                gridColor = ContextCompat.getColor(context, R.color.gray_1) // 축 아닌 격자 색깔 설정
                textColor = ContextCompat.getColor(context, R.color.gray_1) // 라벨 텍스트 컬러 설정
                textSize = 13f // 라벨 텍스트 크기
            }
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다.
                granularity = 1f // 1 단위만큼 간격 두기
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
                textColor = ContextCompat.getColor(context, R.color.blue_back) // 라벨 색상
                textColor = ContextCompat.getColor(context, R.color.blue_back2) // 라벨 색상
                textSize = 12f // 텍스트 크기
                valueFormatter = XAxisFormatter_generator() // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
            }

            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
//            setTouchEnabled(true) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용

            legend.isEnabled = true
            legend.textColor = ContextCompat.getColor(context, R.color.gray_1)
            legend.textSize = 12f
        }


        val present_set = BarDataSet(generator_entries_present, "현재 발전량") // 데이터셋 초기화
        val expected_set = BarDataSet(generator_entries_expected, "예상 발전량") // 데이터셋 초기화

        present_set.color = ContextCompat.getColor(context, R.color.blue_back)
        expected_set.color = ContextCompat.getColor(context, R.color.blue_back2)

        val dataSet: ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(present_set)
        dataSet.add(expected_set)

        val chart_data = BarData(dataSet)
        chart_data.barWidth = 0.3f //막대 너비 설정
        chart_graphfragment.run {
            this.data = chart_data //차트의 데이터를 data로 설정해줌.
            this.data.setDrawValues(false)
            setFitBars(true)
            setDrawValueAboveBar(false)
        }
    }

    inner class XAxisFormatter_generator : ValueFormatter() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return strArray.getOrNull(value.toInt() - 1) ?: value.toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun usage_present_expected_Graph_API() {

        val call = api.getYearUsageData()
        try {
            val execute = call.execute()
            val body = execute.body()
            Log.d("log", "getMeasurementUsageWeek1 :$execute")
            Log.d("log", "getMeasurementUsageWeek2 :" + body.toString())
            Log.d(
                "log", "getMeasurementUsageWeek3 :" + body?.count().toString()
            )
            if (body.toString() != "[]" && body != null) {
                val count = body.count()
                for (index in 0 until count) {
                    val targetTime =
                        LocalDateTime.parse(body[index].date).toLocalDate()
                    Log.d("log", "index = $index/$count $targetTime")

                    val amount = body[index].amount

                    if (!usageTimeHash.containsKey(targetTime))
                        usageTimeHash[targetTime] = amount
                    else
                        usageTimeHash[targetTime] =
                            amount + usageTimeHash.getValue(targetTime)
                }
                for (keyValue in usageTimeHash) {
                    val key = LocalDate.of(keyValue.key.year, keyValue.key.month, 1)
                    val value = keyValue.value
                    if (usageMonthHash.containsKey(key))
                        usageMonthHash[key] = usageMonthHash.getValue(key) + value
                    else
                        usageMonthHash[key] = value.toFloat()
                }
            }
        } catch (e: Exception) {
            Log.d("log", e.toString())
            usage_present_expected_Graph_API()
        }

        for (i in 1..USERCOUNT) {
            val call2 = api.getMeasurementUsage(i)
            try {
                val execute2 = call2.execute()
                val body2 = execute2.body()
                if (body2 != null && body2.toString() != "[]") {
                    val count = body2.count()
                    var amount: Float
                    for (index in 0 until count) {
                        val targetTime = LocalDateTime.parse(body2[index].timeCurrent).toLocalDate()

                        amount = body2[index].current * body2[index].voltage * 15

                        if (!usageTimeHash.containsKey(targetTime))
                            usageTimeHash[targetTime] = amount.toInt()
                        else
                            usageTimeHash[targetTime] =
                                amount.toInt() + usageTimeHash.getValue(targetTime)
                    }
                }
            } catch (e: Exception) {
                usage_present_expected_Graph_API()
            }
        }

        val call3 = api.getPredictionUsage()
        try {
            val execute3 = call3.execute()
            val body3 = execute3.body()
            Log.d("log", "getPredictionUsage1 :$execute3")
            Log.d("log", "getPredictionUsage2 :" + body3.toString())
            Log.d(
                "log", "getPredictionUsage3 :" + body3?.count().toString()
            )
//            var logAmount = 0f
            if (body3 != null && body3.toString() != "[]") {
                val count = body3.count()
                var amount: Float
                for (index in 0 until count) {
                    val targetTime =
                        LocalDateTime.parse(body3[index].period).toLocalDate()

                    amount = body3[index].amount * 2.5f
//                    logAmount += amount
                    if (!usagePredictionTimeHash.containsKey(targetTime))
                        usagePredictionTimeHash[targetTime] = amount
                    else
                        usagePredictionTimeHash[targetTime] =
                            amount + usagePredictionTimeHash.getValue(targetTime)
                }
            }
//            Log.d("log", "예측사용량 = $logAmount")

        } catch (e: Exception) {
            usage_present_expected_Graph_API()
        }
        completeAPI2 = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun usage_present_expected_Graph(context: Context) {
        nowChart = 2 // 현재 보여주는 차트 번호

        // 현재 사용량 그래프 정보
        val present_Entry = ArrayList<BarEntry>()
        // 예상 사용량 그래프 정보
        val expected_Entry = ArrayList<BarEntry>()

        var x = 1f
        var y: Float
//        Log.d("log", "그래프 그릴건데 ${usageTimeHash.keys}")
//        Log.d("log", "그래프 그릴건데 $days")
        for (i in 0..365) {
            y = if (usageTimeHash.containsKey(days[i]))
                usageTimeHash[days[i]]!!.toFloat()
            else 0f
            present_Entry.add(BarEntry(x++, y))
        }
        y = if (usagePredictionTimeHash.containsKey(LocalDate.now()))
            usagePredictionTimeHash[LocalDate.now()]!!
        else
            0f
        Log.d("log", "usage. 마지막 y = $y")
        expected_Entry.add((BarEntry(x, y)))

        chart_graphfragment.run {
            isAutoScaleMinMaxEnabled = true
            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
//            setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 지정
            setVisibleXRangeMaximum(90f) // 가로 스크롤 생김 + 스크롤 넘어가기전 표출되는 데이터 값
            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawBarShadow(false) // 그래프의 그림자
            setDrawGridBackground(false)// 격자구조 넣을건지
            axisLeft.run { // 왼쪽 축. 즉 Y방향 축을 뜻한다.
                axisMinimum = 7000f // 최소값 0
                granularity = 1f // 50 단위마다 선을 그리려고 설정.
                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                setDrawGridLines(true) //격자 라인 활용
                setDrawAxisLine(false) // 축 그리기 설정
                axisLineColor = ContextCompat.getColor(context, R.color.gray_1) // 축 색깔 설정
                gridColor = ContextCompat.getColor(context, R.color.gray_1) // 축 아닌 격자 색깔 설정
                textColor = ContextCompat.getColor(context, R.color.gray_1) // 라벨 텍스트 컬러 설정
                textSize = 8f //라벨 텍스트 크기
            }
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다.
                granularity = 1f // 1 단위만큼 간격 두기
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
                textColor = ContextCompat.getColor(context, R.color.blue_back) //라벨 색상
                textColor = ContextCompat.getColor(context, R.color.blue_back2) //라벨 색상
                textSize = 8f // 텍스트 크기
                valueFormatter = XAxisFormatter_usage() // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
            }
            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
//            setTouchEnabled(true) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용

            legend.isEnabled = true
            legend.textColor = ContextCompat.getColor(context, R.color.gray_1)
            legend.textSize = 8f
        }

        val present_set = BarDataSet(present_Entry, "현재 사용량") // 데이터셋 초기화
        val expected_set = BarDataSet(expected_Entry, "예상 사용량") // 데이터셋 초기화

        present_set.color = ContextCompat.getColor(context, R.color.blue_back)
        expected_set.color = ContextCompat.getColor(context, R.color.blue_back2)

        val dataSet: ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(present_set)
        dataSet.add(expected_set)

        val chart_data = BarData(dataSet)
        chart_data.barWidth = 0.3f //막대 너비 설정
        Log.d("log", "막대 너비는 ${chart_data.barWidth}")
        chart_graphfragment.run {
            this.data = chart_data //차트의 데이터를 data로 설정해줌.
            this.data.setDrawValues(false)
            setFitBars(true)
        }
    }

    inner class XAxisFormatter_usage : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return strArray.getOrNull(value.toInt() - 1) ?: value.toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun gen_month_Graph(context: Context) {
        nowChart = 3 // 현재 보여주는 차트 번호

        // 현재 사용량 그래프 정보
        val month_Entry = ArrayList<BarEntry>()

        var x = 1f
        var y: Float
        Log.d("log", "그래프 그릴건데 ${genMonthHash.keys}")
        for (i in genMonthHash) {
            y = i.value
            month_Entry.add((BarEntry(x++, y)))
        }

        chart_graphfragment.run {
            isAutoScaleMinMaxEnabled = true
            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
//            setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 지정
            setVisibleXRangeMaximum(90f) // 가로 스크롤 생김 + 스크롤 넘어가기전 표출되는 데이터 값
            setPinchZoom(true) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawBarShadow(false) // 그래프의 그림자
            setDrawGridBackground(false)// 격자구조 넣을건지
            axisLeft.run { // 왼쪽 축. 즉 Y방향 축을 뜻한다.
                axisMinimum = 10000f // 최소값 0
                granularity = 1f // 50 단위마다 선을 그리려고 설정.
                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                setDrawGridLines(true) //격자 라인 활용
                setDrawAxisLine(false) // 축 그리기 설정
                axisLineColor = ContextCompat.getColor(context, R.color.gray_1) // 축 색깔 설정
                gridColor = ContextCompat.getColor(context, R.color.gray_1) // 축 아닌 격자 색깔 설정
                textColor = ContextCompat.getColor(context, R.color.gray_1) // 라벨 텍스트 컬러 설정
                textSize = 8f //라벨 텍스트 크기
            }
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다.
                granularity = 1f // 1 단위만큼 간격 두기
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
                textColor = ContextCompat.getColor(context, R.color.blue_back) //라벨 색상
                textColor = ContextCompat.getColor(context, R.color.blue_back2) //라벨 색상
                textSize = 8f // 텍스트 크기
                valueFormatter = XAxisFormatter_usage_month() // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
            }
            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
//            setTouchEnabled(true) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용

            legend.isEnabled = true
            legend.textColor = ContextCompat.getColor(context, R.color.gray_1)
            legend.textSize = 8f
        }

        val month_set = BarDataSet(month_Entry, "월별 사용량") // 데이터셋 초기화

        month_set.color = ContextCompat.getColor(context, R.color.blue_back)

        val dataSet: ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(month_set)

        val chart_data = BarData(dataSet)
        chart_data.barWidth = 0.3f //막대 너비 설정
        chart_graphfragment.run {
            this.data = chart_data //차트의 데이터를 data로 설정해줌.
            this.data.setDrawValues(false)
            setFitBars(false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun usage_month_Graph(context: Context) {
        nowChart = 4 // 현재 보여주는 차트 번호

        // 현재 사용량 그래프 정보
        val month_Entry = ArrayList<BarEntry>()

        var x = 1f
        var y: Float
        Log.d("log", "그래프 그릴건데 ${usageTimeHash.keys}")
        Log.d("log", "그래프 그릴건데 $days")
        for (i in usageMonthHash) {
            y = i.value
            month_Entry.add((BarEntry(x++, y)))
        }

        chart_graphfragment.run {
            isAutoScaleMinMaxEnabled = true
            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
//            setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 지정
            setVisibleXRangeMaximum(90f) // 가로 스크롤 생김 + 스크롤 넘어가기전 표출되는 데이터 값
            setPinchZoom(true) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawBarShadow(false) // 그래프의 그림자
            setDrawGridBackground(false)// 격자구조 넣을건지
            axisLeft.run { // 왼쪽 축. 즉 Y방향 축을 뜻한다.
                axisMinimum = 10000f // 최소값 0
                granularity = 1f // 50 단위마다 선을 그리려고 설정.
                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                setDrawGridLines(true) //격자 라인 활용
                setDrawAxisLine(false) // 축 그리기 설정
                axisLineColor = ContextCompat.getColor(context, R.color.gray_1) // 축 색깔 설정
                gridColor = ContextCompat.getColor(context, R.color.gray_1) // 축 아닌 격자 색깔 설정
                textColor = ContextCompat.getColor(context, R.color.gray_1) // 라벨 텍스트 컬러 설정
                textSize = 8f //라벨 텍스트 크기
            }
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다.
                granularity = 1f // 1 단위만큼 간격 두기
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
                textColor = ContextCompat.getColor(context, R.color.blue_back) //라벨 색상
                textColor = ContextCompat.getColor(context, R.color.blue_back2) //라벨 색상
                textSize = 8f // 텍스트 크기
                valueFormatter = XAxisFormatter_usage_month() // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
            }
            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
//            setTouchEnabled(true) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용

            legend.isEnabled = true
            legend.textColor = ContextCompat.getColor(context, R.color.gray_1)
            legend.textSize = 8f
        }

        val month_set = BarDataSet(month_Entry, "월별 사용량") // 데이터셋 초기화

        month_set.color = ContextCompat.getColor(context, R.color.blue_back)

        val dataSet: ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(month_set)

        val chart_data = BarData(dataSet)
        chart_data.barWidth = 0.3f //막대 너비 설정
        chart_graphfragment.run {
            this.data = chart_data //차트의 데이터를 data로 설정해줌.
            this.data.setDrawValues(false)
            setFitBars(false)
        }
    }

    inner class XAxisFormatter_usage_month : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return strArrayMonth.getOrNull(value.toInt() - 1) ?: value.toString()
        }
    }
}