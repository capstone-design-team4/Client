package com.capstone.jeonshimclient

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class UserRecyclerViewAdapter(private val context: Context) :
    RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder>() {

    var datas = mutableListOf<UserData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_recycler,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val txtName: TextView = itemView.findViewById(R.id.tv_user_name)
        private val txtUsage: TextView = itemView.findViewById(R.id.tv_user_usage)
        fun bind(item: UserData) {
            txtName.text = item.name
        }
    }


}