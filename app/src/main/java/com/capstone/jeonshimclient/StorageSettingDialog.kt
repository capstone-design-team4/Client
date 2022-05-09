package com.capstone.jeonshimclient

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView

class StorageSettingDialog(context: Context) {
    private val dialog = Dialog(context)
    
    fun setDig(context: Context){
        dialog.show()

        dialog.setContentView(androidx.core.R.layout.custom_dialog)

        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        dialog.setContentView(R.layout.dialog_storagesetting)

        val seekBar = dialog.findViewById<SeekBar>(R.id.seekBar)
        val textview_progress = dialog.findViewById<TextView>(R.id.seekBarResult)

        val btnDone = dialog.findViewById<Button>(R.id.check)
        val btnCancel = dialog.findViewById<Button>(R.id.cancle)

        seekBar.max = 100
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress % 10 == 0)
                    textview_progress.text = "${progress.toString()}%"
                else {
                    seekBar?.setProgress((progress / 10) * 10)
                    textview_progress.text = "${progress.toString()}%"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        btnDone.setOnClickListener{
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }


}