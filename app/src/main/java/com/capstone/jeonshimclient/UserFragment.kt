package com.capstone.jeonshimclient

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment : Fragment() {
    //    lateinit var usageAdapter: UsageAdapter
    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
//    val datas = mutableListOf<UsageData>()

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
//        initRecycler()
    }

    var userdialog_text1 = 0
    var userdialog_text2 = "발전 전력"
    var userdialog_text3 = 0F
    var userdialog_text4 = 0F

    fun loadSpinner(view: View) {val spinner: Spinner = view.findViewById(R.id.spinner_fragment_user)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            view.context,
            R.array.spinner_array,
            R.layout.spinner_list
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_list)
            spinner.adapter = adapter
        }

        spinner.setSelection(0)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        userdialog_text1 = 1111
                        userdialog_text2 = "발전 전력"
                        userdialog_text3 = 1111F
                        userdialog_text4 = 1111F

                    }
                    1 -> {
                        userdialog_text1 = 2222
                        userdialog_text2 = "일반 전력"
                        userdialog_text3 = 2222F
                        userdialog_text4 = 2222F
                    }
                    2 -> {
                        userdialog_text1 = 3333
                        userdialog_text2 = "발전 전력"
                        userdialog_text3 = 3333F
                        userdialog_text4 = 3333F
                    }
                }
                spinner.onItemSelectedListener = this
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        bt_fragment_user.setOnClickListener{
            val intent = Intent(requireContext(), UserDialog::class.java)

            intent.putExtra("item1", userdialog_text1)
            intent.putExtra("item2", userdialog_text2)
            intent.putExtra("item3", userdialog_text3)
            intent.putExtra("item4", userdialog_text4)

            val userDialog = UserDialog(requireContext(), intent)
            userDialog.userDig(requireContext())
        }
    }


}