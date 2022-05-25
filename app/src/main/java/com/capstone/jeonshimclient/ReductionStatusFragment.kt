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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_drlist.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.*

class ReductionStatusFragment : Fragment() {
    val api = APIS.create()
    val itemList = arrayListOf<Date>()
    @RequiresApi(Build.VERSION_CODES.O)
    val listAdapter = CalendarAdapter(itemList)
    lateinit var calendarList: RecyclerView
    lateinit var mLayoutManager: LinearLayoutManager
    @RequiresApi(Build.VERSION_CODES.O)
    var selectedDay: Int = LocalDate.now().dayOfMonth - 1
    @RequiresApi(Build.VERSION_CODES.O)
    var selectedView: View? = null
    lateinit var monthAndDay: TextView
    var requestInfoDay: DrRequestInfo? = null


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
        Log.d("log", "OnViewCreated")
        Log.d("log", "listAdapter: " + listAdapter.toString())
        //Log.d("log","calendarList: " + calendarList.)


        // 뷰가 시작되었을 때 현재 날짜를 표시해줘야 함
        monthAndDay.text = "${LocalDate.now().month.getDisplayName(TextStyle.SHORT, Locale.KOREA)} ${LocalDate.now().dayOfMonth}일"


        // 캘린더 어떤 날짜를 클릭했을 때 발생
        listAdapter.setOnItemClickListener(object : CalendarAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: Date, pos: Int) {
                val calendarView: View = v.findViewById(R.id.calendar_cell)
                val dateTv: TextView = v.findViewById(R.id.date_cell)
                val dayTv: TextView = v.findViewById(R.id.day_cell)
                selectedDay = pos+1

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
                monthAndDay.text = "${LocalDate.now().month.getDisplayName(TextStyle.SHORT, Locale.KOREA)} ${data.date}일"
                getRequestInfoDay(selectedDay)
            }
        })

        val graphlistdialog = GraphListDialog(requireContext())

        showgraph1.setOnClickListener {
            graphlistdialog.graphlistDig(requireContext(), "세대1", selectedDay)
        }
        showgraph2.setOnClickListener {
            graphlistdialog.graphlistDig(requireContext(), "세대2", selectedDay)
        }
        showgraph3.setOnClickListener {
            graphlistdialog.graphlistDig(requireContext(), "세대3", selectedDay)
        }
    }

    // list(날짜, 요일)를 만들고, adapter를 등록하는 메소드
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListView() {
        // 현재 달의 마지막 날짜
        val lastDayOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
        lastDayOfMonth.format(DateTimeFormatter.ofPattern("dd"))

        for (i: Int in 1..lastDayOfMonth.dayOfMonth) {
            val date = LocalDate.of(LocalDate.now().year, LocalDate.now().month, i)
            val dayOfWeek: DayOfWeek = date.dayOfWeek
            // dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)
            // Log.d("log", dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREA))
            // Log.d("log", dayOfWeek.toString().substring(0, 3))
            itemList.add(
                Date(
                    dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREA),
                    i.toString()
                )
            )
        }
        calendarList.adapter = listAdapter
    }

<<<<<<< HEAD
    // 어떤 날짜를 선택했을 때 그 날짜에 해당하는 drRequest의 id값을 받아야 함
    @RequiresApi(Build.VERSION_CODES.O)
    fun getRequestInfoDay(day: Int){
        var date = LocalDateTime.of(LocalDate.now().year, LocalDate.now().month, day, 0, 0,0)
        Log.d("log", "date: $date")
=======
    @RequiresApi(Build.VERSION_CODES.O)
    fun initDrRequestInfo(day: String){
        CoroutineScope(Dispatchers.IO).launch {
            val call = api.getDrRequestInfoDay("${LocalDateTime.now().toString()}")
        }
>>>>>>> d1601e184ca5732f1d5cb53e634c467be18fc0e1
    }
}