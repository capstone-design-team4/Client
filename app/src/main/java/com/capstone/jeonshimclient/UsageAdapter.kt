package com.capstone.jeonshimclient

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// 현재 사용 안함
//class UsageAdapter (private val context: Context) : RecyclerView.Adapter<UsageAdapter.ViewHolder>() {
//
//    var datas = mutableListOf<UsageData>()
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.usage_recycler_ex,parent,false)
//        return ViewHolder(view)
//    }
//
//    override fun getItemCount(): Int = datas.size
//
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(datas[position])
//        val graphDialog = GraphDialog(context)
//        val btShowGraph: Button = holder.itemView.findViewById(R.id.button_showGraph)
//        btShowGraph.setOnClickListener {
//            graphDialog.graphDig(context)
//        }
//    }
//
//    interface OnItemClickListener{
//        fun onClick(v:View,position: Int)
//    }
//    private lateinit var itemClickListener: OnItemClickListener
//
//    fun setItemClickListener(itemClickListener: OnItemClickListener){
//        this.itemClickListener = itemClickListener
//    }
//
//    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//
//        private val txtName: TextView = itemView.findViewById(R.id.pf_name)
//        private val txtUsageDay: TextView = itemView.findViewById(R.id.pf_usage_day)
//        private val txtFee: TextView = itemView.findViewById(R.id.pf_fee)
//        private val setSwitch: Switch = itemView.findViewById(R.id.pf_switch)
//
//        val graphDialog = GraphDialog(context)
//
//        fun bind(item: UsageData) {
//            txtName.text = item.name
//            txtUsageDay.text = item.usageDay
//            txtFee.text = item.fee
//            setSwitch.isChecked = item.switch
//
//            itemView.setOnClickListener{
//                graphDialog.graphDig(context)
//            }
//        }
//    }
//}