package com.capstone.jeonshimclient

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
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

        // settingDialog는 지울 계획이지만, 코드 참고용으로 일단 두겠음.
        val setdialog = NoticeDialog(requireContext())
        val storagedialog = StorageSettingDialog(requireContext())
        val storageCondition:Int = 70       // 배터리 저장량 변수
        val generationCondition:Int = 100   // 발전 공급 현황 퍼센트

        // 해당 시간이 되면 이 다이얼로그가 실행 됨

        title1.setOnClickListener {
            setdialog.setDig(requireContext())
        }

        button_set_minimum.setOnClickListener {
            storagedialog.setDig(requireContext())
        }

        setdialog.setOnClickedListener(object : NoticeDialog.ButtonClickListener{
            override fun onClicked(drtime: String, drkwh: String){
                print_time.text = Editable.Factory.getInstance().newEditable("$drtime")
                print_reductions.text = Editable.Factory.getInstance().newEditable("${drkwh}kWh")
            }
        }
        )
        barBattey.max = 100
        barBattey.progress = storageCondition
        text_Battery.text = "저장량 : ${storageCondition}%"
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
        barDRcondition.progress = generationCondition
        text_generation_condition.text = "발전 공급 현황 : ${generationCondition}%"
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
class RunnableTest : Runnable{
    override fun run() {
        var i = 0
        while(i<10)
            i += 1
    }
}