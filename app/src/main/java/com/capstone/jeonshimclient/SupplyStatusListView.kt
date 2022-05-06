package com.capstone.jeonshimclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_supply_status_list_view.*

class SupplyStatusListView : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supply_status_list_view)

        loadSpinner()
    }

    fun loadSpinner(){
        val items = arrayOf("전체", "발전", "일반")
        val textView: TextView = findViewById(R.id.textView)

        val spinner: Spinner = findViewById(R.id.spinner1)
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                textView.text = items[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                textView.text = "선택: "
            }
        }
    }
}
