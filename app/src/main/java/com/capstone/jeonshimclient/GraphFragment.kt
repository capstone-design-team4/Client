package com.capstone.jeonshimclient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_graph.*

class GraphFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bt_start_graph1.setOnClickListener {
            val startgraph = ExpectedGeneratorGraphDialog(requireContext())
            startgraph.startDialog(requireContext())
        }
        bt_start_graph2.setOnClickListener {
            val startgraph = ExpectedUsageGraphDialog(requireContext())
            startgraph.startDialog(requireContext())
        }
    }
}