package com.capstone.jeonshimclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_dr.*
import kotlinx.android.synthetic.main.fragment_drlist.*

class DRlistFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val graphlistdialog = GraphListDialog(requireContext())

        showgraph1.setOnClickListener {
            graphlistdialog.graphlistDig(requireContext(), "세대1", 1)
        }
        showgraph2.setOnClickListener {
            graphlistdialog.graphlistDig(requireContext(), "세대2", 2)
        }
        showgraph3.setOnClickListener {
            graphlistdialog.graphlistDig(requireContext(), "세대3", 3)
        }

    }
}