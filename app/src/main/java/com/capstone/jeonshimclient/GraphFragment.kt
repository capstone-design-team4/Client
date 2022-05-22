package com.capstone.jeonshimclient

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.fragment_graph.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

class GraphFragment : Fragment() {
    var api: APIS = APIS.create()
    var timesArray: ArrayList<Int> = ArrayList()
    var genTimeHash: HashMap<Int, Float> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        api.getMeasurementGenDay().enqueue(object : Callback<List<MeasurementGenDay>> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<List<MeasurementGenDay>>,
                response: Response<List<MeasurementGenDay>>
            ) {
                Log.d("log", "1 :" + response.toString())
                Log.d("log", "2 :" + response.body().toString())
                Log.d("log", "3 :" + response.body()?.count().toString())

                // 해당 response에 내용이 없을 경우 차트를 그릴 수 없도록 한다.
                if (response.body().toString() != "[]") {
                    val count: Int = response.body()?.count()!!
                    // 각 시간대를 key로 가지는 hashmap에 targetTime에 대응하는 value를 이용하여 set한다.
                    for (index in 0 until count) {
                        val targetTime: Int =
                            LocalDateTime.parse(response.body()?.get(index)?.timeCurrent)
                                .toLocalTime().hour
                        val current: Float = response.body()?.get(index)!!.current
                        val voltage: Float = response.body()?.get(index)!!.voltage

                        if (!genTimeHash.containsKey(targetTime)) {
                            genTimeHash[targetTime] = 15 * current * voltage
                            //Log.d("log", "value = " + genTimeHash[targetTime].toString() + " targetTime = " + targetTime.toString())
                        } else {
                            genTimeHash[targetTime] =
                                15 * current * voltage + genTimeHash.getValue(targetTime)
                            //Log.d("log", "replaced value = " + genTimeHash[targetTime].toString() + " targetTime = " + targetTime.toString())
                        }
                    }
                    // 정렬된 array가 필요하지만 hashMap은 정렬할 수 없으므로
                    // 새로운 array를 만듭니다. 차트를 그리기 위함입니다.
                    timesArray = ArrayList(genTimeHash.keys)
                    timesArray.sort()

                    generatorGraph()
                    usageGraph()
                }
            }

            override fun onFailure(call: Call<List<MeasurementGenDay>>, t: Throwable) {
                // 실패
                Log.d("log", t.message.toString())
                Log.d("log", "fail")
            }
        })
    }

    fun generatorGraph() {
        val generator_entries_present = ArrayList<BarEntry>()
        var x: Float = 1.0f
        var y: Float
        if (genTimeHash.isNotEmpty()) {
            for (key in timesArray) {
                y = genTimeHash[key]!!
                generator_entries_present.add(BarEntry(x, y))
                x += 1
            }
        }
//        generator_entries_present.add(BarEntry(1.2f,10.0f))
//        generator_entries_present.add(BarEntry(2.2f,30.0f))
//        generator_entries_present.add(BarEntry(3.2f,70.0f))

        val generator_entries_expected = ArrayList<BarEntry>()
        Log.d("log", x.toString())
        generator_entries_expected.add(BarEntry(x + 1, 40.0f))
        generator_entries_expected.add(BarEntry(x + 2, 50.0f))
        generator_entries_expected.add(BarEntry(x + 3, 60.0f))
        generator_entries_expected.add(BarEntry(x + 4, 20.0f))

        generatorChart.run {
            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
            setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 지정
            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawBarShadow(false) // 그래프의 그림자
            setDrawGridBackground(false)// 격자구조 넣을건지
            axisLeft.run { // 왼쪽 축. 즉 Y방향 축을 뜻한다.
                axisMaximum = 501f // 100 위치에 선을 그리기 위해 101f로 맥시멈값 설정
                axisMinimum = 0f // 최소값 0
                granularity = 50f // 50 단위마다 선을 그리려고 설정.
                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                setDrawGridLines(false) //격자 라인 활용
                setDrawAxisLine(false) // 축 그리기 설정
                axisLineColor = ContextCompat.getColor(context, R.color.white) // 축 색깔 설정
                gridColor = ContextCompat.getColor(context, R.color.white) // 축 아닌 격자 색깔 설정
                textColor = ContextCompat.getColor(context, R.color.white) // 라벨 텍스트 컬러 설정
                textSize = 13f //라벨 텍스트 크기
            }
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM // X축을 아래에다가 둔다.
                granularity = 1f // 1 단위만큼 간격 두기
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
                textColor = ContextCompat.getColor(context, R.color.white) //라벨 색상
                textSize = 12f // 텍스트 크기
                valueFormatter = XAxisFormatter() // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
            }
            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용


            legend.isEnabled = true
            axisLeft.textColor = Color.WHITE
            xAxis.textColor = Color.WHITE
            legend.textColor = Color.WHITE
            legend.textSize = 10f
            description.textColor = Color.WHITE
        }


        var generator_set_present = BarDataSet(generator_entries_present, "발전량") // 데이터셋 초기화
        var generator_set_expected = BarDataSet(generator_entries_expected, "예상 발전량")

        generator_set_present.color = ContextCompat.getColor(requireContext(), R.color.teal_900)
        generator_set_expected.color = ContextCompat.getColor(requireContext(), R.color.teal_200)

        val dataSet: ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(generator_set_present)
        dataSet.add(generator_set_expected)

        val generator_data = BarData(dataSet)
        generator_data.barWidth = 0.3f //막대 너비 설정
        generatorChart.run {
            this.data = generator_data //차트의 데이터를 data로 설정해줌.
            generator_data.barWidth = 0.3f // 막대 너비 설정
            generatorChart.run {
                this.data = generator_data // 차트의 데이터를 data로 설정해줌.
                setFitBars(true)
                invalidate()
            }
        }
    }

    fun usageGraph() {
        val usage_entries_present = ArrayList<BarEntry>()
        usage_entries_present.add(BarEntry(1.2f, 20.0f))
        usage_entries_present.add(BarEntry(2.2f, 40.0f))
        usage_entries_present.add(BarEntry(3.2f, 30.0f))

        val usage_entries_expected = ArrayList<BarEntry>()
        usage_entries_expected.add(BarEntry(4.2f, 40.0f))
        usage_entries_expected.add(BarEntry(5.2f, 70.0f))
        usage_entries_expected.add(BarEntry(6.2f, 20.0f))
        usage_entries_expected.add(BarEntry(7.2f, 40.0f))

        usageChart.run {
            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
            setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 지정
            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawBarShadow(false) // 그래프의 그림자
            setDrawGridBackground(false)//격자구조 넣을건지
            axisLeft.run { // 왼쪽 축. 즉 Y방향 축을 뜻한다.
                axisMaximum = 101f // 100 위치에 선을 그리기 위해 101f로 맥시멈값 설정
                axisMinimum = 0f // 최소값 0
                granularity = 50f // 50 단위마다 선을 그리려고 설정.
                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                setDrawGridLines(false) //격자 라인 활용
                setDrawAxisLine(false) // 축 그리기 설정
                axisLineColor = ContextCompat.getColor(context, R.color.black) // 축 색깔 설정
                gridColor = ContextCompat.getColor(context, R.color.black) // 축 아닌 격자 색깔 설정
                textColor = ContextCompat.getColor(context, R.color.black) // 라벨 텍스트 컬러 설정
                textSize = 13f //라벨 텍스트 크기
                axisLineColor = ContextCompat.getColor(context, R.color.white) // 축 색깔 설정
                gridColor = ContextCompat.getColor(context, R.color.white) // 축 아닌 격자 색깔 설정
                textColor = ContextCompat.getColor(context, R.color.white) // 라벨 텍스트 컬러 설정
                textSize = 13f // 라벨 텍스트 크기
            }
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM // X축을 아래에다가 둔다.
                granularity = 1f // 1 단위만큼 간격 두기
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
                textColor = ContextCompat.getColor(context, R.color.black) //라벨 색상
                textColor = ContextCompat.getColor(context, R.color.white) // 라벨 색상
                textSize = 12f // 텍스트 크기
                valueFormatter = XAxisFormatter() // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
            }
            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용


            legend.isEnabled = true
            axisLeft.textColor = Color.WHITE
            xAxis.textColor = Color.WHITE
            legend.textColor = Color.WHITE
            legend.textSize = 10f
            description.textColor = Color.WHITE
        }


        var usage_set_present = BarDataSet(usage_entries_present, "사용량") // 데이터셋 초기화
        var usage_set_expected = BarDataSet(usage_entries_expected, "예상 사용량")

        usage_set_present.color = ContextCompat.getColor(requireContext(), R.color.gray_1)
        usage_set_expected.color = ContextCompat.getColor(requireContext(), R.color.white)

        val dataSet: ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(usage_set_present)
        dataSet.add(usage_set_expected)

        val usage_data = BarData(dataSet)
        usage_data.barWidth = 0.3f // 막대 너비 설정
        usageChart.run {
            this.data = usage_data // 차트의 데이터를 data로 설정해줌.
            setFitBars(true)
            invalidate()
        }
    }


    // 범석님 잠깐 주석 처리만 하였습니다
//        inner class XAxisFormatter : ValueFormatter() {
//        private var times = timesArray//arrayOf("13:00","14:00","15:00","16:00","17:00","18:00","19:00")
//
//        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//            var timesString: ArrayList<String> = ArrayList()
//            for(time in timesArray){
//                timesString.add(time.toString())
//            }
//            if(timesArray.count() < 24)
//                for(i in 1..24-timesArray.count())
//                    timesString.add((timesArray.last()+i).toString())
//            return timesString.getOrNull(value.toInt()-1) ?: value.toString()
//        }
    inner class XAxisFormatter : ValueFormatter() {
        private val days = arrayOf("13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt() - 1) ?: value.toString()
            var timesString: ArrayList<String> = ArrayList()
            for (time in timesArray) {
                timesString.add(time.toString())
            }
            if (timesArray.count() < 24)
                for (i in 1..24 - timesArray.count())
                    timesString.add((timesArray.last() + i).toString())
            return timesString.getOrNull(value.toInt() - 1) ?: value.toString()
        }
    }
}

// 아래는 비교용
//    inner class XAxisFormatter : ValueFormatter() {
//        private val days = arrayOf("13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00")
//        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//            return days.getOrNull(value.toInt() - 1) ?: value.toString()
//        }
//    }
//}


//class GraphFragment : Fragment() {
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_graph, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        generatorGraph()
//        usageGraph()
//    }
//
//    fun generatorGraph(){
//        val generator_entries_present = ArrayList<BarEntry>()
//        generator_entries_present.add(BarEntry(1.2f,10.0f))
//        generator_entries_present.add(BarEntry(2.2f,30.0f))
//        generator_entries_present.add(BarEntry(3.2f,70.0f))
//
//        val generator_entries_expected = ArrayList<BarEntry>()
//        generator_entries_expected.add(BarEntry(4.2f,40.0f))
//        generator_entries_expected.add(BarEntry(5.2f,50.0f))
//        generator_entries_expected.add(BarEntry(6.2f,60.0f))
//        generator_entries_expected.add(BarEntry(7.2f,20.0f))
//
//        userChart.run {
//            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
//            setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 지정
//            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
//            setDrawBarShadow(false) //그래프의 그림자
//            setDrawGridBackground(false)//격자구조 넣을건지
//            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
//                axisMaximum = 101f //100 위치에 선을 그리기 위해 101f로 맥시멈값 설정
//                axisMinimum = 0f // 최소값 0
//                granularity = 50f // 50 단위마다 선을 그리려고 설정.
//                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
//                setDrawGridLines(false) //격자 라인 활용
//                setDrawAxisLine(false) // 축 그리기 설정
//                axisLineColor = ContextCompat.getColor(context,R.color.white) // 축 색깔 설정
//                gridColor = ContextCompat.getColor(context,R.color.white) // 축 아닌 격자 색깔 설정
//                textColor = ContextCompat.getColor(context,R.color.white) // 라벨 텍스트 컬러 설정
//                textSize = 13f //라벨 텍스트 크기
//            }
//            xAxis.run {
//                position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다.
//                granularity = 1f // 1 단위만큼 간격 두기
//                setDrawAxisLine(true) // 축 그림
//                setDrawGridLines(false) // 격자
//                textColor = ContextCompat.getColor(context,R.color.white) //라벨 색상
//                textSize = 12f // 텍스트 크기
//                valueFormatter = XAxisFormatter() // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
//            }
//            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
//            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
//            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
//
//
//            legend.isEnabled = true
//            axisLeft.textColor = Color.WHITE
//            xAxis.textColor = Color.WHITE
//            legend.textColor = Color.WHITE
//            legend.textSize = 10f
//            description.textColor = Color.WHITE
//        }
//
//
//        var generator_set_present = BarDataSet(generator_entries_present,"발전량") // 데이터셋 초기화
//        var generator_set_expected = BarDataSet(generator_entries_expected, "예상 발전량")
//
//        generator_set_present.color = ContextCompat.getColor(requireContext(),R.color.teal_900)
//        generator_set_expected.color = ContextCompat.getColor(requireContext(),R.color.teal_200)
//
//        val dataSet :ArrayList<IBarDataSet> = ArrayList()
//        dataSet.add(generator_set_present)
//        dataSet.add(generator_set_expected)
//
//        val generator_data = BarData(dataSet)
//        generator_data.barWidth = 0.3f //막대 너비 설정
//        userChart.run {
//            this.data = generator_data //차트의 데이터를 data로 설정해줌.
//            setFitBars(true)
//            invalidate()
//        }
//    }
//
//    fun usageGraph(){
//        val usage_entries_present = ArrayList<BarEntry>()
//        usage_entries_present.add(BarEntry(1.2f,20.0f))
//        usage_entries_present.add(BarEntry(2.2f,40.0f))
//        usage_entries_present.add(BarEntry(3.2f,30.0f))
//
//        val usage_entries_expected = ArrayList<BarEntry>()
//        usage_entries_expected.add(BarEntry(4.2f,40.0f))
//        usage_entries_expected.add(BarEntry(5.2f,70.0f))
//        usage_entries_expected.add(BarEntry(6.2f,20.0f))
//        usage_entries_expected.add(BarEntry(7.2f,40.0f))
//
//        usageChart.run {
//            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
//            setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 지정
//            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
//            setDrawBarShadow(false) //그래프의 그림자
//            setDrawGridBackground(false)//격자구조 넣을건지
//            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
//                axisMaximum = 101f //100 위치에 선을 그리기 위해 101f로 맥시멈값 설정
//                axisMinimum = 0f // 최소값 0
//                granularity = 50f // 50 단위마다 선을 그리려고 설정.
//                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
//                setDrawGridLines(false) //격자 라인 활용
//                setDrawAxisLine(false) // 축 그리기 설정
//                axisLineColor = ContextCompat.getColor(context,R.color.white) // 축 색깔 설정
//                gridColor = ContextCompat.getColor(context,R.color.white) // 축 아닌 격자 색깔 설정
//                textColor = ContextCompat.getColor(context,R.color.white) // 라벨 텍스트 컬러 설정
//                textSize = 13f //라벨 텍스트 크기
//            }
//            xAxis.run {
//                position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다.
//                granularity = 1f // 1 단위만큼 간격 두기
//                setDrawAxisLine(true) // 축 그림
//                setDrawGridLines(false) // 격자
//                textColor = ContextCompat.getColor(context,R.color.white) //라벨 색상
//                textSize = 12f // 텍스트 크기
//                valueFormatter = XAxisFormatter() // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
//            }
//            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
//            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
//            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
//
//
//            legend.isEnabled = true
//            axisLeft.textColor = Color.WHITE
//            xAxis.textColor = Color.WHITE
//            legend.textColor = Color.WHITE
//            legend.textSize = 10f
//            description.textColor = Color.WHITE
//        }
//
//
//        var usage_set_present = BarDataSet(usage_entries_present,"사용량") // 데이터셋 초기화
//        var usage_set_expected = BarDataSet(usage_entries_expected, "예상 사용량")
//
//        usage_set_present.color = ContextCompat.getColor(requireContext(),R.color.teal_900)
//        usage_set_expected.color = ContextCompat.getColor(requireContext(),R.color.teal_200)
//
//        val dataSet :ArrayList<IBarDataSet> = ArrayList()
//        dataSet.add(usage_set_present)
//        dataSet.add(usage_set_expected)
//
//        val usage_data = BarData(dataSet)
//        usage_data.barWidth = 0.3f //막대 너비 설정
//        usageChart.run {
//            this.data = usage_data //차트의 데이터를 data로 설정해줌.
//            setFitBars(true)
//            invalidate()
//        }
//    }
//
//    inner class XAxisFormatter : ValueFormatter() {
//        private val days = arrayOf("13:00","14:00","15:00","16:00","17:00","18:00","19:00")
//        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//            return days.getOrNull(value.toInt()-1) ?: value.toString()
//        }
//    }
//}