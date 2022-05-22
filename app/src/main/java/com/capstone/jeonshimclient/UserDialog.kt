package com.capstone.jeonshimclient

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_user.*

open class UserDialog(context: Context, intent: Intent) {
    // context는 그래프 띄울 때 넣을 용도
    private val dialog = Dialog(context)

    val item0 = intent.getStringExtra("item0")
    val item1 = intent.getIntExtra("item1", 0)
    val item2 = intent.getStringExtra("item2")
    val item3 = intent.getFloatExtra("item3", 0F)
    val item4 = intent.getFloatExtra("item4", 0F)

    fun userDig(context: Context) {
        dialog.show()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(androidx.core.R.layout.custom_dialog)

        //Dialog 크기 설정
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)


        dialog.setContentView(R.layout.dialog_user)

        dialog.text_edit_dialog_user.setText(item1.toString())
        dialog.text_edit_dialog_user2.setText(item2)
        dialog.text_edit_dialog_user3.setText(item3.toString())
        dialog.text_edit_dialog_user4.setText(item4.toString())

        dialog.text_start_graph.setOnClickListener {
            val intent_tograph = Intent(context,GraphDialog::class.java)
            intent_tograph.putExtra("user_name", item0)

            val graphDialog = GraphDialog(context, intent_tograph)
            graphDialog.graphDig(context)
        }
    }
}