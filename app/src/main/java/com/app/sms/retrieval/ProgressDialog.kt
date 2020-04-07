package com.app.sms.retrieval

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

class ProgressDialog : DialogFragment() {


    companion object {
        fun shared(): ProgressDialog {
            val dialog = ProgressDialog()
            dialog.isCancelable = true
            return dialog
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.dialog_loading, container, true)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return view
    }


}