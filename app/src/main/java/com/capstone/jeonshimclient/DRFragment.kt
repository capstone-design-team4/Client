package com.capstone.jeonshimclient

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_dr.*
import kotlinx.android.synthetic.main.fragment_dr.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        // settingDialog는 지울 계획이지만, 코드 참고용으로 일단 두겠음.
        val setdialog = SettingDialog(requireContext())
        val storagedialog = StorageSettingDialog(requireContext())
        val storageCondition:Int = 70       // 배터리 저장량 변수
        val generationCondition:Int = 100   // 발전 공급 현황 퍼센트
<<<<<<< HEAD

=======
>>>>>>> 47ea7c571e04c64e0448100acfa4241f31bd2449

        button_set_minimum.setOnClickListener {
            storagedialog.setDig(requireContext())
        }

        button_set_DR.setOnClickListener {
            setdialog.setDig(requireContext())
        }
        setdialog.setOnClickedListener(object : SettingDialog.ButtonClickListener{
            override fun onClicked(drname: String){
                print_drname.text = Editable.Factory.getInstance().newEditable("$drname")
                print_time.text = Editable.Factory.getInstance().newEditable("13:00 ~ 14:00")
                print_reductions.text = Editable.Factory.getInstance().newEditable("123 kWh")
            }
        }
        )
        barBattey.max = 100
<<<<<<< HEAD
<<<<<<< HEAD
        barBattey.setProgress(storageCondition)
        text_Battery.text = "저장량 : ${storageCondition}%"
=======
        barBattey.progress = 70
>>>>>>> afafd71fa3d63dc573addbe02aff54632521ab93
=======
        barBattey.setProgress(storageCondition)
        text_Battery.text = "저장량 : ${storageCondition}%"
>>>>>>> 47ea7c571e04c64e0448100acfa4241f31bd2449
        barBattey.thumb = ColorDrawable(Color.TRANSPARENT)
        barBattey.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekBar?.progress = storageCondition
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        barDRcondition.max = 100
<<<<<<< HEAD
<<<<<<< HEAD
        barDRcondition.setProgress(generationCondition)
        text_generation_condition.text = "발전 공급 현황 : ${generationCondition}%"
=======
        barDRcondition.progress = 100
>>>>>>> afafd71fa3d63dc573addbe02aff54632521ab93
=======
        barDRcondition.setProgress(generationCondition)
        text_generation_condition.text = "발전 공급 현황 : ${generationCondition}%"
>>>>>>> 47ea7c571e04c64e0448100acfa4241f31bd2449
        barDRcondition.thumb = ColorDrawable(Color.TRANSPARENT)
        barDRcondition.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekBar?.progress = generationCondition
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }
}