package com.capstone.jeonshimclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_dr.*


class DRFragment : Fragment() {
    // APIS 만들어주기 이 녀석을 이용해서 GET, POST 등을 사용
    val api = APIS.create()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bt_refer1.setOnClickListener{
            val mainActivity = (activity as MainActivity)
            mainActivity.changeFragment(1)
        }


        bt_refer2.setOnClickListener{
            val mainActivity = (activity as MainActivity)
            mainActivity.changeFragment(2)
        }

    }
}