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
    // APIS 만들어주기 이 녀석을 이용해서 GET, POST 등을 사용하는 것 같습니다.
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

        val setdialog = SettingDialog(requireContext())
        val storagedialog = StorageSettingDialog(requireContext())
        val storageCondition:Int = 70       // 배터리 저장량 변수
        val generationCondition:Int = 100   // 발전 공급 현황 퍼센트


        // DRFragment에 예시로 만든 버튼들입니다.
        exampleGetButton.setOnClickListener {
            // 사용 방법은 위에서 만든 api에서 정의된 함수들을 사용하며 enqueue 함수는 작성하지 않은 것 같은데
            // enqueue()를 사용해서 밑에 부분을 override 합니다.
            // response가 받은 결과입니다. response를 통해서 text를 받으면 그 text를 통해 DataModels에 있는 데이터 클래스를
            // 구성하는듯 합니다.
            // 더 자세한 내용은 retrofit2 사용법 이라는 검색어로 검색하시면 아실 수 있겠습니다.
            api.get_users().enqueue(object : Callback<HTTP_GET_Model> {
                override fun onResponse(
                    call: Call<HTTP_GET_Model>,
                    response: Response<HTTP_GET_Model>
                ){
                    Log.d("log", response.toString())
                    Log.d("log", response.body().toString())
                    if(!response.body().toString().isEmpty())
                        textExample.setText(response.body().toString())
                }

                override fun onFailure(call: Call<HTTP_GET_Model>, t: Throwable){
                    // 실패
                    Log.d("Log", t.message.toString())
                    Log.d("Log", "fail")
                }
            })
        }

        examplePostButton2.setOnClickListener {
            val data = PostModel(
                exInput0.text.toString(),   // id
                exInput2.text.toString(),   // pw
                exInput1.text.toString(),   // nick
            )
            api.post_users(data).enqueue(object : Callback<PostResult>{
                override fun onResponse(call: Call<PostResult>, response: Response<PostResult>) {
                    Log.d("Log", response.toString())
                    Log.d("Log", response.body().toString())
                    if(!response.body().toString().isEmpty())
                        textExample.setText(response.body().toString())
                }

                override fun onFailure(call: Call<PostResult>, t: Throwable) {
                    // 실패
                    Log.d("log", t.message.toString())
                    Log.d("log", "fail")
                }
            })
        }

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
        barBattey.setProgress(storageCondition)
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
        barDRcondition.setProgress(generationCondition)
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