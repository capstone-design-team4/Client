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
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.fragment_graph.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.time.LocalDateTime

class GraphFragment : Fragment() {
    private val STARTHOUR: Int = 13
    private val ENDHOUR: Int = 19
    private val USERCOUNT: Int = 3

    var api: APIS = APIS.create()
    var genTimeHash: HashMap<Int, Float> = HashMap()
    var usageTimeHash: HashMap<Int, Float> = HashMap()
    var usagePredictionTimeHash: HashMap<Int, Float> = HashMap()
    var predictionTimeHash: HashMap<Int, Float> = HashMap()
    var weCanDrawGraphGen: Boolean = false
    var weCanDrawGraphUsage: Boolean = false


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

        // 코루틴 사용하여 호출함

        // api 호출
        generator_present_expected_Graph_API()
        usage_present_expected_Graph_API()
        graphFragmentMain()
    }

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

        CoroutineScope(Dispatchers.IO).launch {
            val call: Call<List<MeasurementGenDay>> = api.getMeasurementGenDay()
            val execute = call.execute()
            val body = execute.body()
            Log.d("log", "getMeasurementGenDay1 :" + execute.toString())
            Log.d("log", "getMeasurementGenDay2 :" + body.toString())
            Log.d(
                "log", "getMeasurementGenDay3 :" + body?.count().toString()
            )
            if(body.toString() != "[]" && body != null){
                val count = body?.count()!!
                for (index in 0 until count) {
                    val targetTime =
                        LocalDateTime.parse(body[index].timeCurrent).toLocalTime().hour
                    if (targetTime < STARTHOUR || targetTime > ENDHOUR)
                        continue

                    val current = body[index].current
                    val voltage = body[index].voltage

                    if (!genTimeHash.containsKey(targetTime))
                        genTimeHash[targetTime] = 15 * current * voltage
                    else
                        genTimeHash[targetTime] =
                            15 * current * voltage + genTimeHash.getValue(targetTime)
                }
            }

            val call2 = api.getPredictionGen()
            val execute2 = call2.execute()
            val body2 = execute2.body()
            Log.d("log", "getPredictionGen1 :" + execute2.toString())
            Log.d("log", "getPredictionGen2 :" + body2.toString())
            Log.d(
                "log", "getPredictionGen3 :" + body2?.count().toString()
            )
            if(body2.toString() != "[]" && body2 != null){
                val count = body2?.count()!!
                for (index in 0 until count) {
                    val targetTime =
                        LocalDateTime.parse(body2[index].period).toLocalTime().hour
                    if (targetTime < STARTHOUR || targetTime > ENDHOUR)
                        continue

                    val amount = body2[index].amount

                    if (!genTimeHash.containsKey(targetTime))
                        genTimeHash[targetTime] = amount
                    else
                        genTimeHash[targetTime] =
                            amount + genTimeHash.getValue(targetTime)
                }
            }
        }

        weCanDrawGraphGen = true
    }

    // 발전량 그래프 띄우는 함수
    fun generator_present_expected_Graph(context: Context) {
        // 그래프 그릴 판단 못했으면 리턴
        if (!weCanDrawGraphGen) return

        // 그래프 표시
        val generator_entries_present = ArrayList<BarEntry>()
        val generator_entries_expected = ArrayList<BarEntry>()

        var x = 1.2f
        var y: Float
        for (key in STARTHOUR..ENDHOUR) {
            if (genTimeHash.containsKey(key)) {
                y = genTimeHash[key]!!
                generator_entries_present.add(BarEntry(x++, y))
            } else {
                for (key2 in key..ENDHOUR) {
                    if (genTimeHash.containsKey(key2)) {
                        y = predictionTimeHash[key2]!!
                        generator_entries_expected.add(BarEntry(x++, y))
                    } else
                        break
                }
                break
            }
        }
//        영향 없으면 지울 코드
//        // 현재 발전량 그래프 정보
//        val present_Entry = ArrayList<BarEntry>()
//        present_Entry.add(BarEntry(1.2f, 20.0f))
//        present_Entry.add(BarEntry(2.2f, 40.0f))
//        present_Entry.add(BarEntry(3.2f, 30.0f))
//
//        // 예상 발전량 그래프 정보
//        val expected_Entry = ArrayList<BarEntry>()
//        expected_Entry.add(BarEntry(4.2f, 40.0f))
//        expected_Entry.add(BarEntry(5.2f, 70.0f))
//        expected_Entry.add(BarEntry(6.2f, 20.0f))
//        expected_Entry.add(BarEntry(7.2f, 40.0f))


        chart_graphfragment.run {
            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
            setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 지정
            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawBarShadow(false) // 그래프의 그림자
            setDrawGridBackground(false)//격자구조 넣을건지
            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                axisMaximum = 101f //100 위치에 선을 그리기 위해 101f로 맥시멈값 설정
                axisMinimum = 0f // 최소값 0
                granularity = 25f // 50 단위마다 선을 그리려고 설정.
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
        //        private val days = arrayOf("13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//            return days.getOrNull(value.toInt() - 1) ?: value.toString()
            var timesString: ArrayList<String> = ArrayList()

            for (i in STARTHOUR..ENDHOUR)
                timesString.add("$i:00")
            return timesString.getOrNull(value.toInt() - 1) ?: value.toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun usage_present_expected_Graph_API() {

        CoroutineScope(Dispatchers.IO).launch {
            for (i in 1..USERCOUNT) {
                val call: Call<List<MeasurementUsageDay>> = api.getMeasurementUsageDay("$i")
                val execute = call.execute()
                val body = execute.body()
                Log.d("log", "getMeasurementUsageDay$i 1 :" + execute.toString())
                Log.d("log", "getMeasurementUsageDay$i 2 :" + body.toString())
                Log.d(
                    "log", "getMeasurementUsageDay$i 3 :" + body?.count().toString()
                )
                if (body.toString() != "[]" && body != null) {
                    val count = body?.count()!!
                    for (index in 0 until count) {
                        val targetTime =
                            LocalDateTime.parse(body[index].timeCurrent).toLocalTime().hour
                        if (targetTime < STARTHOUR || targetTime > ENDHOUR)
                            continue

                        val current = body[index].current
                        val voltage = body[index].voltage

                        if (!usageTimeHash.containsKey(targetTime))
                            usageTimeHash[targetTime] = 15 * current * voltage
                        else
                            usageTimeHash[targetTime] =
                                15 * current * voltage + usageTimeHash.getValue(targetTime)
                    }
                }
            }

            for (i in 1..USERCOUNT) {
                val call = api.getPredictionUsage("$i")
                val execute = call.execute()
                val body = execute.body()
                Log.d("log", "getPredictionUsage$i 1 :" + execute.toString())
                Log.d("log", "getPredictionUsage$i 2 :" + body.toString())
                Log.d(
                    "log", "getPredictionUsage$i 3 :" + body?.count().toString()
                )
                if (body.toString() != "[]" && body != null) {
                    val count = body?.count()!!
                    for (index in 0 until count) {
                        val targetTime =
                            LocalDateTime.parse(body[index].period).toLocalTime().hour
                        if (targetTime < STARTHOUR || targetTime > ENDHOUR)
                            continue

                        val amount = body[index].amount

                        if (!usagePredictionTimeHash.containsKey(targetTime))
                            usagePredictionTimeHash[targetTime] = amount
                        else
                            usageTimeHash[targetTime] = amount + usageTimeHash.getValue(targetTime)
                    }
                }
            }

            weCanDrawGraphUsage = true
        }
    }

    fun usage_present_expected_Graph(context: Context) {
        if(!weCanDrawGraphUsage) return
        // 현재 사용량 그래프 정보
        val present_Entry = ArrayList<BarEntry>()
        // 예상 사용량 그래프 정보
        val expected_Entry = ArrayList<BarEntry>()

        var x = 1.2f
        var y: Float
        for (key in STARTHOUR..ENDHOUR) {
            if (genTimeHash.containsKey(key)) {
                y = genTimeHash[key]!!
                present_Entry.add(BarEntry(x++, y))
            } else {
                for (key2 in key..ENDHOUR) {
                    if (genTimeHash.containsKey(key2)) {
                        y = predictionTimeHash[key2]!!
                        expected_Entry.add(BarEntry(x++, y))
                    } else
                        break
                }
                break
            }
        }


        chart_graphfragment.run {
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
        //        private val days = arrayOf("13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val timesString = ArrayList<String>()
            for (time in STARTHOUR..ENDHOUR)
                timesString.add("$time:00")
            return timesString.getOrNull(value.toInt() - 1) ?: value.toString()
        }
    }

}