package com.capstone.jeonshimclient

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.capstone.jeonshimclient.databinding.FragmentDrBinding
import kotlinx.android.synthetic.main.fragment_dr.*

class DRFragment : Fragment() {
    private lateinit var binding: FragmentDrBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog = SettingDialog(requireContext())
        button_DR설정.setOnClickListener {
            dialog.setDig()
        }
        dialog.setOnClickedListener(object : SettingDialog.ButtonClickListener{
            override fun onClicked(drname: String){
                print_dr_name.text = Editable.Factory.getInstance().newEditable(drname)
            }
        })
    }
}