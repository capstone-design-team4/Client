package com.capstone.jeonshimclient

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_usage.*

class UsageFragment : Fragment() {
    lateinit var usageAdapter: UsageAdapter
    val datas = mutableListOf<UsageData>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_usage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }
    private fun initRecycler() {
        usageAdapter = UsageAdapter(requireContext())
        rv_profile.adapter = usageAdapter
        rv_profile.addItemDecoration(VerticalItemDecorator(30))
        rv_profile.addItemDecoration(HorizontalItemDecorator(20))

        datas.apply {
            add(UsageData(name = "세대1", usageDay = "일간 전력량 : 1234 kWh", fee = "사용 요금 : 1234 원"))
            add(UsageData(name = "세대2", usageDay = "일간 전력량 : 5678 kWh", fee = "사용 요금 : 5678 원"))
            add(UsageData(name = "세대3", usageDay = "일간 전력량 : 9999 kWh", fee = "사용 요금 : 9999 원"))
            add(UsageData(name = "세대4", usageDay = "일간 전력량 : 0 kWh", fee = "사용 요금 : 0 원"))

            usageAdapter.datas = datas
            usageAdapter.notifyDataSetChanged()

        }
    }
}
class VerticalItemDecorator(private val divHeight : Int) : RecyclerView.ItemDecoration() {

    @Override
    override fun getItemOffsets(outRect: Rect, view: View, parent : RecyclerView, state : RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = divHeight
        outRect.bottom = divHeight
    }
}

class HorizontalItemDecorator(private val divHeight : Int) : RecyclerView.ItemDecoration() {

    @Override
    override fun getItemOffsets(outRect: Rect, view: View, parent : RecyclerView, state : RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = divHeight
        outRect.right = divHeight
    }
}