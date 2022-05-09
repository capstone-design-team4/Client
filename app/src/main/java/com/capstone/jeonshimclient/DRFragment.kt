package com.capstone.jeonshimclient

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_dr.*

class DRFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val setdialog = SettingDialog(requireContext())
        val storagedialog = StorageSettingDialog(requireContext())

        button_set_minimum.setOnClickListener {
            storagedialog.setDig(requireContext())
        }

        button_set_DR.setOnClickListener {
            setdialog.setDig(requireContext())
        }
        setdialog.setOnClickedListener(object : SettingDialog.ButtonClickListener{
            override fun onClicked(drname: String, reductions: String, time1: String, time2: String){
                print_drname.text = Editable.Factory.getInstance().newEditable("$drname")
                print_reductions.text = Editable.Factory.getInstance().newEditable("${reductions}kWh")
                print_time.text = Editable.Factory.getInstance().newEditable("$time1 ~ $time2")
            }
        }
        )
    }
}