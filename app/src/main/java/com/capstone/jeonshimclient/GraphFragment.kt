package com.capstone.jeonshimclient

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_dr.*

class GraphFragment : Fragment() {
    override fun onAttach(context: Context) {
        super.onAttach(context)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity = context as Activity
        return inflater.inflate(R.layout.fragment_graph, container, false)
    }
}