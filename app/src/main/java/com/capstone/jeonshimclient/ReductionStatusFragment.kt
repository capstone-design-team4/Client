package com.capstone.jeonshimclient

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_calendar.view.*
import kotlinx.android.synthetic.main.fragment_reductionstatus.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList

class ReductionStatusFragment : Fragment() {
    private val api = APIS.create()
    private val itemList = arrayListOf<Date>()
    var nullBody = false

    @RequiresApi(Build.VERSION_CODES.O)
    var now: LocalDate = LocalDate.now()

    @RequiresApi(Build.VERSION_CODES.O)
    val listAdapter = CalendarAdapter(itemList)
    private lateinit var calendarList: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager

    @RequiresApi(Build.VERSION_CODES.O)
    var selectedDay: Int = LocalDate.now().dayOfMonth

    @RequiresApi(Build.VERSION_CODES.O)
    var selectedMonth: Int = LocalDate.now().monthValue

    @RequiresApi(Build.VERSION_CODES.O)
    private var selectView: View? = null
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

        // recyclerView orientation (?????? ?????? ????????? ??????)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        calendarList.layoutManager = mLayoutManager

        setListView()

        calendarList.smoothScrollToPosition(LocalDate.now().dayOfMonth - 1)

        return view
    }

    @SuppressLint("SetTextI18n", "ResourceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getRequestInfoDay(selectedMonth, selectedDay)
        drRequestInfoDay(LocalDate.now().dayOfMonth)

        selectedDay = LocalDate.now().dayOfMonth
        selectedMonth = LocalDate.now().monthValue
        // ?????? ??????????????? ??? ?????? ????????? ??????????????? ???
        monthAndDay.text = "${
            LocalDate.now().month.getDisplayName(
                TextStyle.SHORT,
                Locale.KOREA
            )
        } ${LocalDate.now().dayOfMonth}???"

        if (drRequestInfo != null) {
            val starttime = LocalDateTime.parse(drRequestInfo?.requestStartTime)
            val endtime = LocalDateTime.parse(drRequestInfo?.requestEndTime)

            if (LocalDateTime.now() <= endtime) {
                val startTime_string =
                    starttime.hour.toString() + ":" + starttime.minute.toString()
                val endTime_string =
                    endtime.hour.toString() + ":" + endtime.minute.toString()
                drTime.text = "$startTime_string ~ $endTime_string"
                drkWh.text = drRequestInfo!!.amount.toString() + "Wh"
            }
        }

        // ????????? ?????? ????????? ???????????? ??? ??????
        listAdapter.setOnItemClickListener(object : CalendarAdapter.OnItemClickListener {
            @SuppressLint("ResourceAsColor")
            override fun onItemClick(v: View, data: Date, pos: Int) {
                val calendarView: View = v.findViewById(R.id.calendar_cell)
                val dateTv: TextView = v.findViewById(R.id.date_cell)
                val dayTv: TextView = v.findViewById(R.id.day_cell)
                selectedDay = pos + 1

                if (listAdapter.selectedView != null && listAdapter.selectedView != v) {
                    Log.d("log", "if??? ??????")
                    listAdapter.selectedView!!.setBackgroundResource(0)
                    val temp1: TextView = listAdapter.selectedView!!.findViewById(R.id.date_cell)
                    val temp2: TextView = listAdapter.selectedView!!.findViewById(R.id.day_cell)
                    temp1.setTextColor(R.color.white)
                    temp2.setTextColor(R.color.white)
                }
                calendarView.setBackgroundResource(R.drawable.border_layout_bs1)
                dateTv.setTextColor(R.color.blue_back)
                dayTv.setTextColor(R.color.blue_back)
                listAdapter.selectedView = v
                monthAndDay.text = "${selectedMonth}??? ${data.date}???"
                getRequestInfoDay(selectedMonth, selectedDay)
            }
        })

        // ?????? ????????? ??? left, right ??? ????????? ??? month ??? ????????????????????? ???
        val left: ImageView = view.findViewById(R.id.left_reductionstatus)
        left.setOnClickListener {
            if (listAdapter.selectedView != null)
                listAdapter.selectedView!!.setBackgroundResource(0)
            if (selectedMonth > 1) {
                selectedMonth -= 1
                now = now.withMonth(selectedMonth)
                selectedDay = 1

                monthAndDay.text = "${selectedMonth}??? ${selectedDay}???"
                setListView()
                getRequestInfoDay(selectedMonth, selectedDay)
            }
        }
        val right: ImageView = view.findViewById(R.id.right_reductionstatus)
        right.setOnClickListener {
            if (listAdapter.selectedView != null)
                listAdapter.selectedView!!.setBackgroundResource(0)
            if (selectedMonth < 12 && selectedMonth < LocalDate.now().monthValue) {
                selectedMonth += 1
                now = now.withMonth(selectedMonth)
                selectedDay = 1

                monthAndDay.text = "${selectedMonth}??? ${selectedDay}???"
                setListView()
                getRequestInfoDay(selectedMonth, selectedDay)
            }
        }
        val graphListDialog = GraphListDialog(requireContext())

        showgraph1.setOnClickListener {
            if (!nullBody && drRequestInfo!!.user1Flag != false)
                graphListDialog.graphListDig(requireContext(), 1, selectedDay, drRequestInfo)
        }
        showgraph2.setOnClickListener {
            if (!nullBody && drRequestInfo?.user2Flag != false)
                graphListDialog.graphListDig(requireContext(), 2, selectedDay, drRequestInfo)
        }
        showgraph3.setOnClickListener {
            if (!nullBody && drRequestInfo?.user3Flag != false)
                graphListDialog.graphListDig(requireContext(), 3, selectedDay, drRequestInfo)
        }

    }

    // list(??????, ??????)??? ?????????, adapter??? ???????????? ?????????
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListView() {
        // ?????? ?????? ????????? ??????
        Log.d("log", now.monthValue.toString())
        val lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth())
        lastDayOfMonth.format(DateTimeFormatter.ofPattern("dd"))
        itemList.clear()
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

    // ?????? ????????? ???????????? ??? ??? ????????? ???????????? drRequest??? id?????? ????????? ???
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
                val body = response.body()
                if (body != null && body.toString() != "[]") {
                    drRequestInfo = body
                    changeText()
                    nullBody = false
                }
                else{
                    nullBody = true
                    changeText()
                }
            }

            override fun onFailure(call: Call<DrRequestInfo>, t: Throwable) {
                // ??????
                Log.d("log", t.message.toString())
                Log.d("log", "fail")
                nullBody = true
                // changeText()
            }
        })
    }

    fun changeText() {
        if (nullBody) {
            showgraph1.text = "???????????? ??????"
            showgraph2.text = "???????????? ??????"
            showgraph3.text = "???????????? ??????"
        }
        else if (showgraph1 != null) {
            if (drRequestInfo?.user1Flag == true)
                showgraph1.text = "????????? ?????? >"
            else
                showgraph1.text = "???????????? ??????"

            if (drRequestInfo?.user2Flag == true)
                showgraph2.text = "????????? ?????? >"
            else
                showgraph2.text = "???????????? ??????"

            if (drRequestInfo?.user3Flag == true)
                showgraph3.text = "????????? ?????? >"
            else
                showgraph3.text = "???????????? ??????"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun drRequestInfoDay(day: Int) {
        val date = LocalDateTime.of(LocalDateTime.now().year, LocalDateTime.now().month, day, 0, 0)
        Log.d("log", "date: $date")

        api.getDrRequestInfoDay(date.toString()).enqueue(object : Callback<DrRequestInfo> {
            override fun onResponse(call: Call<DrRequestInfo>, response: Response<DrRequestInfo>) {
                val body = response.body()
                Log.d("abc_ :", "${body}")
                if (body != null && body.toString() != "[]") {
                    drRequestInfo = DrRequestInfo(
                        body.requestId,
                        body.requestStartTime,
                        body.requestEndTime,
                        body.amount,
                        body.user1Flag,
                        body.user2Flag,
                        body.user3Flag,
                        body.decisionFlag
                    )
                }
            }

            override fun onFailure(call: Call<DrRequestInfo>, t: Throwable) {
                Log.d("log", t.message.toString())
                Log.d("log", "fail")
            }
        })
    }
}