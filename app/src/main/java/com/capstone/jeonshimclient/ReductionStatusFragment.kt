package com.capstone.jeonshimclient

import android.annotation.SuppressLint
import android.content.res.Resources
import android.content.res.Resources.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_calendar.*
import kotlinx.android.synthetic.main.fragment_reductionstatus.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.*

class ReductionStatusFragment : Fragment() {
    private val api = APIS.create()
    private val itemList = arrayListOf<Date>()
    @RequiresApi(Build.VERSION_CODES.O)
    var now: LocalDate = LocalDate.now()
    @RequiresApi(Build.VERSION_CODES.O)
    val listAdapter = CalendarAdapter(itemList)
    lateinit var calendarList: RecyclerView
    lateinit var mLayoutManager: LinearLayoutManager
    @RequiresApi(Build.VERSION_CODES.O)
    var selectedDay: Int = LocalDate.now().dayOfMonth
    @RequiresApi(Build.VERSION_CODES.O)
    var selectedMonth: Int = LocalDate.now().monthValue
    @RequiresApi(Build.VERSION_CODES.O)
    var selectedView: View? = null
    lateinit var monthAndDay: TextView
    var drRequestInfo: DrRequestInfo? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reductionstatus, container, false)
        calendarList = view.findViewById(R.id.calendar_list)
        mLayoutManager = LinearLayoutManager(view.context)
        monthAndDay = view.findViewById(R.id.monthAndDay)

        // recyclerView orientation (가로 방향 스크롤 설정)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        calendarList.layoutManager = mLayoutManager

        setListView()

        calendarList.smoothScrollToPosition(LocalDate.now().dayOfMonth - 1)

        return view
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getRequestInfoDay(selectedMonth, selectedDay)

        // 뷰가 시작되었을 때 현재 날짜를 표시해줘야 함
        monthAndDay.text = "${
            LocalDate.now().month.getDisplayName(
                TextStyle.SHORT,
                Locale.KOREA
            )
        } ${LocalDate.now().dayOfMonth}일"

        // 캘린더 어떤 날짜를 클릭했을 때 발생
        listAdapter.setOnItemClickListener(object : CalendarAdapter.OnItemClickListener {
            @SuppressLint("ResourceAsColor")
            override fun onItemClick(v: View, data: Date, pos: Int) {
                val calendarView: View = v.findViewById(R.id.calendar_cell)
                val dateTv: TextView = v.findViewById(R.id.date_cell)
                val dayTv: TextView = v.findViewById(R.id.day_cell)
                selectedDay = pos + 1

                if (selectedView != null && selectedView != v) {
                    Log.d("log", "if문 실행")
                    selectedView?.setBackgroundResource(0)
                    val temp1: TextView = selectedView!!.findViewById(R.id.date_cell)
                    val temp2: TextView = selectedView!!.findViewById(R.id.day_cell)
                    temp1.setTextColor(R.color.white)
                    temp2.setTextColor(R.color.white)
                }
                calendarView.setBackgroundResource(R.drawable.border_layout_bs1)
                dateTv.setTextColor(R.color.blue_back)
                dayTv.setTextColor(R.color.blue_back)
                selectedView = v
                monthAndDay.text = "${selectedMonth}월 ${data.date}일"
                getRequestInfoDay(selectedMonth, selectedDay)
            }
        })

        // 뷰가 생성될 때 left, right 를 눌렀을 때 month 를 변화시켜주어야 함
        val left: ImageView = view.findViewById(R.id.left_reductionstatus)
        left.setOnClickListener {
            if (selectedMonth > 1) {
                selectedMonth -= 1
                now.withMonth(selectedMonth)
                monthAndDay.text = "${selectedMonth}월 ${selectedDay}일"
                setListView()
                getRequestInfoDay(selectedMonth, selectedDay)
            }
        }
        val right: ImageView = view.findViewById(R.id.right_reductionstatus)
        right.setOnClickListener {
            if (selectedMonth < 12) {
                selectedMonth += 1
                now.withMonth(selectedMonth)
                monthAndDay.text = "${selectedMonth}월 ${selectedDay}일"
                setListView()
                getRequestInfoDay(selectedMonth, selectedDay)
            }
        }
        val graphListDialog = GraphListDialog(requireContext())

        showgraph1.setOnClickListener {
//            if (drRequestInfo?.user1Flag != false)
            graphListDialog.graphListDig(requireContext(), 1, selectedDay, drRequestInfo)
        }
        showgraph2.setOnClickListener {
//            if (drRequestInfo?.user2Flag != false)
            graphListDialog.graphListDig(requireContext(), 2, selectedDay, drRequestInfo)
        }
        showgraph3.setOnClickListener {
//            if (drRequestInfo?.user3Flag != false)
            graphListDialog.graphListDig(requireContext(), 3, selectedDay, drRequestInfo)
        }
    }

    // list(날짜, 요일)를 만들고, adapter를 등록하는 메소드
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListView() {
        // 현재 달의 마지막 날짜
        val lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth())
        lastDayOfMonth.format(DateTimeFormatter.ofPattern("dd"))

        for (i: Int in 1..lastDayOfMonth.dayOfMonth) {
            val date = LocalDate.of(now.year, now.month, i)
            val dayOfWeek: DayOfWeek = date.dayOfWeek

            itemList.add(
                Date(
                    dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREA),
                    i.toString()
                )
            )
        }
        calendarList.adapter = listAdapter
    }

    // 어떤 날짜를 선택했을 때 그 날짜에 해당하는 drRequest의 id값을 받아야 함
    @RequiresApi(Build.VERSION_CODES.O)
    fun getRequestInfoDay(month: Int, day: Int) {
        val date = LocalDateTime.of(now.year, month, day, 0, 0)
        Log.d("log", "date: $date")

        api.getDrRequestInfoDay(date.toString()).enqueue(object : Callback<DrRequestInfo> {
            override fun onResponse(
                call: Call<DrRequestInfo>,
                response: Response<DrRequestInfo>
            ) {
                Log.d("log", response.toString())
                Log.d("log", response.body().toString())
                var body = response.body()
                if (body != null && body.toString() != "[]") {
                    drRequestInfo = body
                    changeText()
                }
            }

            override fun onFailure(call: Call<DrRequestInfo>, t: Throwable) {
                // 실패
                Log.d("log", t.message.toString())
                Log.d("log", "fail")
            }
        })
    }

    fun changeText() {
        if (drRequestInfo?.user1Flag == true)
            showgraph1.text = "사용량 보기 >"
        else
            showgraph1.text = "참여하지 않음"

        if (drRequestInfo?.user2Flag == true)
            showgraph2.text = "사용량 보기 >"
        else
            showgraph2.text = "참여하지 않음"

        if (drRequestInfo?.user3Flag == true)
            showgraph3.text = "사용량 보기 >"
        else
            showgraph3.text = "참여하지 않음"
    }
}