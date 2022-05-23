package com.capstone.jeonshimclient

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_drlist.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.*

class ReductionStatusFragment : Fragment() {
    val itemList = arrayListOf<Date>()
    val listAdapter = CalendarAdapter(itemList)
    lateinit var calendarList: RecyclerView
    lateinit var mLayoutManager: LinearLayoutManager
    var selectedPos: Int? = null
    var selectedView: View? = null
    lateinit var monthAndDay: TextView

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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("log", "OnViewCreated")
        Log.d("log", "listAdapter: " + listAdapter.toString())
        //Log.d("log","calendarList: " + calendarList.)

        listAdapter.setOnItemClickListener(object : CalendarAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: Date, pos: Int) {
                val calendarView: View = v.findViewById(R.id.calendar_cell)
                val dateTv: TextView = v.findViewById(R.id.date_cell)
                val dayTv: TextView = v.findViewById(R.id.day_cell)

                if (selectedView != null && selectedView != v) {
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
            }
        })

        val graphlistdialog = GraphListDialog(requireContext())

        showgraph1.setOnClickListener {
            graphlistdialog.graphlistDig(requireContext(), "세대1")
        }
        showgraph2.setOnClickListener {
            graphlistdialog.graphlistDig(requireContext(), "세대2")
        }
        showgraph3.setOnClickListener {
            graphlistdialog.graphlistDig(requireContext(), "세대3")
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
}