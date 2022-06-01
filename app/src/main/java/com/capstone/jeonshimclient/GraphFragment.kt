package com.capstone.jeonshimclient

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.fragment_graph.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.log
import kotlin.text.Typography.times

class GraphFragment : Fragment() {
    private val STARTHOUR: Int = 13
    private val ENDHOUR: Int = 19

    @RequiresApi(Build.VERSION_CODES.O)
    private val STARTDAYOFWEEK: DayOfWeek = DayOfWeek.MONDAY

    @RequiresApi(Build.VERSION_CODES.O)
    private val ENDDAYOFWEEK: DayOfWeek = DayOfWeek.SUNDAY
    private val USERCOUNT: Int = 3

    private lateinit var api: APIS
    private var genTimeHash: HashMap<LocalDate, Float> = HashMap()
    private var usageTimeHash: HashMap<LocalDate, Float> = HashMap()
    private var usagePredictionTimeHash: HashMap<LocalDate, Float> = HashMap()
    private var predictionTimeHash: HashMap<LocalDate, Float> = HashMap()
    private var completeAPI1: Boolean = false
    private var completeAPI2: Boolean = false
    private var nowChart: Int = 1 // 1 or 2 현재 보여주는 chart


    // 그래프 x축 항목들 앞에 5일 + 오늘 + 내일 -> 총 7일, 달/일로 표시
    private val days: ArrayList<LocalDate> = ArrayList()

    // 그래프 x축 항목들 13시부터 19시까지 표기
    private val strArray = ArrayList<String>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // days에 들어가야할 날짜들부터 넣어줌
        var date = LocalDate.now().minusDays(5)
        Log.d("log", "오늘 " + LocalDate.now().toString())
        for (i in 0..6) {
            Log.d("log", "배열 만드는 중 " + date.toString())
            days.add(date)
            date = date.plusDays(1)
        }
        // x축 항목 만들어주려고 string형 배열
        for (i in 0..6) {
            strArray.add("${days[i].monthValue}/${days[i].dayOfMonth}")
        }


        api = APIS.create()
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("log", "Coroutine.launch")
            if (!completeAPI1) {
                Log.d("log", "API1")
                generator_present_expected_Graph_API()
                if (nowChart == 1)
                    withContext(Dispatchers.Main) {
                        generator_present_expected_Graph(requireContext())
                    }
            }
            if (!completeAPI2) {
                Log.d("log", "API2")
                usage_present_expected_Graph_API()
                if (nowChart == 2) {
                    withContext(Dispatchers.Main) {
                        usage_present_expected_Graph(requireContext())
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_graph, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor", "ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("log", "onViewCreated")
        Log.d("log", "graph")
        graphFragmentMain()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun graphFragmentMain() {
        // 어플 시작시 띄우는 그래프 = 발전량 그래프
        generator_present_expected_Graph(requireContext())
        bt_start_graph1.setOnClickListener {
            bt_start_graph1.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.background_bs_blue)
            bt_start_graph1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            bt_start_graph2.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.background_bs_gray)
            bt_start_graph2.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_2))

            generator_present_expected_Graph(requireContext())
            text_fragment_graph2.text =
                "일주일 동안 건물에서 발전시킨 전력의 양을 보여줍니다.\n실제로 발전된 전력의 양과 이후에 발전될 전력의 양을 \n동시에 확인할 수 있습니다."

//            val startgraph = ExpectedGeneratorGraphDialog(requireContext())
//            startgraph.startDialog(requireContext())
        }
        bt_start_graph2.setOnClickListener {
            bt_start_graph2.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.background_bs_blue)
            bt_start_graph2.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            bt_start_graph1.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.background_bs_gray)
            bt_start_graph1.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_2))

            usage_present_expected_Graph(requireContext())
            text_fragment_graph2.text =
                "일주일 동안 건물 전체에서 사용된 전력의 양을 보여줍니다.\n실제로 사용한 전력의 양과 이후에 사용될 전력의 양을 \n동시에 확인할 수 있습니다.\n( 세대별 사용량은 세대 정보 페이지에서 조회합니다. )"
//            val startgraph = ExpectedUsageGraphDialog(requireContext())
//            startgraph.startDialog(requireContext())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generator_present_expected_Graph_API() {
        val call = api.getMeasurementGenWeek()
        try {
            val execute = call.execute()
            val body = execute.body()
            Log.d("log", "getMeasurementGenWeek1 :$execute")
            Log.d("log", "getMeasurementGenWeek2 : ${body.toString()}")
            Log.d(
                "log", "getMeasurementGenWeek3 : ${body?.count().toString()}"
            )

            if (body.toString() != "[]" && body != null) {
                val count = body.count()
                for (index in 0 until count) {
                    val targetTime =
                        LocalDateTime.parse(body[index].timeCurrent).toLocalDate()

                    val amount = body[index].current * body[index].voltage * 15
                    if (!genTimeHash.containsKey(targetTime))
                        genTimeHash[targetTime] = amount
                    else
                        genTimeHash[targetTime] = amount + genTimeHash.getValue(targetTime)
                }
            }
        } catch (e: Exception) {
            generator_present_expected_Graph_API()
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
                        LocalDateTime.parse(body2[index].period).toLocalDate()
                    Log.d("log", "발견된 targetTime = $targetTime")

                    val amount = body2[index].amount

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
        for (i in 0 until 6) {
            y = if (genTimeHash.containsKey(days[i]))
                genTimeHash[days[i]]!!
            else
                0f
            generator_entries_present.add(BarEntry(x++, y))
        }
        y = if(predictionTimeHash.containsKey(days[6]))
                predictionTimeHash[days[6]]!!
            else
                0f
        generator_entries_expected.add(BarEntry(x++, y))

        chart_graphfragment.run {
            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
            setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 지정
            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawBarShadow(false) // 그래프의 그림자
            setDrawGridBackground(false)//격자구조 넣을건지
            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                axisMaximum = 501f //100 위치에 선을 그리기 위해 101f로 맥시멈값 설정
                axisMinimum = 0f // 최소값 0
                granularity = 25f // 25 단위마다 선을 그리려고 설정.
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
            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
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
            setFitBars(true)
            invalidate()
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
        Log.d("log", "api가 반복되고 있는 것인지 확인")
        for (i in 1..USERCOUNT) {
            val call = api.getMeasurementUsageWeek("$i")
            try {
                val execute = call.execute()
                val body = execute.body()
                Log.d("log", "getMeasurementUsageWeek$i 1 :$execute")
                Log.d("log", "getMeasurementUsageWeek$i 2 :" + body.toString())
                Log.d(
                    "log", "getMeasurementUsageWeek$i 3 :" + body?.count().toString()
                )
                if (body.toString() != "[]" && body != null) {
                    val count = body.count()
                    for (index in 0 until count) {
                        val targetTime =
                            LocalDateTime.parse(body[index].timeCurrent).toLocalDate()
                        Log.d("log", "index = $index/$count $targetTime")

                        val amount = body[index].current * body[index].voltage * 15

                        if (!usageTimeHash.containsKey(targetTime))
                            usageTimeHash[targetTime] = amount
                        else
                            usageTimeHash[targetTime] =
                                amount + usageTimeHash.getValue(targetTime)

                        Log.d("log", "$usageTimeHash[targetTime]")
                    }
                }
            } catch (e: Exception) {
                Log.d("log", e.toString())
                usage_present_expected_Graph_API()
            }
        }

        for (i in 1..USERCOUNT) {
            val call = api.getPredictionUsage("$i")
            try {
                val execute = call.execute()
                val body = execute.body()
                Log.d("log", "getPredictionUsage$i 1 :$execute")
                Log.d("log", "getPredictionUsage$i 2 :" + body.toString())
                Log.d(
                    "log", "getPredictionUsage$i 3 :" + body?.count().toString()
                )
                if (body.toString() != "[]" && body != null) {
                    val count = body.count()
                    var amount: Float
                    for (index in 0 until count) {
                        val targetTime =
                            LocalDateTime.parse(body[index].period).toLocalDate()

                        amount = body[index].amount

                        if (!usagePredictionTimeHash.containsKey(targetTime))
                            usagePredictionTimeHash[targetTime] = amount
                        else
                            usagePredictionTimeHash[targetTime] =
                                amount + usagePredictionTimeHash.getValue(targetTime)
                    }
                }
            } catch (e: Exception) {
                usage_present_expected_Graph_API()
            }
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
        Log.d("log", "그래프 그릴건데 ${usageTimeHash.keys}")
        Log.d("log", "그래프 그릴건데 $days")
        for (i in 0 until 6) {
            y = if (usageTimeHash.containsKey(days[i]))
                usageTimeHash[days[i]]!!
            else 0f
            present_Entry.add(BarEntry(x++, y))
        }
        y = if (usagePredictionTimeHash.containsKey(days[6]))
            usagePredictionTimeHash[days[6]]!!
        else
            0f
        expected_Entry.add((BarEntry(x++, y)))

        chart_graphfragment.run {
            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
            setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 지정
            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawBarShadow(false) // 그래프의 그림자
            setDrawGridBackground(false)// 격자구조 넣을건지
            axisLeft.run { // 왼쪽 축. 즉 Y방향 축을 뜻한다.
                axisMaximum = 20001f // 100 위치에 선을 그리기 위해 101f로 맥시멈값 설정
                axisMinimum = 0f // 최소값 0
                granularity = 1000f // 50 단위마다 선을 그리려고 설정.
                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                setDrawGridLines(true) //격자 라인 활용
                setDrawAxisLine(false) // 축 그리기 설정
                axisLineColor = ContextCompat.getColor(context, R.color.gray_1) // 축 색깔 설정
                gridColor = ContextCompat.getColor(context, R.color.gray_1) // 축 아닌 격자 색깔 설정
                textColor = ContextCompat.getColor(context, R.color.gray_1) // 라벨 텍스트 컬러 설정
                textSize = 12f //라벨 텍스트 크기
            }
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다.
                granularity = 1f // 1 단위만큼 간격 두기
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
                textColor = ContextCompat.getColor(context, R.color.blue_back) //라벨 색상
                textColor = ContextCompat.getColor(context, R.color.blue_back2) //라벨 색상
                textSize = 12f // 텍스트 크기
                valueFormatter = XAxisFormatter_usage() // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
            }
            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용

            legend.isEnabled = true
            legend.textColor = ContextCompat.getColor(context, R.color.gray_1)
            legend.textSize = 12f
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
        chart_graphfragment.run {
            this.data = chart_data //차트의 데이터를 data로 설정해줌.
            setFitBars(true)
            invalidate()
        }
    }

    inner class XAxisFormatter_usage : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return strArray.getOrNull(value.toInt() - 1) ?: value.toString()
        }
    }
}