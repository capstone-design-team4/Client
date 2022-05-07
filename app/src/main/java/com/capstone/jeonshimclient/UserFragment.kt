package com.capstone.jeonshimclient

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_dr.*

class UserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSpinner(view)
    }

    // spinner 설정 메소드
    fun loadSpinner(view: View){
        val items = arrayOf("전체", "발전", "일반")
        val spinner: Spinner = view.findViewById(R.id.spinner1)
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            view.context,
            android.R.layout.simple_spinner_item,
            items
        )

        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner.adapter = adapter
        spinner.setSelection(1)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0 -> Toast.makeText(view?.context, "${items[0]} 항목 선택", Toast.LENGTH_SHORT).show()
                    1 -> Toast.makeText(view?.context, "1번 항목 선택", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }
}