package com.example.penca.mainscreen

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.example.penca.R

class CustomDialog(context: Context) :Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.alert_dialog)
    }

}