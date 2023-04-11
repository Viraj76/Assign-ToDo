package com.example.assigntodo.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.assigntodo.R

import com.google.android.material.dialog.MaterialAlertDialogBuilder

object Config {
    private var dialog : AlertDialog? = null
    private var alertDialog : AlertDialog? = null
    fun showDialog(context: Context){
        dialog   = AlertDialog.Builder(context).setView(R.layout.progress_dialog).setCancelable(false).create()
        dialog!!.show()
    }

    fun hideDialog(){
        dialog?.dismiss()
    }



}