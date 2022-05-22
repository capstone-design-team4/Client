package com.capstone.jeonshimclient

import android.content.Context
import android.os.Bundle
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

    fun loadSpinner(view: View) {
        val items = arrayOf("전체", "발전", "일반")



        val spinner: Spinner = view.findViewById(R.id.spinner_fragment_user)
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
            val userDialog = UserDialog(requireContext(),userdialog_text1,userdialog_text2,userdialog_text3,userdialog_text4)
            userDialog.userDig(requireContext())
        }
        //        usageAdapter = UsageAdapter(requireContext())
        //        rv_profile.adapter = usageAdapter
        //        rv_profile.addItemDecoration(VerticalItemDecorator(30))
        //        rv_profile.addItemDecoration(HorizontalItemDecorator(20))
    }
}
//    private fun initRecycler() {
//        usageAdapter = UsageAdapter(requireContext())
//        rv_profile.adapter = usageAdapter
//        rv_profile.addItemDecoration(VerticalItemDecorator(30))
//        rv_profile.addItemDecoration(HorizontalItemDecorator(20))
//
//        datas.apply {
//            add(UsageData(name = "세대1", usageDay = "일간 전력량 : 1234 kWh", fee = "사용 요금 : 1234 원", switch = true))
//            add(UsageData(name = "세대2", usageDay = "일간 전력량 : 5678 kWh", fee = "사용 요금 : 5678 원", switch = false))
//            add(UsageData(name = "세대3", usageDay = "일간 전력량 : 9999 kWh", fee = "사용 요금 : 9999 원", switch = false))
//            add(UsageData(name = "세대4", usageDay = "일간 전력량 : 0 kWh", fee = "사용 요금 : 0 원", switch = true))
//
//            usageAdapter.datas = datas
//            usageAdapter.notifyDataSetChanged()
//
//        }
//    }
//}
//
//class VerticalItemDecorator(private val divHeight : Int) : RecyclerView.ItemDecoration() {
//
//    @Override
//    override fun getItemOffsets(outRect: Rect, view: View, parent : RecyclerView, state : RecyclerView.State) {
//        super.getItemOffsets(outRect, view, parent, state)
//        outRect.top = divHeight
//        outRect.bottom = divHeight
//    }
//}
//
//class HorizontalItemDecorator(private val divHeight : Int) : RecyclerView.ItemDecoration() {
//
//    @Override
//    override fun getItemOffsets(outRect: Rect, view: View, parent : RecyclerView, state : RecyclerView.State) {
//        super.getItemOffsets(outRect, view, parent, state)
//        outRect.left = divHeight
//        outRect.right = divHeight
//    }
//}