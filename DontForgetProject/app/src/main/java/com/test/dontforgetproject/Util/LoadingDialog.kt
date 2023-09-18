package com.test.dontforgetproject.Util

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.TextView
import com.test.dontforgetproject.R

class LoadingDialog(context: Context) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_loading)
        setCancelable(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun setMessage(message: String) {
        val messageTextView = findViewById<TextView>(R.id.textView_dialogLoading)
        messageTextView.text = message
    }
}
