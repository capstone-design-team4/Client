package com.capstone.jeonshimclient

import android.content.Context
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
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_dr.*
import kotlinx.android.synthetic.main.fragment_usage.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.rv_profile

class UserFragment : Fragment() {
    lateinit var userRecyclerViewAdapter: UserRecyclerViewAdapter
    val datas = mutableListOf<UserData>()

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
        initRecycler(view)
    }

    // spinner 설정 메소드
    fun loadSpinner(view: View){
        //val items = arrayOf("전체", "발전", "일반")
        val spinner: Spinner = view.findViewById(R.id.spinner1)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            view.context,
            R.array.spinner_array,
            R.layout.spinner_list
        ).also { adapter->
            adapter.setDropDownViewResource(R.layout.spinner_list)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    // 일단 예시로 채워둠
                    0 -> Toast.makeText(view?.context, "항목 선택", Toast.LENGTH_SHORT).show()
                    1 -> Toast.makeText(view?.context, "1번 항목 선택", Toast.LENGTH_SHORT).show()
                }
                spinner.onItemSelectedListener = this
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun initRecycler(view: View){
        userRecyclerViewAdapter = UserRecyclerViewAdapter(view.context)
        rv_profile.adapter = userRecyclerViewAdapter
        rv_profile.addItemDecoration(VerticalItemDecorator(30))
        rv_profile.addItemDecoration(HorizontalItemDecorator(20))

        datas.apply {
            add(UserData(name = "세대1", supplied = 3.8f, update = "20220509" ))
            add(UserData(name = "세대2", supplied = 3.8f, update = "20220509" ))
            userRecyclerViewAdapter.datas = datas
            userRecyclerViewAdapter.notifyDataSetChanged()
        }
    }
}
