package com.capstone.jeonshimclient

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_dr.*
import kotlinx.android.synthetic.main.fragment_drlist.*

class DRListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drlist, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val graphListDialog = GraphListDialog(requireContext())
//
//        showgraph1.setOnClickListener {
//            graphListDialog.graphListDig(requireContext(), 1, 1)
//        }
//        showgraph2.setOnClickListener {
//            graphListDialog.graphListDig(requireContext(), 2, 2)
//        }
//        showgraph3.setOnClickListener {
//            graphListDialog.graphListDig(requireContext(), 3, 3)
//        }

    }
}