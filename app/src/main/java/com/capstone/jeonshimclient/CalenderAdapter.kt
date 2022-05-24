package com.capstone.jeonshimclient

import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapter(private val dataSet: ArrayList<Date>) :
    RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    var drawable: Drawable? = null
    var selectedIndex: Int? = null
    var selectedView: View? = null

    private lateinit var itemClickListener: AdapterView.OnItemClickListener

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTv: TextView = view.findViewById(R.id.date_cell)
        val dayTv: TextView = view.findViewById(R.id.day_cell)
        val thisView: View = view

        fun bind(item: Date){
            val pos = adapterPosition
            if(pos!=RecyclerView.NO_POSITION){
                itemView.setOnClickListener{
                    listener?.onItemClick(itemView, item, pos)
                    selectedIndex = pos
                }
            }
//            Log.d("log", "bind: 선택한 Date = $item")
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.calendar_cell, viewGroup, false)

        //drawable = ContextCompat.getDrawable(view.context, R.drawable.border_layout_bs1)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dateTv.text = dataSet[position].date
        holder.dayTv.text = dataSet[position].day

        holder.bind(dataSet[position])
        if(selectedIndex != null && selectedIndex == position){
            selectedView = holder.thisView
            holder.thisView.setBackgroundResource(R.drawable.border_layout_bs1)
            holder.dateTv.setTextColor(R.color.blue_back)
            holder.dayTv.setTextColor(R.color.blue_back)
            selectedIndex = position
        }
        else{
            holder.dateTv.setTextColor(R.color.white)
            holder.dayTv.setTextColor(R.color.white)
            holder.thisView.setBackgroundResource(0)
        }
    }

    override fun getItemCount() = dataSet.size

    interface OnItemClickListener {
        fun onItemClick(v: View, data: Date, pos: Int)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}